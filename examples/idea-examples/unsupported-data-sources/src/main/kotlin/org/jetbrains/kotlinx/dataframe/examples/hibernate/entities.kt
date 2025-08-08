package org.jetbrains.kotlinx.dataframe.examples.hibernate

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

@Entity
@Table(name = "Albums")
class AlbumsEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AlbumId")
    var albumId: Int? = null,

    @Column(name = "Title", length = 160, nullable = false)
    var title: String = "",

    @Column(name = "ArtistId", nullable = false)
    var artistId: Int = 0,
)

@Entity
@Table(name = "Artists")
class ArtistsEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ArtistId")
    var artistId: Int? = null,

    @Column(name = "Name", length = 120, nullable = false)
    var name: String = "",
)

@Entity
@Table(name = "Customers")
class CustomersEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CustomerId")
    var customerId: Int? = null,

    @Column(name = "FirstName", length = 40, nullable = false)
    var firstName: String = "",

    @Column(name = "LastName", length = 20, nullable = false)
    var lastName: String = "",

    @Column(name = "Company", length = 80)
    var company: String? = null,

    @Column(name = "Address", length = 70)
    var address: String? = null,

    @Column(name = "City", length = 40)
    var city: String? = null,

    @Column(name = "State", length = 40)
    var state: String? = null,

    @Column(name = "Country", length = 40)
    var country: String? = null,

    @Column(name = "PostalCode", length = 10)
    var postalCode: String? = null,

    @Column(name = "Phone", length = 24)
    var phone: String? = null,

    @Column(name = "Fax", length = 24)
    var fax: String? = null,

    @Column(name = "Email", length = 60, nullable = false)
    var email: String = "",

    @Column(name = "SupportRepId")
    var supportRepId: Int? = null,
)

// DataFrame schema to get typed accessors similar to Exposed example
@DataSchema
data class DfCustomers(
    @ColumnName("Address") val address: String?,
    @ColumnName("City") val city: String?,
    @ColumnName("Company") val company: String?,
    @ColumnName("Country") val country: String?,
    @ColumnName("CustomerId") val customerId: Int,
    @ColumnName("Email") val email: String,
    @ColumnName("Fax") val fax: String?,
    @ColumnName("FirstName") val firstName: String,
    @ColumnName("LastName") val lastName: String,
    @ColumnName("Phone") val phone: String?,
    @ColumnName("PostalCode") val postalCode: String?,
    @ColumnName("State") val state: String?,
    @ColumnName("SupportRepId") val supportRepId: Int?,
)
