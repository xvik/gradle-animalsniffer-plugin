rootProject.name = "KotlinProject2"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        mavenLocal()
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
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

include(":composeApp")
include(":server")
include(":shared")