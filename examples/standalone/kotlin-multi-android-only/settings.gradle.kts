rootProject.name = "KotlinProject"
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
        // for github package (used on CI)
        maven {
            url  = uri("https://maven.pkg.github.com/xvik/gradle-animalsniffer-plugin")
            credentials {
                username = extra.has("gpr.user").let { if (it) extra["gpr.user"] as String else System.getenv("USERNAME")}
                password = extra.has("gpr.key").let { if (it) extra["gpr.key"] as String else System.getenv("TOKEN")}
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")