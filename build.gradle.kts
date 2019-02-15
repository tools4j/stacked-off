plugins {
    kotlin("jvm") version "1.3.10"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compile("org.apache.lucene:lucene-core:7.6.0")
    compile("org.apache.lucene:lucene-queryparser:7.6.0")
    compile("com.fasterxml.jackson.core:jackson-core:2.9.8")
    compile("com.fasterxml.jackson.core:jackson-annotations:2.9.8")
    compile("com.fasterxml.jackson.core:jackson-databind:2.9.8")
    compile("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.9.8")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")

    testCompile("org.assertj:assertj-core:3.11.1")
    testCompile("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testCompile("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}
