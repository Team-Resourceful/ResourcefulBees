package com.teamresourceful.resourcefulbees.common.item.locator;

import com.teamresourceful.resourcefulbees.client.gui.screen.locator.BeeLocatorScreen;
import com.teamresourceful.resourcefulbees.platform.common.workers.LevelWorkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BeeLocatorItem extends Item {

    public BeeLocatorItem(Properties p_41383_) {
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
        if (!player.getAbilities().instabuild && player.getCooldowns().isOnCooldown(stack.getItem())) return;
        if (!(stack.getItem() instanceof BeeLocatorItem)) return;
        LevelWorkManager.addWork(new BeeLocatorWorker(player, hand, bee, 100));
    }

}
