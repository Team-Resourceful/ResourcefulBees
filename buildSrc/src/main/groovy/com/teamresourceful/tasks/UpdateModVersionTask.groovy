package com.teamresourceful.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

//TODO consider adding a Version pojo/record with methods for getting major/minor/etc
abstract class UpdateModVersionTask extends DefaultTask {

    @Input
    abstract Property<String> getInitialMCVersion()

    @Input
    abstract Property<String> getCurrentMCVersion()

    @Input
    abstract Property<String> getPatchVersion()

    @Input
    abstract Property<String> getVersionPostfix()

    @Input
    abstract Property<String> getPostfixBuild()

    @Input
    abstract Property<String> getBuildTime()

    @Input
    abstract Property<Boolean> getIsNightly()

    @OutputFile
    final abstract RegularFileProperty outputFile = project.rootProject.objects.fileProperty()
            .convention(project.rootProject.layout.projectDirectory.file("version.properties"))

    @TaskAction
    def updateVersion() {
        //set initial and current mc versions
        def init = tokenizeVersionString initialMCVersion.get()
        def cur = tokenizeVersionString currentMCVersion.get()

        //determine mod major and minor
        def modMajor = cur[1] - init[1] + 1
        def modMinor = cur[1] == init[1] ? (cur[2] as int) - (init[2] as int) : cur[2]

        //create mod version string and apply postfix if available
        def modVersion = "$modMajor.$modMinor.${patchVersion.get()}"

        //add postfix if one exists
        if (!versionPostfix.get().isEmpty()) {
            modVersion += "-${versionPostfix.get()}"
            if (!postfixBuild.get().isEmpty()) {
                modVersion += ".${postfixBuild.get()}"
            }
        }

        //add build time if nightly version
        if (isNightly.get()) {
            def bt = !buildTime.get().isEmpty() ?: System.currentTimeSeconds()
            modVersion += "+$bt"
        }

        println("Mod version has been updated: $modVersion")
        outputFile.get().asFile.text = "version=$modVersion"
    }

    private static def tokenizeVersionString(String string) {
        def cur = string.tokenize(".")
        if (cur.size() == 2) cur.add("00")
        else if (cur.size() == 3 && cur[2].length() == 1) cur[2] = "0" + cur[2]
        cur
    }
}
