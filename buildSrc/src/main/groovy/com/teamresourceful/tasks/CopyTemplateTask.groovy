package com.teamresourceful.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class CopyTemplateTask extends DefaultTask {

    @Input
    abstract MapProperty<String, Object> getVersionProperties()

    @InputFile
    abstract RegularFileProperty getSource()

    //can't set this to the root directory as a default value due to .gradle folder having a lock on windows
    //the lock causes hashing errors for checking if everything is up to date when executing the task
    //not sure if there's an alternative solution outside of targeting a specific file in the root directory
    @OutputDirectory
    final abstract File targetDir = project.buildDir

    @TaskAction
    def copyTemplate() {
        project.copy {
            from source.get()
            into targetDir
            rename { String file -> file.replace(".template", "") }
            expand(versionProperties.get())
        }
    }
}
