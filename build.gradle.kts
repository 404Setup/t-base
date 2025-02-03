import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `java-library`
    idea
    signing

    id("com.vanniktech.maven.publish") version "0.30.0"
}

group = "one.tranic"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.opencollab.dev/main/")
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2")

    compileOnly("org.geysermc.geyser:api:2.4.2-SNAPSHOT")
    compileOnly("org.geysermc.floodgate:api:2.2.3-SNAPSHOT")

    compileOnly("com.google.guava:guava:33.3.0-jre")
    compileOnly("it.unimi.dsi:fastutil:8.5.15")
    compileOnly("net.kyori:adventure-api:4.18.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.18.0")
}

val targetJavaVersion = 21

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.name()
    options.release = targetJavaVersion
}

tasks.withType<ProcessResources> {
    filteringCharset = Charsets.UTF_8.name()
}

val apiAndDocs: Configuration by configurations.creating {
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(DocsType.SOURCES))
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
    }
}

configurations.api {
    extendsFrom(apiAndDocs)
}

mavenPublishing {
    coordinates(group as String, "t-base", version as String)

    pom {
        name.set("TLIB Base")
        description.set("Basic Development Library")
        inceptionYear.set("2025")
        url.set("https://github.com/404Setup/t-base")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("404")
                name.set("404Setup")
                url.set("https://github.com/404Setup")
            }
        }
        scm {
            url.set("https://github.com/404Setup/t-base")
            connection.set("scm:git:git://github.com/404Setup/t-base.git")
            developerConnection.set("scm:git:ssh://git@github.com/404Setup/t-base.git")
        }

        /*withXml {
            val root = asNode()
            val nodes = root["dependencies"] as NodeList
            if (nodes.isNotEmpty()) {
                root.remove(nodes.first() as Node)
            }
        }*/
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
}
