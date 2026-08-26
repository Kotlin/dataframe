# AGENTS.md — dataframe-geo

Guidance for this module. See the root `AGENTS.md` for repo-wide build/style/KoDEx rules; only module-specific
details are here.

## What this module is

`dataframe-geo` (artifact `dataframe-geo`, "GeoDataFrame API") — geographical/GIS support for Kotlin DataFrame.
**Experimental.** Depends on `api(core)`. The companion `dataframe-geo-jupyter` module adds the Kotlin Notebook
integration (see below). Both are declared as normal subprojects in the root `settings.gradle.kts`.

Package root: `org.jetbrains.kotlinx.dataframe.geo`.

## Public API surface

- `GeoDataFrame<T : WithGeometry>` (`GeoDataFrame.kt`) — wraps a `DataFrame<T>` + a nullable
  `CoordinateReferenceSystem crs`. Key members: `modify { }`, `applyCrs(targetCrs)` (JTS/GeoTools reprojection;
  default CRS is `EPSG:4326`/WGS 84), and companion `DEFAULT_CRS`.
- `WithGeometry.kt` — `@DataSchema` interfaces: `WithGeometry` (`geometry: Geometry`) and the specializations
  `WithPointGeometry`, `WithMultiPointGeometry`, `WithLineStringGeometry`, `WithMultiLineStringGeometry`,
  `WithPolygonGeometry`, `WithMultiPolygonGeometry`. Each has a checked-in generated `…$Extensions.kt`.
- `toGeo.kt` — `AnyFrame.toGeo(crs)` (view a frame that has a `geometry` column as a `GeoDataFrame`).
- `io/read.kt` — `readGeoJson` / `readShapefile` on **both** `GeoDataFrame.Companion` and `DataFrame.Companion`.
  Shapefile resolution handles `.shp`, `.shp.gz`, a directory containing `<dir>/<dir>.shp`, and remote URLs.
- `io/write.kt` — `writeGeoJson` / `writeShapefile` are extension functions **on a `GeoDataFrame<*>` instance**
  (not on any companion).
- `geotools/` — conversions to/from GeoTools `SimpleFeatureCollection`. `jts/` — geometry helpers (`bounds`,
  `geometryExtensions`, `toMulti`). `geocode/Geocoder.kt` — experimental `Geocoder.geocodeCountries(...)` (POSTs to
  a JetBrains geocoding endpoint via Ktor and parses GeoJSON).

## Dependencies & build gotchas

- **OSGeo repository must come *before* Maven Central** in `repositories { }` — GeoTools artifacts resolve from
  `https://repo.osgeo.org/repository/release`.
- **JAI exclusion.** A helper `excludeJaiCore()` strips `javax.media:jai_core` from every GeoTools dependency, and
  JAI is re-added separately (see the explanatory comment in `build.gradle.kts`). Preserve this when touching deps.
- Deps: GeoTools (`geotools.main`/`shapefile`/`geojson`/`referencing`/`epsg.hsql`), LocationTech **JTS**
  (`org.locationtech.jts.geom.*` is the geometry model), and Ktor client (for the geocoder).
- Registers a checked-in generated-accessors source root (`src/generated-dataschema-accessors/main/kotlin/`) —
  don't hand-edit those files. Uses `friendPaths` into `:core`.
- Applies the `kodex` plugin (KDocs preprocessed; generated sources are build output — see root `AGENTS.md`).
- Targets JDK 8 (`kotlinJvm8`); the README notes the Java-11 constraint that comes from the jupyter integration.

## Tests

- `src/test/kotlin/.../geo/io/IOTest.kt` is the main test (JUnit + `kotlin.test`; `testImplementation(projects.dataframeJson)`).
- Test data in `src/test/resources/`: `simple_points.geojson` and a full shapefile set
  `simple_points/simple_points.{shp,shx,dbf,prj,fix}`.
- **Two tests hit the network** (`readShapefileRemoteUrl`, `readShapefileDirectoryRemoteUrl`) fetching from a
  GitHub datasets repo — expect them to fail offline. Note also that GeoTools CRS equality can't always be compared
  directly (one assertion is intentionally commented out).
- Run: `./gradlew dataframe-geo:test` (add `-Pkotlin.dataframe.debug=true` to match CI).

## `dataframe-geo-jupyter`

Separate module (JDK 11, `jupyter.api`). Single file `jupyter/IntegrationGeo.kt` — a `JupyterIntegration` that adds
imports, registers the `WithGeometry` schemas (`useSchema<…>()`), and provides a `GeoDataFrame` renderer. It needs
`log4j` on the classpath for GeoTools. Registered via `processJupyterApiResources { libraryProducers = [...] }`.
