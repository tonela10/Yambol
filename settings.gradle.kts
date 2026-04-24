pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Yambol"
include(":app")
include(":composeApp")

// Phase 0 skeleton modules — code will be migrated here in Phase 1+
include(":core:domain")
include(":core:data")
include(":core:designsystem")
include(":feature:home")
include(":feature:training")
include(":feature:trainingDetails")
include(":feature:createTrain")
include(":feature:createTeam")
include(":feature:playerCard")
include(":feature:statistics")
include(":feature:profile")
include(":feature:login")
