package com.resourcefulbees.resourcefulbees.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class StrawbeeryMilkshake extends Item {

    public StrawbeeryMilkshake(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    public ItemStack finishUsingItem(ItemStack itemStack, World world, LivingEntity livingEntity) {
        super.finishUsingItem(itemStack, world, livingEntity);
        if (livingEntity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)livingEntity;
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, itemStack);
            serverplayerentity.awardStat(Stats.ITEM_USED.get(this));
        }

        if (!world.isClientSide) {
            livingEntity.removeEffect(Effects.POISON);
        }

        if (itemStack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        } else {
            if (livingEntity instanceof PlayerEntity && !((PlayerEntity)livingEntity).abilities.instabuild) {
                ItemStack itemstack = new ItemStack(Items.GLASS_BOTTLE);
                PlayerEntity playerentity = (PlayerEntity)livingEntity;
                if (!playerentity.inventory.add(itemstack)) {
                    playerentity.drop(itemstack, false);
                }
            }

            return itemStack;
        }
    }

    public int getUseDuration(ItemStack itemStack) {
        return 80;
    }

    public UseAction getUseAnimation(ItemStack itemStack) {
        return UseAction.DRINK;
    }

    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    public ActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        return DrinkHelper.useDrink(world, playerEntity, hand);
    }
}
