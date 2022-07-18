package com.teamresourceful.resourcefulbees.common.utils;

import com.teamresourceful.resourcefulbees.api.beedata.traits.TraitData;
import com.teamresourceful.resourcefulbees.common.capabilities.HoneyFluidTank;
import com.teamresourceful.resourcefulbees.common.entity.passive.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TraitConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeeEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ModUtils {

    private ModUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void checkBottleAndCapability(HoneyFluidTank tank, BlockEntity entity, Player player, Level world, BlockPos pos, InteractionHand hand) {
        Item item = player.getItemInHand(hand).getItem();
        if (item instanceof BottleItem) {
            tank.fillBottle(player, hand);
        } else if (item instanceof HoneyBottleItem) {
            tank.emptyBottle(player, hand);
        } else {
            ModUtils.capabilityOrGuiUse(entity, player, world, pos, hand);
        }
    }

    public static void capabilityOrGuiUse(BlockEntity tileEntity, Player player, Level world, BlockPos pos, InteractionHand hand){
        if (player.getItemInHand(hand).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
            tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                    .ifPresent(iFluidHandler -> FluidUtil.interactWithFluidHandler(player, hand, world, pos, null));
        } else if (!player.isShiftKeyDown() && !world.isClientSide) {
            NetworkHooks.openScreen((ServerPlayer) player, (MenuProvider) tileEntity, pos);
        }
    }

    public static ItemStack insertItem(ItemStackHandler handler, int slot, ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;

        ItemStack existing = handler.getStackInSlot(slot);
        int limit = Math.min(64, stack.getMaxStackSize());

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) return stack;
            limit -= existing.getCount();
        }

        if (limit <= 0) return stack;

        boolean reachedLimit = stack.getCount() > limit;
        if (existing.isEmpty()) handler.setStackInSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
        else existing.grow(reachedLimit ? limit : stack.getCount());

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
    }

    public static CompoundTag nbtWithData(String key, Tag tag) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put(key, tag);
        return compoundTag;
    }

    public static <T extends Tag> ListTag listTag(@Nullable List<T> tags) {
        ListTag list = new ListTag();
        if (tags != null && !tags.isEmpty()) {
            list.addAll(tags);
        }
        return list;
    }

    public static <T extends Tag> List<T> fromListTag(ListTag list, Class<T> tagClass) {
        return list.stream().filter(tagClass::isInstance).map(tagClass::cast).toList();
    }

    public static void updateCapturedBee(Bee bee, Player player) {
        bee.setSavedFlowerPos(null);
        ((BeeEntityAccessor) bee).setHivePos(null);
        if (bee.isAngry()) {
            bee.setTarget(player);
            if (bee instanceof ResourcefulBee customBee) {
                TraitData traitData = customBee.getTraitData();
                if (traitData.getDamageTypes().stream().anyMatch(damageType -> damageType.type().equals(TraitConstants.EXPLOSIVE))) {
                    customBee.setExplosiveCooldown(60);
                }
            }
        }
    }

    public static void summonEntity(CompoundTag tag, Level level, Player player, BlockPos pos) {
        if (tag == null) return;
        EntityType.by(tag)
            .map(type -> type.create(level))
            .ifPresent(entity -> {
                entity.load(tag);
                entity.absMoveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                level.addFreshEntity(entity);
                if (entity instanceof Bee beeEntity) ModUtils.updateCapturedBee(beeEntity, player);
            });
    }
}
