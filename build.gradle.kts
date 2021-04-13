import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    `java-library`
    kotlin("jvm") version "1.4.32" apply false
    kotlin("kapt") version "1.4.32" apply false
    id("org.openjfx.javafxplugin") version "0.0.8" apply false
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        jcenter()
    }
}

subprojects {

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "maven-publish")
    apply(plugin ="org.openjfx.javafxplugin")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")


    repositories {
        mavenCentral()
        jcenter()
        maven {
            name = "GitHubPackages"
            url = uri(project.findProperty("gpr_url_java_libs") as String)
            credentials {
                username = project.findProperty("gpr_user") as String
                password = project.findProperty("gpr_public_key") as String
            }
        }
    }

    publishing {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri(project.findProperty("gpr_url_java_libs") as String)
                credentials {
                    username = project.findProperty("gpr_user") as String
                    password = project.findProperty("gpr_key") as String
                }
            }
        }

        publications {
            register("gprRelease", MavenPublication::class) {
                from(components["java"])
            }
        }
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    val javaVersion = "11"

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = javaVersion
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    tasks.withType<Test> {
        testLogging { events = mutableSetOf(PASSED, SKIPPED, FAILED) }
    }

    extensions.getByType<KaptExtension>().run {
        includeCompileClasspath = false
    }
}