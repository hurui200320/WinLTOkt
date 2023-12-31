plugins {
    kotlin("jvm") version "1.9.21"
    `maven-publish`
}

group = "info.skyblond"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("--enable-preview")
}
tasks.withType<Test>().configureEach {
    jvmArgs("--enable-preview", "--enable-native-access=ALL-UNNAMED", "-Djava.library.path=${File(".").canonicalPath}")
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group.toString()
            artifactId = rootProject.name
            version = rootProject.version.toString()

            from(components["java"])
        }
    }
}
