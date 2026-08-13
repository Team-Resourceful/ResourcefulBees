package com.teamresourceful.resourcefulbees.common.lib.util;

import com.teamresourceful.resourcefulbees.api.registry.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.items.honey.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class FluidUtils {
    private FluidUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static boolean fillBottle(ResourceHandler<FluidResource> tank, Player player, InteractionHand hand) {
        for (int i = 0; i < tank.size(); i++) {
            if (fillBottle(tank, i, player, hand)) return true;
        }
        return false;
    }

    public static boolean fillBottle(ResourceHandler<FluidResource> tank, int index, Player player, InteractionHand hand) {
        if (tank.getAmountAsInt(index) < BeeConstants.HONEY_PER_BOTTLE) return false;

        FluidResource resource = tank.getResource(index);
        Item bottle = HoneyRegistry.get().getBottleFromFluid(resource);
        if (bottle == Items.AIR) return false;

        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = tank.extract(index, resource, BeeConstants.HONEY_PER_BOTTLE, transaction);
            if (extracted != BeeConstants.HONEY_PER_BOTTLE) return false;
            transaction.commit();
        }
        givePlayerBottle(bottle.getDefaultInstance(), SoundEvents.BOTTLE_FILL, player, hand);
        return true;
    }

    public static boolean emptyBottle(ResourceHandler<FluidResource> tank, Player player, InteractionHand hand) {
        FluidResource resource = HoneyRegistry.get().getResourceFromBottle(player.getItemInHand(hand).getItem());
        if (resource.isEmpty()) return false;

        try(Transaction transaction = Transaction.openRoot()) {
            int inserted = tank.insert(resource, BeeConstants.HONEY_PER_BOTTLE, transaction);
            if (inserted != BeeConstants.HONEY_PER_BOTTLE) return false;
            transaction.commit();
        }
        givePlayerBottle(Items.GLASS_BOTTLE.getDefaultInstance(), SoundEvents.BOTTLE_EMPTY, player, hand);
        return true;
    }

    private static void givePlayerBottle(ItemStack result, SoundEvent soundEvent, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);

        if (!player.isCreative()) {
            if (heldStack.getCount() == 1) {
                player.setItemInHand(hand, result);
                playBottleSound(soundEvent, player);
                return;
            }

            heldStack.shrink(1);
        }

        if (!player.addItem(result)) {
            player.drop(result, false);
        }

        playBottleSound(soundEvent, player);
    }

    private static void playBottleSound(SoundEvent soundEvent, Player player) {
        player.level().playSound(null, player.blockPosition(), soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public static InteractionResult fillOrEmptyBottle(ResourceHandler<FluidResource> tank, Player player, InteractionHand hand) {
        Item item = player.getItemInHand(hand).getItem();

        if (item instanceof BottleItem) {
            return fillBottle(tank, player, hand)
                    ? InteractionResult.SUCCESS_SERVER
                    : InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (item == Items.HONEY_BOTTLE || item instanceof CustomHoneyBottleItem) {
            return emptyBottle(tank, player, hand)
                    ? InteractionResult.SUCCESS_SERVER
                    : InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }
}
