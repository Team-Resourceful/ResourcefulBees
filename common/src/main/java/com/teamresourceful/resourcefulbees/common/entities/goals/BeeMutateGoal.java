package com.teamresourceful.resourcefulbees.common.entities.goals;

import com.teamresourceful.resourcefulbees.api.data.bee.mutation.BeeMutationData;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

import java.util.Map;

public class BeeMutateGoal extends Goal {

    private final ResourcefulBee bee;

    public BeeMutateGoal(ResourcefulBee bee) {
        this.bee = bee;
    }

    public static void spawnParticles(Level level, Entity entity) {
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 5; ++i) {
                double d0 = level.random.nextGaussian() * 0.02D;
                double d1 = level.random.nextGaussian() * 0.02D;
                double d2 = level.random.nextGaussian() * 0.02D;
                serverLevel.sendParticles(ParticleTypes.COMPOSTER,
                        entity.getRandomX(2.0D),
                        entity.getRandomY(),
                        entity.getRandomZ(2.0D),
                        10, d0, d1, d2, 0.1f);
            }
        }
    }

    @Override
    public boolean canUse() {
        if (bee.fakeFlower.hasData()) return false;
        if (bee.getNumberOfMutations() >= bee.getMutationData().count()) return false;
        if (bee.level.random.nextFloat() < 0.3F) return false;
        return bee.hasNectar() && !bee.isAngry();
    }

    @Override
    public void tick() {
        if (!(bee.getLevel() instanceof ServerLevel serverLevel)) return;
        if (bee.tickCount % 5 == 0) {
            BeeMutationData mutationData = bee.getMutationData();
            Map<MutationType, WeightedCollection<MutationType>> mutationMap = mutationData.mutations(serverLevel);
            if (doMutation(mutationMap, (ServerLevel) bee.level, bee.blockPosition())) {
                bee.incrementNumCropsGrownSincePollination();
                spawnParticles(serverLevel, bee);
            }
        }
    }

    public static boolean doMutation(Map<MutationType, WeightedCollection<MutationType>> mutationMap, ServerLevel serverLevel, BlockPos blockPos) {
        for (Map.Entry<MutationType, WeightedCollection<MutationType>> entry : mutationMap.entrySet()) {
            MutationType input = entry.getKey();
            if (input.chance() < serverLevel.random.nextFloat()) continue;
            BlockPos pos = input.check(serverLevel, blockPos);
            if (pos == null) continue;
            MutationType output = entry.getValue().next();
            if (output.chance() >= serverLevel.random.nextFloat() && output.activate(serverLevel, pos)) {
                return true;
            }
        }
        return false;
    }
}
