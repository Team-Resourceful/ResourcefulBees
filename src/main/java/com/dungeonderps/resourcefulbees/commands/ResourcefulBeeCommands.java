package com.dungeonderps.resourcefulbees.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class ResourcefulBeeCommands
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("resourcefulbees")
                .then(Commands.literal("help")
                        .executes(context -> help(context.getSource().asPlayer()))));
    }

    private static int help(ServerPlayerEntity player) {
        player.sendMessage(new StringTextComponent("Resourceful Bees is currently in alpha all information needed is found on our curseforge page: https://www.curseforge.com/minecraft/mc-mods/resourceful-bees"));
        return Command.SINGLE_SUCCESS;
    }
}