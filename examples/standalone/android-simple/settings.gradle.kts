pluginManagement {
    repositories {
        mavenLocal()
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        // for github package (used on CI)
        maven {
            url  = uri("https://maven.pkg.github.com/xvik/gradle-animalsniffer-plugin")
            credentials {
                username = extra["gpr.user"]?.toString() ?: System.getenv("USERNAME")
                password = extra["gpr.key"]?.toString() ?: System.getenv("TOKEN")
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "My Application"
include(":app")
