package com.teamresourceful.resourcefulbees.common.registry.custom;

import com.teamresourceful.resourcefulbees.common.entity.passive.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TraitConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTeleportEvent;

public final class DefaultTraitAbilities {

    private DefaultTraitAbilities() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void registerDefaultAbilities(TraitAbilityRegistry registry) {
        registry.register(TraitConstants.TELEPORT, DefaultTraitAbilities::enderAbility);
        registry.register(TraitConstants.SLIMY, DefaultTraitAbilities::slimeAbility);
        registry.register(TraitConstants.FLAMMABLE, DefaultTraitAbilities::fireAbility);
        registry.register(TraitConstants.ANGRY, DefaultTraitAbilities::angryAbility);
    }

    private static void enderAbility(ResourcefulBee bee) {
        if (!bee.hasCustomName() && bee.tickCount % 150 == 0 && bee.level.isDay() && !bee.isPollinating() && !bee.hasHiveInRange() && !bee.getDisruptorInRange() && !bee.level.isClientSide() && bee.isAlive()) {
            double x = bee.getX() + (bee.getRandom().nextDouble() - 0.5D) * 4.0D;
            double y = bee.getY() + (bee.getRandom().nextInt(4) - 2);
            double z = bee.getZ() + (bee.getRandom().nextDouble() - 0.5D) * 4.0D;
            BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(x, y, z);

            while (blockPos.getY() > 0 && !bee.level.getBlockState(blockPos).getMaterial().blocksMotion()) {
                blockPos.move(Direction.DOWN);
            }

            BlockState blockstate = bee.level.getBlockState(blockPos);
            boolean canMove = blockstate.getMaterial().blocksMotion();
            boolean water = blockstate.getFluidState().is(FluidTags.WATER);
            if (canMove && !water) {
                EntityTeleportEvent.EnderEntity event = new EntityTeleportEvent.EnderEntity(bee, x, y, z);
                if (MinecraftForge.EVENT_BUS.post(event)) return;
                boolean teleported = bee.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
                if (teleported) {
                    bee.level.playSound(null, event.getTargetX(), event.getTargetY(), event.getTargetZ(), SoundEvents.ENDERMAN_TELEPORT, bee.getSoundSource(), 1.0F, 1.0F);
                    bee.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }
        }
    }

    private static void slimeAbility(ResourcefulBee bee) {
        if (!bee.checkSpawnObstruction(bee.level) && !bee.wasColliding()) {
            for (int j = 0; j < 8; ++j) {
                float f = bee.getRandom().nextFloat() * ((float) Math.PI * 2F);
                float f1 = bee.getRandom().nextFloat() * 0.5F + 0.5F;
                float f2 = Mth.sin(f) * 1 * 0.5F * f1;
                float f3 = Mth.cos(f) * 1 * 0.5F * f1;
                bee.level.addParticle(ParticleTypes.ITEM_SLIME, bee.getX() + f2, bee.getY(), bee.getZ() + f3, 0.0D, 0.0D, 0.0D);
            }

            bee.playSound(SoundEvents.SLIME_SQUISH, 0.4F, ((bee.getRandom().nextFloat() - bee.getRandom().nextFloat()) * 0.2F + 1.0F) / 0.8F);
            bee.setColliding();
        }
    }

    private static void fireAbility(ResourcefulBee bee) {
        if (bee.tickCount % 150 == 0) {
            bee.setSecondsOnFire(3);
        }
    }

    private static void angryAbility(ResourcefulBee bee) {
        if (!bee.hasEffect(ModEffects.CALMING.get())) {
            Entity player = bee.level.getNearestPlayer(bee, 20);
            bee.setPersistentAngerTarget(player != null ? player.getUUID() : null);
            bee.setRemainingPersistentAngerTime(1000);
        }
    }

}
