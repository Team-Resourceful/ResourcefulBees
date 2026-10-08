package com.teamresourceful.resourcefulbees.common.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.teamresourceful.resourcefulbees.common.commands.arguments.BeeArgument;
import com.teamresourceful.resourcefulbees.common.registries.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModAttachments;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.BeeDiscoveryData;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

public final class BeepediaCommand {

    private BeepediaCommand() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("beepedia").requires(stack -> stack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("add").then(BeeArgument.argument().executes(ctx -> add(ctx, false)))
                                .then(Commands.literal("*").executes(ctx -> add(ctx, true))))
                        .then(Commands.literal("remove").then(BeeArgument.argument().executes(ctx -> remove(ctx, false)))
                                .then(Commands.literal("*").executes(ctx -> remove(ctx, true))))
                );
    }

    private static int add(CommandContext<CommandSourceStack> context, boolean all) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        BeeDiscoveryData data = player.getData(ModAttachments.BEE_DISCOVERY);

        Identifier bee = all ? null : BeeArgument.get(context);
        boolean changed = false;

        if (all) {
            for (Identifier id : BeeRegistry.getRegistry().getBeeTypes()) {
                changed |= data.discover(id);
            }
        } else {
            changed = data.discover(bee);
        }

        if (!changed) {
            Component message;

            if (all) {
                message = Component.literal("All bees are already discovered for ").append(player.getDisplayName());
            } else {
                message = Component.literal("Bee is already discovered: ")
                        .append(Component.literal(bee.toString()))
                        .append(" for ")
                        .append(player.getDisplayName());
            }

            context.getSource().sendFailure(message);
            return 0;
        }

        player.syncData(ModAttachments.BEE_DISCOVERY);

        Component message;

        if (all) {
            message = Component.literal("Discovered all bees for ").append(player.getDisplayName());
        } else {
            message = Component.literal("Discovered ")
                    .append(Component.literal(bee.toString()))
                    .append(" for ")
                    .append(player.getDisplayName());
        }

        context.getSource().sendSuccess(() -> message, true);

        return 1;
    }

    private static int remove(CommandContext<CommandSourceStack> context, boolean all) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        BeeDiscoveryData data = player.getData(ModAttachments.BEE_DISCOVERY);

        Identifier bee = all ? null : BeeArgument.get(context);
        boolean changed;

        if (all) {
            changed = data.size() > 0;

            if (changed) {
                data.clear();
            }
        } else {
            changed = data.forget(bee);
        }

        if (!changed) {
            Component message;

            if (all) {
                message = Component.literal("No bee discoveries to remove for ")
                        .append(player.getDisplayName());
            } else {
                message = Component.literal("Bee was not discovered: ")
                        .append(Component.literal(bee.toString()))
                        .append(" for ")
                        .append(player.getDisplayName());
            }

            context.getSource().sendFailure(message);
            return 0;
        }

        player.syncData(ModAttachments.BEE_DISCOVERY);

        Component message;

        if (all) {
            message = Component.literal("Removed all bee discoveries for ")
                    .append(player.getDisplayName());
        } else {
            message = Component.literal("Removed discovery of ")
                    .append(Component.literal(bee.toString()))
                    .append(" for ")
                    .append(player.getDisplayName());
        }

        context.getSource().sendSuccess(() -> message, true);

        return 1;
    }
}
