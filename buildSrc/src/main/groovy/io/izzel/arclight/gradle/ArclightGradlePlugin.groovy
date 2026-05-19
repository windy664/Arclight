package io.izzel.arclight.gradle

import io.izzel.arclight.gradle.extension.ArclightExtension
import io.izzel.arclight.gradle.runnable.FileDownloader
import io.izzel.arclight.gradle.runnable.SpigotBuilder
import io.izzel.arclight.gradle.tasks.ResetSpigotTask
import net.fabricmc.loom.configuration.mods.dependency.LocalMavenHelper
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.apache.commons.io.IOUtils

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

class ArclightGradlePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def arclight = project.extensions.create('arclight', ArclightExtension, project)

        def arclightRepo = arclight.cacheDir.resolve('arclight_repo')
        project.repositories.maven {
            name = 'Arclight Spigot Repo'
            url = arclightRepo
        }

        project.afterEvaluate {
            setupSpigot(project, arclightRepo)
        }
    }

    private static def setupSpigot(Project project, Path arclightRepo) {
        def arclight = project.extensions.getByName('arclight') as ArclightExtension


        def spigotDeps = arclightRepo.resolve("io/izzel/arclight/generated/spigot/${arclight.mcVersion}")
        def spigotMapped = spigotDeps.resolve("spigot-${arclight.mcVersion}-mapped.jar")
        def spigotDeobf = spigotDeps.resolve("spigot-${arclight.mcVersion}-deobf.jar")

        def buildMeta = arclight.cacheDir.resolve('spigot_version.json')
        def rev = arclight.mcVersion
        if (arclight.spigotReversion) {
            rev = arclight.spigotReversion
        }

        if (Files.exists(spigotDeobf) && !arclight.updatingSpigot) {
            return
        }

        project.logger.lifecycle("Setup for Spigot ${arclight.mcVersion}(${arclight.spigotReversion})")
        def newBuildMeta = IOUtils.toString(new URI("https://hub.spigotmc.org/versions/${rev}.json").toURL(), StandardCharsets.UTF_8)
        if (Files.exists(buildMeta)) {
            var built = Files.readString(buildMeta)
            if (built == newBuildMeta) {
                if (Files.exists(spigotDeobf)) {
                    project.logger.lifecycle(":spigot build cache valid, using it")
                    project.logger.debug(built)
                    return
                }
            }
        }

        def buildSpigotWorkDir = arclight.cacheDir.resolve('arclight_cache/buildtools')

        FileUtils.deleteDirectory(buildSpigotWorkDir.toFile())
        Files.createDirectories(buildSpigotWorkDir)

        project.logger.lifecycle(":step1 download build tools")
        def buildToolsJar = buildSpigotWorkDir.resolve('BuildTools.jar')
        def downloadBuildTools = new FileDownloader("https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar", buildToolsJar)
        downloadBuildTools.run()

        project.logger.lifecycle(":step2 build spigot")
        def spigotBuilder = project.getObjects().newInstance(SpigotBuilder)
        spigotBuilder.buildToolsJar = buildToolsJar
        spigotBuilder.workDir = buildSpigotWorkDir
        spigotBuilder.outputDir = spigotDeps
        spigotBuilder.minecraftVersion = arclight.mcVersion
        spigotBuilder.reversion = arclight.spigotReversion
        spigotBuilder.run()

        new LocalMavenHelper("io.izzel.arclight.generated", "spigot", arclight.mcVersion, null, arclightRepo).savePom()

        project.logger.lifecycle(":step3 reset spigot jar")
        def resetSpigot = new ResetSpigotTask(project)
        resetSpigot.ssJar = new File(buildSpigotWorkDir.toFile(), 'BuildData/bin/SpecialSource.jar')
        resetSpigot.inJar = project.file(spigotDeps.resolve("spigot-${arclight.mcVersion}.jar"))
        resetSpigot.outJar = project.file(spigotMapped)
        resetSpigot.outDeobf = project.file(spigotDeobf)
        resetSpigot.inAt = arclight.accessTransformer
        resetSpigot.inExtraSrg = arclight.extraMapping
        resetSpigot.run()

        Files.writeString(buildMeta, newBuildMeta)
    }
}
