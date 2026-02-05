@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.testSets
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.companyLocation: DataColumn<String> @JvmName("DsSalaries_companyLocation") get() = this["company_location"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.companyLocation: String @JvmName("DsSalaries_companyLocation") get() = this["company_location"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.companyLocation: DataColumn<String?> @JvmName("NullableDsSalaries_companyLocation") get() = this["company_location"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.companyLocation: String? @JvmName("NullableDsSalaries_companyLocation") get() = this["company_location"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.companySize: DataColumn<String> @JvmName("DsSalaries_companySize") get() = this["company_size"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.companySize: String @JvmName("DsSalaries_companySize") get() = this["company_size"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.companySize: DataColumn<String?> @JvmName("NullableDsSalaries_companySize") get() = this["company_size"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.companySize: String? @JvmName("NullableDsSalaries_companySize") get() = this["company_size"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.employeeResidence: DataColumn<String> @JvmName("DsSalaries_employeeResidence") get() = this["employee_residence"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.employeeResidence: String @JvmName("DsSalaries_employeeResidence") get() = this["employee_residence"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.employeeResidence: DataColumn<String?> @JvmName("NullableDsSalaries_employeeResidence") get() = this["employee_residence"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.employeeResidence: String? @JvmName("NullableDsSalaries_employeeResidence") get() = this["employee_residence"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.employmentType: DataColumn<String> @JvmName("DsSalaries_employmentType") get() = this["employment_type"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.employmentType: String @JvmName("DsSalaries_employmentType") get() = this["employment_type"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.employmentType: DataColumn<String?> @JvmName("NullableDsSalaries_employmentType") get() = this["employment_type"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.employmentType: String? @JvmName("NullableDsSalaries_employmentType") get() = this["employment_type"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.experienceLevel: DataColumn<String> @JvmName("DsSalaries_experienceLevel") get() = this["experience_level"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.experienceLevel: String @JvmName("DsSalaries_experienceLevel") get() = this["experience_level"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.experienceLevel: DataColumn<String?> @JvmName("NullableDsSalaries_experienceLevel") get() = this["experience_level"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.experienceLevel: String? @JvmName("NullableDsSalaries_experienceLevel") get() = this["experience_level"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.jobTitle: DataColumn<String> @JvmName("DsSalaries_jobTitle") get() = this["job_title"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.jobTitle: String @JvmName("DsSalaries_jobTitle") get() = this["job_title"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.jobTitle: DataColumn<String?> @JvmName("NullableDsSalaries_jobTitle") get() = this["job_title"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.jobTitle: String? @JvmName("NullableDsSalaries_jobTitle") get() = this["job_title"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.remoteRatio: DataColumn<Int> @JvmName("DsSalaries_remoteRatio") get() = this["remote_ratio"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.remoteRatio: Int @JvmName("DsSalaries_remoteRatio") get() = this["remote_ratio"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.remoteRatio: DataColumn<Int?> @JvmName("NullableDsSalaries_remoteRatio") get() = this["remote_ratio"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.remoteRatio: Int? @JvmName("NullableDsSalaries_remoteRatio") get() = this["remote_ratio"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.salary: DataColumn<Int> @JvmName("DsSalaries_salary") get() = this["salary"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.salary: Int @JvmName("DsSalaries_salary") get() = this["salary"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.salary: DataColumn<Int?> @JvmName("NullableDsSalaries_salary") get() = this["salary"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.salary: Int? @JvmName("NullableDsSalaries_salary") get() = this["salary"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.salaryCurrency: DataColumn<String> @JvmName("DsSalaries_salaryCurrency") get() = this["salary_currency"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.salaryCurrency: String @JvmName("DsSalaries_salaryCurrency") get() = this["salary_currency"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.salaryCurrency: DataColumn<String?> @JvmName("NullableDsSalaries_salaryCurrency") get() = this["salary_currency"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.salaryCurrency: String? @JvmName("NullableDsSalaries_salaryCurrency") get() = this["salary_currency"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.salaryInUsd: DataColumn<Int> @JvmName("DsSalaries_salaryInUsd") get() = this["salary_in_usd"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.salaryInUsd: Int @JvmName("DsSalaries_salaryInUsd") get() = this["salary_in_usd"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.salaryInUsd: DataColumn<Int?> @JvmName("NullableDsSalaries_salaryInUsd") get() = this["salary_in_usd"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.salaryInUsd: Int? @JvmName("NullableDsSalaries_salaryInUsd") get() = this["salary_in_usd"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.untitled: DataColumn<Int> @JvmName("DsSalaries_untitled") get() = this["untitled"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.untitled: Int @JvmName("DsSalaries_untitled") get() = this["untitled"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.untitled: DataColumn<Int?> @JvmName("NullableDsSalaries_untitled") get() = this["untitled"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.untitled: Int? @JvmName("NullableDsSalaries_untitled") get() = this["untitled"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.workYear: DataColumn<Int> @JvmName("DsSalaries_workYear") get() = this["work_year"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries>.workYear: Int @JvmName("DsSalaries_workYear") get() = this["work_year"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.workYear: DataColumn<Int?> @JvmName("NullableDsSalaries_workYear") get() = this["work_year"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.DsSalaries?>.workYear: Int? @JvmName("NullableDsSalaries_workYear") get() = this["work_year"] as Int?
