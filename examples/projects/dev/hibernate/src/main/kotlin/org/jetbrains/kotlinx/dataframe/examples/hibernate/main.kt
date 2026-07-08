package org.jetbrains.kotlinx.dataframe.examples.hibernate

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaDelete
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import org.hibernate.FlushMode
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider
import org.hibernate.jpa.HibernatePersistenceConfiguration
import org.hibernate.tool.schema.Action
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.size

/**
 * Example showing Kotlin DataFrame with Hibernate ORM + H2 in-memory DB.
 * Mirrors logic from the Exposed example: load data, convert to DataFrame, group/describe, write back.
 */
fun main() {
    val sessionFactory: SessionFactory = buildSessionFactory()

    sessionFactory.insertSampleData()

    val df = sessionFactory.loadCustomersAsDataFrame()

    // Pure Hibernate + Criteria API approach for counting customers per country
    println("=== Hibernate + Criteria API Approach ===")
    sessionFactory.countCustomersPerCountryWithHibernate()

    println("\n=== DataFrame Approach ===")
    df.analyzeAndPrintResults()

    sessionFactory.replaceCustomersFromDataFrame(df)

    sessionFactory.close()
}

private fun SessionFactory.insertSampleData() {
    inTransaction { session ->
        // a few artists and albums (minimal, not used further; just demo schema)
        val artist1 = ArtistsEntity(name = "AC/DC")
        val artist2 = ArtistsEntity(name = "Queen")
        session.persist(artist1)
        session.persist(artist2)

        session.persist(AlbumsEntity(title = "High Voltage", artist = artist1))
        session.persist(AlbumsEntity(title = "Back in Black", artist = artist1))
        session.persist(AlbumsEntity(title = "A Night at the Opera", artist = artist2))
        // customers we'll analyze using DataFrame
        session.persist(
            CustomersEntity(
                firstName = "John",
                lastName = "Doe",
                email = "john.doe@example.com",
                country = "USA",
            ),
        )
        session.persist(
            CustomersEntity(
                firstName = "Jane",
                lastName = "Smith",
                email = "jane.smith@example.com",
                country = "USA",
            ),
        )
        session.persist(
            CustomersEntity(
                firstName = "Alice",
                lastName = "Wang",
                email = "alice.wang@example.com",
                country = "Canada",
            ),
        )
    }
}

private fun SessionFactory.loadCustomersAsDataFrame(): DataFrame<DfCustomers> =
    withReadOnlyTransaction { session ->
        val criteriaBuilder: CriteriaBuilder = session.criteriaBuilder
        val criteriaQuery: CriteriaQuery<CustomersEntity> = criteriaBuilder.createQuery(CustomersEntity::class.java)
        val root: Root<CustomersEntity> = criteriaQuery.from(CustomersEntity::class.java)
        criteriaQuery.select(root)

        session.createSelectionQuery(criteriaQuery)
            .resultList
            .map { c ->
                DfCustomers(
                    address = c.address,
                    city = c.city,
                    company = c.company,
                    country = c.country,
                    customerId = c.customerId ?: error("Loaded customer must have a generated id"),
                    email = c.email,
                    fax = c.fax,
                    firstName = c.firstName,
                    lastName = c.lastName,
                    phone = c.phone,
                    postalCode = c.postalCode,
                    state = c.state,
                    supportRepId = c.supportRepId,
                )
            }
            .toDataFrame()
            // This cast is a workaround to turn `DataFrame<DfCustomers_XX>` into `DataFrame<DfCustomers>`:
            // TODO We plan to improve this:
            //   1) This `cast` should not be needed. `List<DfCustomers>` should become `DataFrame<DfCustomers>` #1880
            //   2) `cast` should not need `verify = false` here.
            //      This is a bug related to `@ColumnName` in the compiler plugin.
            .cast<DfCustomers>(verify = false)
    }

/** DTO used for aggregation projection. */
private data class CountryCountDto(val country: String?, val customerCount: Long)

/**
 * **Hibernate + Criteria API:**
 * - ✅ Database-level aggregation (efficient)
 * - ✅ Type-safe queries
 * - ❌ Verbose syntax
 * - ❌ Limited to SQL-like operations
 */
private fun SessionFactory.countCustomersPerCountryWithHibernate() {
    withReadOnlyTransaction { session ->
        val cb = session.criteriaBuilder
        val cq: CriteriaQuery<CountryCountDto> = cb.createQuery(CountryCountDto::class.java)
        val root: Root<CustomersEntity> = cq.from(CustomersEntity::class.java)
        val countryPath = root.get<String?>("country")
        val idPath = root.get<Int?>("customerId")

        val countExpr = cb.count(idPath)

        cq.select(
            cb.construct(
                CountryCountDto::class.java,
                countryPath, // country
                countExpr, // customerCount
            ),
        )
        cq.groupBy(countryPath)
        cq.orderBy(cb.desc(countExpr))

        val results = session.createSelectionQuery(cq).resultList
        results.forEach { dto ->
            println("${dto.country}: ${dto.customerCount} customers")
        }
    }
}

/**
 * **DataFrame approach: **
 * - ✅ Rich analytical operations
 * - ✅ Fluent, readable API
 * - ✅ Flexible data transformations
 * - ❌ In-memory processing (less efficient for large datasets)
 */
private fun DataFrame<DfCustomers>.analyzeAndPrintResults() {
    println(size())

    // same operation as Exposed example: customers per country
    groupBy { country }.count()
        .sortByDesc { count }
        .print(columnTypes = true, borders = true)

    // general statistics
    describe()
        .print(columnTypes = true, borders = true)
}

private fun SessionFactory.replaceCustomersFromDataFrame(df: DataFrame<DfCustomers>) =
    inTransaction { session ->
        val criteriaBuilder: CriteriaBuilder = session.criteriaBuilder
        val criteriaDelete: CriteriaDelete<CustomersEntity> =
            criteriaBuilder.createCriteriaDelete(CustomersEntity::class.java)
        criteriaDelete.from(CustomersEntity::class.java)
        session.createMutationQuery(criteriaDelete).executeUpdate()

        df.asSequence().forEach { row ->
            session.persist(row.toCustomersEntity())
        }
    }

private fun DataRow<DfCustomers>.toCustomersEntity(): CustomersEntity =
    CustomersEntity(
        customerId = null, // let the DB generate
        firstName = this.firstName,
        lastName = this.lastName,
        company = this.company,
        address = this.address,
        city = this.city,
        state = this.state,
        country = this.country,
        postalCode = this.postalCode,
        phone = this.phone,
        fax = this.fax,
        email = this.email,
        supportRepId = this.supportRepId,
    )

/** Read-only transaction helper for SELECT queries to minimize overhead. */
private inline fun <T> SessionFactory.withReadOnlyTransaction(crossinline block: (session: Session) -> T): T =
    fromTransaction { session ->
        // Minimize overhead for read operations
        session.isDefaultReadOnly = true
        session.hibernateFlushMode = FlushMode.MANUAL
        block(session)
    }

private fun buildSessionFactory(): SessionFactory =
    HibernatePersistenceConfiguration("hibernate-example")
        .managedClasses(CustomersEntity::class.java, ArtistsEntity::class.java, AlbumsEntity::class.java)
        .jdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1")
        .jdbcCredentials("sa", "")
        .schemaToolingAction(Action.CREATE_DROP)
        .showSql(true, true, true)
        .property("hibernate.connection.provider_class", HikariCPConnectionProvider::class.java)
        .property("hibernate.hikari.maximumPoolSize", 5)
        .createEntityManagerFactory()
