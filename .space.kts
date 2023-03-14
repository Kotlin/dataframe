import java.nio.file.Files
import java.nio.file.Paths

job("Polaris Artifact") {
    container("registry.jetbrains.team/p/sa/public/qodana-jvm:polaris") {
        kotlinScript { api ->
            val spaceProjectKey = System.getenv("JB_SPACE_PROJECT_KEY")
            val repoName = System.getenv("JB_SPACE_GIT_REPOSITORY_NAME")
            val initialPath = System.getenv("JB_SPACE_FILE_SHARE_PATH")
            val tid: String = api.space().projects.getProject(ProjectIdentifier.Key(spaceProjectKey)) {
                repos {
                    id()
                    name()
                }
            }.repos.first { it.name == repoName }.id!!
            Files.write(Paths.get("$initialPath/tid"), tid.encodeToByteArray())
        }
    }
    container("registry.jetbrains.team/p/sa/public/qodana-jvm:polaris")
}
