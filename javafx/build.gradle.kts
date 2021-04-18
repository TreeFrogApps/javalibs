import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.treefrogapps.javafx"
project.extra["name"] = "javafx"
version = "2.5.0"

val javaFxVersion = "16"

javafx {
    version = javaFxVersion
    modules("javafx.controls", "javafx.fxml")
}

dependencies {

    // Kotlin
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))

    // RxJava
    implementation("io.reactivex.rxjava3:rxjava:3.0.0")
    implementation("com.treefrogapps.rxjava3:rxjava3:1.1.0")

    // Dagger
    val daggerVersion = "2.34"
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")

    // Logging
    implementation("org.apache.logging.log4j:log4j-core:2.8.2")

    testImplementation("junit:junit:4.13.1")
}