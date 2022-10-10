package com.teamresourceful.resourcefulbees.common.item.locator;

import com.teamresourceful.resourcefulbees.client.gui.screen.locator.BeeLocatorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.WorldWorkerManager;
import org.jetbrains.annotations.NotNull;

public class BeeLocator extends Item {

    public BeeLocator(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (level.isClientSide) {
            clientOpenScreen(hand);
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @OnlyIn(Dist.CLIENT)
    private static void clientOpenScreen(InteractionHand hand) {
        Minecraft.getInstance().setScreen(new BeeLocatorScreen(hand));
    }

    public static void run(Player player, String bee, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack.getItem())) return;
        if (!(stack.getItem() instanceof BeeLocator)) return;
        WorldWorkerManager.addWorker(new BeeLocatorWorker(player, hand, bee, 100));
    }

}
