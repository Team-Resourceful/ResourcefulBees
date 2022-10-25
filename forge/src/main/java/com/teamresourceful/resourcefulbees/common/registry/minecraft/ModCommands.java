package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.common.commands.ResourcefulBeesCommand;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;

public final class ModCommands {

    private ModCommands() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(ModCommands::registerCommands);
    }

    private static void registerCommands(final RegisterCommandsEvent ev) {
        ResourcefulBeesCommand.registerSubCommands(ev.getDispatcher());
    }
}
