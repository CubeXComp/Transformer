pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        
            google()
            mavenCentral()

        mavenCentral()
        maven {
            url = uri("https://repository.aspose.com/repo/")
        }
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://repository.aspose.com/repo/")
        }

    }
}

rootProject.name = "Transformer"
include(":app")
