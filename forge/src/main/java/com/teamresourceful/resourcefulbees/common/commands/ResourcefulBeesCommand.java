package com.teamresourceful.resourcefulbees.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ResourcefulBeesCommand {

    private ResourcefulBeesCommand() {
        throw new UtilityClassError();
    }

    public static void registerSubCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        //CommandBuildContext buildContext = new CommandBuildContext(RegistryAccess.BUILTIN.get());
        //buildContext.missingTagAccessPolicy(CommandBuildContext.MissingTagAccessPolicy.RETURN_EMPTY);
        LiteralArgumentBuilder<CommandSourceStack> cmd = Commands.literal(ResourcefulBees.MOD_ID)
                .then(TemplateCommand.register());
        dispatcher.register(cmd);
    }
}
