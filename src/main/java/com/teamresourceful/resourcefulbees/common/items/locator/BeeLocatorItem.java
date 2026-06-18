//package com.teamresourceful.resourcefulbees.common.items.locator;
//
//import com.teamresourceful.resourcefulbees.common.world.workers.LevelWorkManager;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import org.jetbrains.annotations.NotNull;
//
//public class BeeLocatorItem extends Item {
//
//    public BeeLocatorItem(Properties p_41383_) {
//        super(p_41383_);
//    }
//
//    @Override
//    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
//        if (level.isClientSide()) {
//            //BeeLocatorScreen.openScreen(player, hand);
//        }
//        return InteractionResult.SUCCESS_SERVER;
//    }
//
//    public static void run(Player player, String bee, int slot) {
//        ItemStack stack = player.getInventory().getItem(slot);
//        if (!player.getAbilities().instabuild && player.getCooldowns().isOnCooldown(stack)) return;
//        if (!(stack.getItem() instanceof BeeLocatorItem)) return;
//        LevelWorkManager.addWork(new BeeLocatorWorker(player, slot, bee, 100));
//    }
//
//}
