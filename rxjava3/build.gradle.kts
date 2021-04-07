import org.jetbrains.kotlin.config.KotlinCompilerVersion

group = "com.treefrogapps.rxjava3"
project.extra["name"] = "rxjava3"
version = "1.1.0"

dependencies {

    // Kotlin
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))

    // RxJava
    implementation("io.reactivex.rxjava3:rxjava:3.0.0")

    // Logging
    implementation("org.apache.logging.log4j:log4j-core:2.8.2")

    testImplementation("junit:junit:4.13.1")
}