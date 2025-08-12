package org.jetbrains.kotlinx.dataframe.examples.hibernate

import jakarta.persistence.Tuple
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaDelete
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.Root
import org.hibernate.FlushMode
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.asSequence
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
    withTransaction { session ->
        // a few artists and albums (minimal, not used further; just demo schema)
        val artist1 = ArtistsEntity(name = "AC/DC")
        val artist2 = ArtistsEntity(name = "Queen")
        session.persist(artist1)
        session.persist(artist2)
        session.flush()

        session.persist(AlbumsEntity(title = "High Voltage", artistId = artist1.artistId!!))
        session.persist(AlbumsEntity(title = "Back in Black", artistId = artist1.artistId!!))
        session.persist(AlbumsEntity(title = "A Night at the Opera", artistId = artist2.artistId!!))
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

private fun SessionFactory.loadCustomersAsDataFrame(): DataFrame<DfCustomers> {
    return withReadOnlyTransaction { session ->
        val criteriaBuilder: CriteriaBuilder = session.criteriaBuilder
        val criteriaQuery: CriteriaQuery<CustomersEntity> = criteriaBuilder.createQuery(CustomersEntity::class.java)
        val root: Root<CustomersEntity> = criteriaQuery.from(CustomersEntity::class.java)
        criteriaQuery.select(root)

        session.createQuery(criteriaQuery)
            .resultList
            .map { c ->
                DfCustomers(
                    address = c.address,
                    city = c.city,
                    company = c.company,
                    country = c.country,
                    customerId = c.customerId ?: -1,
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
    }
}

/** DTO used for aggregation projection. */
private data class CountryCountDto(
    val country: String,
    val customerCount: Long,
)

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

        val countryPath = root.get<String>("country")
        val idPath = root.get<Long>("customerId")

        val countExpr = cb.count(idPath)

        cq.select(
            cb.construct(
                CountryCountDto::class.java,
                countryPath,  // country
                countExpr,    // customerCount
            ),
        )
        cq.groupBy(countryPath)
        cq.orderBy(cb.desc(countExpr))

        val results = session.createQuery(cq).resultList
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
        .sortByDesc { "count"<Int>() }
        .print(columnTypes = true, borders = true)

    // general statistics
    describe()
        .print(columnTypes = true, borders = true)
}

private fun SessionFactory.replaceCustomersFromDataFrame(df: DataFrame<DfCustomers>) {
    withTransaction { session ->
        val criteriaBuilder: CriteriaBuilder = session.criteriaBuilder
        val criteriaDelete: CriteriaDelete<CustomersEntity> =
            criteriaBuilder.createCriteriaDelete(CustomersEntity::class.java)
        criteriaDelete.from(CustomersEntity::class.java)

        session.createMutationQuery(criteriaDelete).executeUpdate()
    }

    withTransaction { session ->
        df.asSequence().forEach { row ->
            session.persist(row.toCustomersEntity())
        }
    }
}

private fun DataRow<DfCustomers>.toCustomersEntity(): CustomersEntity {
    return CustomersEntity(
        customerId = null, // let DB generate
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
}

private inline fun <T> SessionFactory.withSession(block: (session: org.hibernate.Session) -> T): T {
    return openSession().use(block)
}

private inline fun SessionFactory.withTransaction(block: (session: org.hibernate.Session) -> Unit) {
    withSession { session ->
        session.beginTransaction()
        try {
            block(session)
            session.transaction.commit()
        } catch (e: Exception) {
            session.transaction.rollback()
            throw e
        }
    }
}

/** Read-only transaction helper for SELECT queries to minimize overhead. */
private inline fun <T> SessionFactory.withReadOnlyTransaction(block: (session: org.hibernate.Session) -> T): T {
    return withSession { session ->
        session.beginTransaction()
        // Minimize overhead for read operations
        session.isDefaultReadOnly = true
        session.hibernateFlushMode = FlushMode.MANUAL
        try {
            val result = block(session)
            session.transaction.commit()
            result
        } catch (e: Exception) {
            session.transaction.rollback()
            throw e
        }
    }
}


private fun buildSessionFactory(): SessionFactory {
    // Load configuration from resources/hibernate/hibernate.cfg.xml
    return Configuration().configure("hibernate/hibernate.cfg.xml").buildSessionFactory()
}
