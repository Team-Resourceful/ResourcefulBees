//package com.teamresourceful.resourcefulbees.common.items;
//
//import com.teamresourceful.resourcefulbees.common.entities.CustomBeeEntityType;
//import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.BeepediaSavedData;
//import com.teamresourceful.resourcefulbees.common.lib.util.BeepediaUtils;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import org.jetbrains.annotations.NotNull;
//
//public class BeepediaItem extends Item {
//
//    public BeepediaItem(Properties properties) {
//        super(properties);
//    }
//
//    @Override
//    public @NotNull InteractionResult use(Level level, Player player, @NotNull InteractionHand hand) {
//        ItemStack itemstack = player.getItemInHand(hand);
//        if (level.isClientSide()) {
//            BeepediaUtils.loadBeepedia(itemstack, player);
//        } else {
//            //NetworkHandler.NETWORK.sendToPlayer(SyncBeepediaPacket.of(player), player); //todo no .of method anymore
//        }
//        return InteractionResult.SUCCESS_SERVER;
//    }
//
//    @Override
//    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
//        if (entity.getType() instanceof CustomBeeEntityType<?> beeType) {
//            if (player.level().isClientSide()) {
//                return InteractionResult.PASS;
//            } else {
//                BeepediaSavedData.addBee(player, beeType.getBeeType());
//            }
//            player.setItemInHand(hand, stack);
//            return InteractionResult.SUCCESS;
//        }
//        return super.interactLivingEntity(stack, player, entity, hand);
//    }
//
///*    @Override
//    public @NotNull Component getName(ItemStack stack) {
//        if (stack.hasTag() && stack.getTag() != null && !stack.getTag().isEmpty()) {
//            if (stack.getTag().getBoolean(NBTConstants.Beepedia.CREATIVE)) return ItemTranslations.CREATIVE_BEEPEDIA.withStyle(ChatFormatting.LIGHT_PURPLE);
//            if (stack.getTag().getBoolean(NBTConstants.Beepedia.COMPLETE)) return Component.literal("✦ ").withStyle(ChatFormatting.GREEN).append(super.getName(stack).copy().withStyle(ChatFormatting.WHITE));
//        }
//        return super.getName(stack);
//    }
//
//    @Override
//    @Environment(EnvType.CLIENT)
//    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
//        super.appendHoverText(stack, level, tooltip, flag);
//        tooltip.add(ItemTranslations.INFO_BEEPEDIA.withStyle(ChatFormatting.GREEN));
//        if (stack.hasTag() && stack.getTag() != null && !stack.getTag().isEmpty()) {
//            boolean complete = stack.getTag().getBoolean(NBTConstants.Beepedia.COMPLETE) || stack.getTag().getBoolean(NBTConstants.Beepedia.CREATIVE);
//            int total = BeeRegistry.get().getBees().size();
//            int count = stack.getTag().getList(NBTConstants.NBT_BEES, 8).size();
//            tooltip.add(Component.translatable(BeepediaTranslations.PROGRESS, complete? total : count, total).withStyle(ChatFormatting.GRAY));
//        }
//
//    }
//
//    public static boolean isCreative(ItemStack stack) {
//        return stack.getItem() instanceof BeepediaItem && !stack.isEmpty() && stack.hasTag() && stack.getOrCreateTag().getBoolean(NBTConstants.Beepedia.CREATIVE);
//    }*/
//}
