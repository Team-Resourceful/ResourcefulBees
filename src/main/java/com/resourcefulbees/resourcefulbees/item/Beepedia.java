package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.utils.BeepediaUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class Beepedia extends Item {


    public Beepedia(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ActionResult<ItemStack> use(World world, PlayerEntity playerEntity, @NotNull Hand hand) {
        ItemStack itemstack = playerEntity.getItemInHand(hand);
        if (world.isClientSide){
            BeepediaUtils.loadBeepedia();
        }
        return ActionResult.sidedSuccess(itemstack, world.isClientSide());
    }

    @Override
    public @NotNull ActionResultType interactLivingEntity(@NotNull ItemStack stack, PlayerEntity player, @NotNull LivingEntity entity, @NotNull Hand hand) {
        if (player.level.isClientSide && entity instanceof CustomBeeEntity) {
            BeepediaUtils.loadBeepedia(entity);
            return ActionResultType.SUCCESS;
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }
}
