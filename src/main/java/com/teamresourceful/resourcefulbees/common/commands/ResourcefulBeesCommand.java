package com.teamresourceful.resourcefulbees.common.commands;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class ResourcefulBeesCommand {

    private ResourcefulBeesCommand() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal(ModConstants.MOD_ID)
                .then(TemplateCommand.register())
                .then(BeepediaCommand.register())
                .then(GenerateCommand.register())
        );
    }
}
