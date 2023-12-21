package com.teamresourceful.resourcefulbees.common.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.teamresourceful.resourcefulbees.common.commands.arguments.BeeArgument;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.BeepediaSavedData;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public final class BeepediaCommand {

    private BeepediaCommand() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("beepedia")
            .requires(stack -> stack.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.literal("add")
                    .then(BeeArgument.argument()).executes(ctx -> add(ctx, false))
                    .then(Commands.literal("*").executes(ctx -> add(ctx, true)))
                )
                .then(Commands.literal("remove")
                    .then(BeeArgument.argument()).executes(ctx -> remove(ctx, false))
                    .then(Commands.literal("*").executes(ctx -> remove(ctx, true)))
                )
            );
    }

    private static int add(CommandContext<CommandSourceStack> context, boolean all) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        if (all) {
            BeepediaSavedData.addBees(player, BeeArgument.BEES);
        } else {
            String bee = BeeArgument.get(context);
            BeepediaSavedData.removeBee(player, bee);
        }
        return 1;
    }

    private static int remove(CommandContext<CommandSourceStack> context, boolean all) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        if (all) {
            BeepediaSavedData.clearBees(player);
        } else {
            String bee = BeeArgument.get(context);
            BeepediaSavedData.removeBee(player, bee);
        }
        return 1;
    }



}
