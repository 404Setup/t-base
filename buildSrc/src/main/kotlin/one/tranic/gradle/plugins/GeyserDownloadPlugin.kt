package one.tranic.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.net.URL

class GeyserDownloadPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val downloadGeyserTask = project.tasks.register("downloadGeyser") {
            description = "Downloads Geyser and Floodgate Spigot plugins"
            group = "geyser"

            doLast {
                val libsDir = project.file("libs")

                if (!libsDir.exists()) {
                    libsDir.mkdirs()
                    println("Created libs directory")
                }

                val artifacts = mapOf(
                    "Geyser-Spigot.jar" to "https://download.geysermc.org/v2/projects/geyser/versions/latest/builds/latest/downloads/spigot",
                    "Floodgate-Spigot.jar" to "https://download.geysermc.org/v2/projects/floodgate/versions/latest/builds/latest/downloads/spigot"
                )

                artifacts.forEach { (fileName, downloadUrl) ->
                    val jarFile = File(libsDir, fileName)

                    if (!jarFile.exists()) {
                        println("Downloading $fileName...")

                        try {
                            URL(downloadUrl).openStream().use { input ->
                                jarFile.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                            println("Successfully downloaded $fileName")
                        } catch (e: Exception) {
                            println("Failed to download $fileName: ${e.message}")
                            throw e
                        }
                    }
                }
            }
        }

        project.tasks.named("compileJava").configure {
            dependsOn(downloadGeyserTask)
        }
    }
}