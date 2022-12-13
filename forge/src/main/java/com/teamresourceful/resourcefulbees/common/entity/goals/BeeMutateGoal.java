package com.teamresourceful.resourcefulbees.common.entity.goals;

import com.teamresourceful.resourcefulbees.api.data.bee.mutation.BeeMutationData;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.common.entity.passive.ResourcefulBee;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.Map;

public class BeeMutateGoal extends Goal {

    private final ResourcefulBee bee;

    public BeeMutateGoal(ResourcefulBee bee) {
        this.bee = bee;
    }

    @Override
    public boolean canUse() {
        if (bee.hasFakeFlower()) return false;
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
            if (doMutation(mutationMap, (ServerLevel) bee.level, bee.blockPosition())){
                bee.incrementNumCropsGrownSincePollination();
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
            if (output.chance() < serverLevel.random.nextFloat()) continue;
            if (output.activate(serverLevel, pos)) {
                return true;
            }
        }
        return false;
    }
}
