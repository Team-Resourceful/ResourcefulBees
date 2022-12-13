package com.teamresourceful.resourcefulbees.common.entity.projectile;

import com.teamresourceful.resourcefulbees.common.entity.goals.BeeMutateGoal;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.MutationRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModEntities;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class ThrownMutatedPollen extends ThrowableItemProjectile {
    public ThrownMutatedPollen(EntityType<? extends ThrowableItemProjectile> arg, Level arg2) {
        super(arg, arg2);
    }

    public ThrownMutatedPollen(Level level) {
        super(ModEntities.THROWN_MUTATED_POLLEN.get(), level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.MUTATED_POLLEN.get();
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
        spawnParticles();
        this.kill();
        ItemStack item = this.getItem();
        if (item.getTag() == null || !item.getTag().contains(NBTConstants.POLLEN_ID)) {
            return;
        }
        if (this.level instanceof ServerLevel serverLevel) {
            MutationRecipe recipe = MutationRecipe.getRecipe(level, new ResourceLocation(item.getTag().getString(NBTConstants.POLLEN_ID)));
            if (recipe == null) return;
            BeeMutateGoal.doMutation(recipe.mutations(), serverLevel, result);
        }
    }
    protected void spawnParticles() {
        if (level instanceof ServerLevel serverLevel) {
            for(int i = 0; i < 5; ++i) {
                double d0 = level.random.nextGaussian() * 0.02D;
                double d1 = level.random.nextGaussian() * 0.02D;
                double d2 = level.random.nextGaussian() * 0.02D;
                serverLevel.sendParticles(ParticleTypes.COMPOSTER,
                        this.getRandomX(2.0D),
                        this.getRandomY(),
                        this.getRandomZ(2.0D),
                        10, d0, d1, d2, 0.1f);
            }
        }
    }
}
