package com.teamresourceful.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

//TODO not a fan of having this as a separate task like this but I need to move on for now
abstract class SetNightlyVersionTask extends DefaultTask {

    @OutputFile
    final abstract RegularFileProperty outputFile = project.rootProject.objects.fileProperty()
            .convention(project.rootProject.layout.projectDirectory.file("version.properties"))

    //TODO successive calls will cause the build time to continue being appended without first removing any existing build time
    // Adding a Version pojo/record could make this easier to handle by having each version segment stored in their own respective
    // fields. If for some reason successive calls need to be made then the data in version.properties will need to be manually modified.
    @TaskAction
    def updateVersion() {
        def current = GetModVersionTask.currentVersionFromFile
        def buildTime = System.currentTimeSeconds()
        def nightlyVersion = "$current+$buildTime"
        println("Nightly version has been set: $nightlyVersion")
        outputFile.get().asFile.text = "version=$nightlyVersion"
    }
}
