plugins {
    kotlin("jvm") version "1.8.0"
}

group = "makine"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    configurations["runtimeClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("dev.kord:kord-core:0.12.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("com.mohamedrejeb.ksoup:ksoup-html:0.1.4")
    implementation("com.github.kittinunf.fuel:fuel:3.0.0-alpha1")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}