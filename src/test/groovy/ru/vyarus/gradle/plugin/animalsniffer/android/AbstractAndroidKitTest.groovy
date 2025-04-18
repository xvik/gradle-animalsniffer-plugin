package ru.vyarus.gradle.plugin.animalsniffer.android

import org.gradle.testkit.runner.GradleRunner
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest

/**
 * @author Vyacheslav Rusakov
 * @since 21.11.2024
 */
abstract class AbstractAndroidKitTest extends AbstractKitTest {

    @Override
    def setup() {
        // google plugin repository required
        file("settings.gradle") << """
            pluginManagement {
                repositories {
                    mavenLocal()
                    gradlePluginPortal()
                    google()
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
            """
    }

    // required for application plugin
    protected void generateManifest(String path = 'src/main') {
        file("$path/AndroidManifest.xml") << """<?xml version="1.0" encoding="utf-8"?>
<manifest/>
"""
    }

    @Override
    GradleRunner applyCommonConfiguration(GradleRunner runner) {
        runner.withEnvironment(['ANDROID_HOME': detectSdk()])
    }

    private String detectSdk() {
        String home = System.getProperty('user.home')
        List<String> candidates = [
                System.getenv('ANDROID_HOME'),
                home + (isWin ? '\\AppData\\Local\\Android\\Sdk' : '/Android/Sdk')
        ]
        if (isWin) {
            candidates.add('C:\\Program Files (x86)\\Android\\android-sdk')
        }
        for (String path : candidates) {
            if (path && new File(path).exists()) {
                return path
            }
        }
        return null
    }
}
