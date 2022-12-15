package com.teamresourceful.resourcefulbees.common.commands;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.platform.common.events.CommandRegisterEvent;
import net.minecraft.commands.Commands;

public class ResourcefulBeesCommand {

    private ResourcefulBeesCommand() {
        throw new UtilityClassError();
    }

    public static void registerCommand(CommandRegisterEvent event) {
        event.dispatcher().register(Commands.literal(ModConstants.MOD_ID)
                .then(TemplateCommand.register())
                .then(BeepediaCommand.register())
        );
    }
}
