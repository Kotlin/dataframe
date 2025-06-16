package org.jetbrains.kotlinx.dataframe.examples.exposed

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.generateDataClasses
import org.jetbrains.kotlinx.dataframe.api.print

object Albums : Table() {
    val albumId: Column<Int> = integer("AlbumId").autoIncrement()
    val title: Column<String> = varchar("Title", 160)
    val artistId: Column<Int> = integer("ArtistId")

    override val primaryKey = PrimaryKey(albumId)
}

object Artists : Table() {
    val artistId: Column<Int> = integer("ArtistId").autoIncrement()
    val name: Column<String> = varchar("Name", 120)

    override val primaryKey = PrimaryKey(artistId)
}

object Customers : Table() {
    val customerId: Column<Int> = integer("CustomerId").autoIncrement()
    val firstName: Column<String> = varchar("FirstName", 40)
    val lastName: Column<String> = varchar("LastName", 20)
    val company: Column<String?> = varchar("Company", 80).nullable()
    val address: Column<String?> = varchar("Address", 70).nullable()
    val city: Column<String?> = varchar("City", 40).nullable()
    val state: Column<String?> = varchar("State", 40).nullable()
    val country: Column<String?> = varchar("Country", 40).nullable()
    val postalCode: Column<String?> = varchar("PostalCode", 10).nullable()
    val phone: Column<String?> = varchar("Phone", 24).nullable()
    val fax: Column<String?> = varchar("Fax", 24).nullable()
    val email: Column<String> = varchar("Email", 60)
    val supportRepId: Column<Int?> = integer("SupportRepId").nullable()

    override val primaryKey = PrimaryKey(customerId)
}

/**
 * Exposed requires you to provide [Table] instances to
 * provide type-safe access to your columns and data.
 *
 * While DataFrame can infer types at runtime, which is enough for Kotlin Notebook,
 * to get type safe access at compile time, we need to define a [@DataSchema][DataSchema].
 *
 * This is what we created the [toDataFrameSchema] function for!
 */
fun main() {
    val (schema, nameNormalizer) = Customers.toDataFrameSchemaWithNameNormalizer()

    // checking whether the schema is converted correctly.
    // schema.print()

    // printing a @DataSchema data class to copy-paste into the code.
    // we use a NameNormalizer to let DataFrame generate the same accessors as in the Table
    // while keeping the correct column names
    schema.generateDataClasses(
        name = "DfCustomers",
        nameNormalizer = nameNormalizer,
    ).print()
}

// created by Customers.toDataFrameSchema()
// The same can be done for the other tables
@DataSchema
data class DfCustomers(
    @ColumnName("Address")
    val address: String?,
    @ColumnName("City")
    val city: String?,
    @ColumnName("Company")
    val company: String?,
    @ColumnName("Country")
    val country: String?,
    @ColumnName("CustomerId")
    val customerId: Int,
    @ColumnName("Email")
    val email: String,
    @ColumnName("Fax")
    val fax: String?,
    @ColumnName("FirstName")
    val firstName: String,
    @ColumnName("LastName")
    val lastName: String,
    @ColumnName("Phone")
    val phone: String?,
    @ColumnName("PostalCode")
    val postalCode: String?,
    @ColumnName("State")
    val state: String?,
    @ColumnName("SupportRepId")
    val supportRepId: Int?,
)
