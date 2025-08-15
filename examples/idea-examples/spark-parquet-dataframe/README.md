# spark-parquet-dataframe

This example runs on Windows without winutils.exe. We configure Spark/Hadoop to use the pure Java local filesystem (file://) and disable native permission checks, so you don't need to install any extra binaries.

This example demonstrates:

1) Reading a CSV (California housing) with local Apache Spark into a Spark DataFrame
2) Printing the Spark DataFrame and writing it to a temporary Parquet directory
3) Reading that Parquet back into a Kotlin DataFrame using the Apache Arrow-based reader
4) Printing head() of the Kotlin DataFrame
5) Training a simple regression model with Spark MLlib (Linear Regression)
6) Exporting the model summary (coefficients and intercept) to Parquet
7) Reading the model summary Parquet with Kotlin DataFrame and printing it

## About the dataset (housing.csv)

About this file

Suggest Edits
1. longitude: A measure of how far west a house is; a higher value is farther west

2. latitude: A measure of how far north a house is; a higher value is farther north

3. housingMedianAge: Median age of a house within a block; a lower number is a newer building

4. totalRooms: Total number of rooms within a block

5. totalBedrooms: Total number of bedrooms within a block

6. population: Total number of people residing within a block

7. households: Total number of households, a group of people residing within a home unit, for a block

8. medianIncome: Median income for households within a block of houses (measured in tens of thousands of US Dollars)

9. medianHouseValue: Median house value for households within a block (measured in US Dollars)

10. oceanProximity: Location of the house w.r.t ocean/sea

The CSV file is located at `examples/housing.csv` in the repository root.
