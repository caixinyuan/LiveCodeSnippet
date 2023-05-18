buildscript {
    repositories {
        maven { url=uri("https://maven.aliyun.com/repository/public/") }
        mavenCentral()
        maven { url=uri("https://plugins.gradle.org/m2/") }
        maven { url=uri("https://oss.sonatype.org/content/repositories/releases/") }
        maven { url=uri("https://dl.bintray.com/jetbrains/intellij-plugin-service") }
        maven { url=uri("https://dl.bintray.com/jetbrains/intellij-third-party-dependencies/") }
    }
    dependencies {
        classpath("org.jetbrains.intellij.plugins:gradle-intellij-plugin:0.7.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
    }
}


plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.0"
}

group = "com.cxy"
version = "1.0.4"

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
}
dependencies {
    implementation ("cn.hutool:hutool-core:5.8.9")
    implementation ("cn.hutool:hutool-http:5.8.9")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2021.3.3")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf("org.intellij.plugins.markdown"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("213")
        untilBuild.set("223.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
