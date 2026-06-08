package org.jetbrains.kotlinx.dataframe.example.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.io.DataFrameHtmlData
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.io.renderToString
import org.jetbrains.kotlinx.dataframe.io.toStandaloneHtml

@Composable
actual fun DataFrameContent(modifier: Modifier) {
    DataFrameTable(getDataFrame(), modifier = modifier)
}

@Preview
@Composable
fun PreviewDataFrameContent() {
    DataFrameContent()
}

@Suppress("ktlint:standard:argument-list-wrapping")
private fun getDataFrame(): AnyFrame =
    DataFrame.readJson("/mnt/data/Projects/dataframe2/examples/notebooks/github/jetbrains.json")
        .cast<DataEntry>()
        .single()
        .repos
        .take(3)

private fun getDataFrameOutputAsString(): String =
    getDataFrame().renderToString(
        borders = true,
        columnTypes = true,
    )

private fun getDataFrameOutputAsHtml(): DataFrameHtmlData = getDataFrame().toStandaloneHtml()

@DataSchema
interface DataEntry {
    val members: List<Members>
    val name: String
    val repos: List<Repos>
    val url: String

    @DataSchema(isOpen = false)
    interface Members {
        @ColumnName("gravatar_id")
        val gravatarId: String
        val id: Int
        val login: String

        @ColumnName("node_id")
        val nodeId: String

        @ColumnName("site_admin")
        val siteAdmin: Boolean
        val type: String
        val url: String

        @ColumnName("user_view_type")
        val userViewType: String
    }

    @DataSchema(isOpen = false)
    interface Contributors {
        val contributions: Int?

        @ColumnName("gravatar_id")
        val gravatarId: String?
        val id: Int?
        val login: String?
        val message: String?

        @ColumnName("node_id")
        val nodeId: String?

        @ColumnName("site_admin")
        val siteAdmin: Boolean?
        val type: String?
        val url: String?

        @ColumnName("user_view_type")
        val userViewType: String?
    }

    @DataSchema(isOpen = false)
    interface License {
        val key: String?
        val name: String?

        @ColumnName("node_id")
        val nodeId: String?

        @ColumnName("spdx_id")
        val spdxId: String?
        val url: String?
    }

    @DataSchema(isOpen = false)
    interface Permissions {
        val admin: Boolean
        val maintain: Boolean
        val pull: Boolean
        val push: Boolean
        val triage: Boolean
    }

    @DataSchema(isOpen = false)
    interface Repos {
        @ColumnName("allow_forking")
        val allowForking: Boolean
        val archived: Boolean
        val contributors: List<Contributors>

        @ColumnName("created_at")
        val createdAt: String

        @ColumnName("custom_properties")
        val customProperties: Nothing?

        @ColumnName("default_branch")
        val defaultBranch: String
        val description: String?
        val disabled: Boolean
        val fork: Boolean
        val forks: Int

        @ColumnName("forks_count")
        val forksCount: Int

        @ColumnName("full_name")
        val fullName: String

        @ColumnName("has_discussions")
        val hasDiscussions: Boolean

        @ColumnName("has_downloads")
        val hasDownloads: Boolean

        @ColumnName("has_issues")
        val hasIssues: Boolean

        @ColumnName("has_pages")
        val hasPages: Boolean

        @ColumnName("has_projects")
        val hasProjects: Boolean

        @ColumnName("has_wiki")
        val hasWiki: Boolean
        val homepage: String?

        @ColumnName("html_url")
        val htmlUrl: String
        val id: Int

        @ColumnName("is_template")
        val isTemplate: Boolean
        val language: String?
        val license: License
        val name: String

        @ColumnName("node_id")
        val nodeId: String

        @ColumnName("open_issues")
        val openIssues: Int

        @ColumnName("open_issues_count")
        val openIssuesCount: Int
        val permissions: Permissions
        val private: Boolean

        @ColumnName("pushed_at")
        val pushedAt: String
        val size: Int

        @ColumnName("stargazers_count")
        val stargazersCount: Int
        val topics: List<String>

        @ColumnName("updated_at")
        val updatedAt: String
        val url: String
        val visibility: String
        val watchers: Int

        @ColumnName("watchers_count")
        val watchersCount: Int

        @ColumnName("web_commit_signoff_required")
        val webCommitSignoffRequired: Boolean
    }
}
