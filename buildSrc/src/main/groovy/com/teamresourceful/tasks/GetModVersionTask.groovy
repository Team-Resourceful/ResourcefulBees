package com.teamresourceful.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class GetModVersionTask extends DefaultTask {

    static String getCurrentVersionFromFile() throws IOException {
        new File("./version.properties").text.replace("version=","")
    }

    @SuppressWarnings('GrMethodMayBeStatic')
    @Internal
    @TaskAction
    String getModVersion() throws IOException {
        String currentVersion = getCurrentVersionFromFile()
        println currentVersion
        currentVersion
    }
}
