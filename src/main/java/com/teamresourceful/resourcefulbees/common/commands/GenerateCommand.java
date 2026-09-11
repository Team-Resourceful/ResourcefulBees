package com.teamresourceful.resourcefulbees.common.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.teamresourceful.resourcefulbees.client.data.LangGenerator;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.Permissions;

public class GenerateCommand {

    private GenerateCommand() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("generate")
                .then(registerRecipeCommand())
                .then(registerTradeCommand())
                .then(registerLangCommand());
    }

    // -------------------------------------------------------------------------
    // Command registration
    // -------------------------------------------------------------------------

    private static ArgumentBuilder<CommandSourceStack, ?> registerLangCommand() {
        return Commands.literal("lang")
                .requires(stack -> stack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .executes(LangGenerator::generateEnglishLang);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerRecipeCommand() {
        return  Commands.literal("recipe")
                .then(Commands.literal("honeycomb")
                        .requires(stack -> stack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(RecipeCommand::generateHoneycombRecipes))
                .then(Commands.literal("honey")
                        .requires(stack -> stack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(RecipeCommand::generateHoneyRecipes))
                .then(Commands.literal("breeder")
                        .requires(stack -> stack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(RecipeCommand::generateBreederRecipes))
                .then(Commands.literal("chamber")
                        .requires(stack -> stack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(RecipeCommand::generateChamberRecipes));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerTradeCommand() {
        return Commands.literal("trade")
                .then(Commands.literal("beekeeper")
                        .requires(stack -> stack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(TradeCommand::generateBeekeeperTrades)
        );
    }
}