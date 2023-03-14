package com.teamresourceful

import com.teamresourceful.tasks.GetModVersionTask
import org.gradle.api.provider.Property

abstract class ModVersionExtension {
    abstract Property<String> getInitialMCVersion()
    abstract Property<String> getCurrentMCVersion()
    abstract Property<String> getPatchVersion()
    abstract Property<String> getVersionPostfix()
    abstract Property<String> getPostfixBuild()
    abstract Property<String> getBuildTime()
    abstract Property<Boolean> getIsNightly()

    static String getVersion() throws IOException {
        return GetModVersionTask.getCurrentVersionFromFile()
    }
}
