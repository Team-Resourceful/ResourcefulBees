package com.teamresourceful.resourcefulbees.common.entities.entity;

import com.teamresourceful.resourcefulbees.common.entities.goals.BeeMutateGoal;
import com.teamresourceful.resourcefulbees.common.recipes.MutationRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEntities;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.NonNull;

public class ThrownPollenItem extends ThrowableItemProjectile {

    public ThrownPollenItem(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public ThrownPollenItem(Level level) {
        super(ModEntities.THROWN_POLLEN.get(), level);
    }

    @Override
    protected @NonNull Item getDefaultItem() {
        return ModItems.POLLEN.get();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        doMutation(result.getBlockPos().above());
        super.onHitBlock(result);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (result.getEntity() instanceof Player) return;
        doMutation(result.getEntity().blockPosition().above());
        super.onHitEntity(result);
    }

    private void doMutation(BlockPos result) {
        BeeMutateGoal.spawnParticles(this.level(), this);
        this.discard();
        ItemStack item = this.getItem();
        var pollenRecipe = item.get(ModDataComponents.POLLEN_RECIPE);
        if (pollenRecipe != null && this.level() instanceof ServerLevel serverLevel) {
            MutationRecipe recipe = MutationRecipe.getRecipe(serverLevel, pollenRecipe.recipe()).value();
            BeeMutateGoal.doMutation(recipe.mutations(), serverLevel, result);
        }
    }
}
