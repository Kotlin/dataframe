package org.jetbrains.kotlinx.dataframe.testSets

import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

// Dataset from https://www.kaggle.com/datasets/ruchi798/data-science-job-salaries
@Suppress("unused")
@DataSchema
interface DsSalaries {
    @ColumnName("company_location")
    val companyLocation: String
    @ColumnName("company_size")
    val companySize: String
    @ColumnName("employee_residence")
    val employeeResidence: String
    @ColumnName("employment_type")
    val employmentType: String
    @ColumnName("experience_level")
    val experienceLevel: String
    @ColumnName("job_title")
    val jobTitle: String
    @ColumnName("remote_ratio")
    val remoteRatio: Int
    val salary: Int
    @ColumnName("salary_currency")
    val salaryCurrency: String
    @ColumnName("salary_in_usd")
    val salaryInUsd: Int
    val untitled: Int
    @ColumnName("work_year")
    val workYear: Int
}
