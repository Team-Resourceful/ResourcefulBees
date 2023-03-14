package com.teamresourceful

import com.teamresourceful.tasks.GetModVersionTask
import com.teamresourceful.tasks.SetNightlyVersionTask
import com.teamresourceful.tasks.UpdateModVersionTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ModVersionPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("modVersion", ModVersionExtension)

        project.tasks.register("updateModVersion", UpdateModVersionTask) {
            description = "Remember to pass in the patch, postfix, postfix version and nightly/build time via command line as necessary!"
            group = "versioning"
            initialMCVersion = project.modVersion.initialMCVersion
            currentMCVersion = project.modVersion.currentMCVersion
            patchVersion = project.modVersion.patchVersion
            versionPostfix = project.modVersion.versionPostfix
            postfixBuild = project.modVersion.postfixBuild
            isNightly = project.modVersion.isNightly
            buildTime = project.modVersion.buildTime
        }

        project.tasks.register("getModVersion", GetModVersionTask) {
            description = "Returns the current mod version"
            group = "versioning"
        }

        project.tasks.register("setNightlyVersion", SetNightlyVersionTask) {
            description = "Sets the nightly build version to current version + build time"
            group = "versioning"
        }
    }
}
