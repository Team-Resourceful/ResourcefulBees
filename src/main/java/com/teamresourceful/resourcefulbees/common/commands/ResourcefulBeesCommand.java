package com.teamresourceful.resourcefulbees.common.commands;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.platform.common.events.CommandRegisterEvent;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.commands.Commands;

public final class ResourcefulBeesCommand {

    private ResourcefulBeesCommand() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static void registerCommand(CommandRegisterEvent event) {
        event.dispatcher().register(Commands.literal(ModConstants.MOD_ID)
                .then(TemplateCommand.register())
                .then(BeepediaCommand.register())
        );
    }
}
