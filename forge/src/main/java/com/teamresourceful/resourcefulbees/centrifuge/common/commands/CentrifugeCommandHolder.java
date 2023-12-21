package com.teamresourceful.resourcefulbees.centrifuge.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.Collection;

public final class CentrifugeCommandHolder {

    private CentrifugeCommandHolder() throws UtilityClassException {
        throw new UtilityClassException();
    }

    private static CommandDispatcher<CentrifugeCommandSource> dispatcher = generateCommands();

    public static CommandDispatcher<CentrifugeCommandSource> generateCommands() {
        final CommandDispatcher<CentrifugeCommandSource> newDispatcher = new CommandDispatcher<>();
        newDispatcher.register(literal("help").executes(ctx -> {
            Collection<String> commands = newDispatcher.getSmartUsage(newDispatcher.getRoot(), ctx.getSource()).values();
            ctx.getSource().sendMessage("Centrifuge Commands: ");
            for (String command : commands) ctx.getSource().sendMessage(" - " + command);
            return Command.SINGLE_SUCCESS;
        }));
        newDispatcher.register(literal("say").then(argument("message", StringArgumentType.greedyString()).executes(ctx -> {
            ctx.getSource().sendMessage(StringArgumentType.getString(ctx, "message"));
            return Command.SINGLE_SUCCESS;
        })));
        newDispatcher.register(literal("ping").executes(ctx -> {
            ctx.getSource().sendMessage("pong");
            return Command.SINGLE_SUCCESS;
        }));
        if (!FMLLoader.isProduction()) {
            newDispatcher.register(literal("reload").executes(ctx -> {
                dispatcher = generateCommands();
                return Command.SINGLE_SUCCESS;
            }));
        }
        return newDispatcher;
    }

    public static LiteralArgumentBuilder<CentrifugeCommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <T> RequiredArgumentBuilder<CentrifugeCommandSource, T> argument(String pName, ArgumentType<T> pType) {
        return RequiredArgumentBuilder.argument(pName, pType);
    }

    public static void callDispatcher(String data, CentrifugeCommandSource source) {
        try {
            dispatcher.execute(data, source);
        } catch (CommandSyntaxException e) {
            source.sendError(e.getMessage());
        }
    }
}
