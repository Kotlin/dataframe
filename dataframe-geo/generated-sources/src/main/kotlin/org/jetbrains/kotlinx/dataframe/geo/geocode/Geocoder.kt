package org.jetbrains.kotlinx.dataframe.geo.geocode

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.annotations.ApiStatus.Experimental
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.geo.GeoDataFrame
import org.jetbrains.kotlinx.dataframe.geo.toGeo
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.io.geojson.GeoJsonReader

/**
 * Experimental geo coding utility.
 */
@Experimental
public object Geocoder {

    private val url = "https://geo2.datalore.jetbrains.com/map_data/geocoding"

    private fun countryQuery(country: String) =
        """
         {
        "region_query_names" : [ "$country" ],
        "region_query_countries" : null,
        "region_query_states" : null,
        "region_query_counties" : null,
        "ambiguity_resolver" : {
          "ambiguity_resolver_ignoring_strategy" : null,
          "ambiguity_resolver_box" : null,
          "ambiguity_resolver_closest_coord" : null
        }
        }
        """.trimIndent()

    private fun geocodeQuery(countries: List<String>) =
        """
{
  "version" : 3,
  "mode" : "by_geocoding",
  "feature_options" : [ "limit", "position", "centroid" ],
  "resolution" : null,
  "view_box" : null,
  "fetched_ids" : null,
  "region_queries" : [ 
  ${countries.joinToString(",\n") { countryQuery(it) }}
   ],
  "scope" : [ ],
  "level" : "country",
  "namesake_example_limit" : 10,
  "allow_ambiguous" : false
}
        """.trimIndent()

    private fun idsQuery(ids: List<String>) =
        """
        {"version": 3, 
        "mode": "by_id", 
        "feature_options": ["boundary"], 
        "resolution": 5, 
        "view_box": null, 
        "fetched_ids": null, 
        "ids": [${ids.joinToString(", ") { "\"" + it + "\"" }}]}
        """.trimIndent()

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                },
            )
        }
    }

    public fun geocodeCountries(countries: List<String>): GeoDataFrame<*> {
        val query = geocodeQuery(countries)
        val foundNames = mutableListOf<String>()
        val geometries = mutableListOf<Geometry>()
        runBlocking {
            val responseString = client.post(url) {
                contentType(ContentType.Application.Json)
                // headers[HttpHeaders.AcceptEncoding] = "gzip"
                setBody(query)
            }.bodyAsText()
            val ids = mutableListOf<String>()

            Json.parseToJsonElement(responseString).jsonObject["data"]!!.jsonObject["answers"]!!.jsonArray.forEach {
                it.jsonObject["features"]!!.jsonArray.single().jsonObject.also {
                    foundNames.add(it["name"]!!.jsonPrimitive.content)
                    ids.add(it["id"]!!.jsonPrimitive.content)
                }
            }
            val idsQuery = idsQuery(ids)

            val responseStringGeometries = client.post(url) {
                contentType(ContentType.Application.Json)
                // headers[HttpHeaders.AcceptEncoding] = "gzip"
                setBody(idsQuery)
            }.bodyAsText()

            val geoJsonReader = GeoJsonReader(GeometryFactory())
            Json.parseToJsonElement(
                responseStringGeometries,
            ).jsonObject["data"]!!.jsonObject["answers"]!!.jsonArray.forEach {
                it.jsonObject["features"]!!.jsonArray.single().jsonObject.also {
                    val boundary = it["boundary"]!!.jsonPrimitive.content
                    geometries.add(geoJsonReader.read(boundary))
                }
            }
        }
        return dataFrameOf(
            "country" to countries,
            "foundName" to foundNames,
            "geometry" to geometries,
        ).toGeo()
    }
}
