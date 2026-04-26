plugins {
    kotlin("jvm") version "2.0.21"
    antlr
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.antlr:antlr4-runtime:4.13.2")
    antlr("org.antlr:antlr4:4.13.2")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}

tasks.generateGrammarSource {
    arguments = arguments + listOf("-visitor", "-long-messages")
}

sourceSets {
    main {
        antlr {
            setSrcDirs(listOf("grammar"))
        }
    }
}

tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.compileTestKotlin {
    dependsOn(tasks.generateTestGrammarSource)
}
