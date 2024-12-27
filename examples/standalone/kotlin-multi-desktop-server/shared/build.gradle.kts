
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.animalsniffer)
}

animalsniffer {
    isDebug = false
    // only show errors
    isIgnoreFailures = true
}

kotlin {
    jvm()
    
    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here
        }
    }
}

dependencies {
    "signature"("org.codehaus.mojo.signature:java16-sun:1.0@signature")
}

