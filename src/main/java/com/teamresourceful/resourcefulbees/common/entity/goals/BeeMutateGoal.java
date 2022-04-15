package com.teamresourceful.resourcefulbees.common.entity.goals;

import com.teamresourceful.resourcefulbees.api.beedata.mutation.MutationData;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.IMutation;
import com.teamresourceful.resourcefulbees.common.entity.passive.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

import java.util.Map;

public class BeeMutateGoal extends Goal {

    private final ResourcefulBee bee;

    public BeeMutateGoal(ResourcefulBee bee) {
        super();
        this.bee = bee;
    }

    @Override
    public boolean canUse() {
        if (bee.getNumberOfMutations() >= bee.getMutationData().getMutationCount()) return false;
        if (bee.level.random.nextFloat() < 0.3F) return false;
        return bee.hasNectar() && !bee.isAngry();
    }

    @Override
    public void tick() {
        Level level = bee.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (bee.tickCount % 5 == 0) {
            MutationData mutationData = bee.getMutationData();
            if (mutationData.hasMutation()) {
                for (Map.Entry<IMutation, RandomCollection<IMutation>> entry : mutationData.mutations().entrySet()) {
                    IMutation input = entry.getKey();
                    if (input.chance() < bee.level.random.nextFloat()) continue;
                    BlockPos pos = entry.getKey().check(serverLevel, bee.blockPosition());
                    if (pos == null) continue;
                    IMutation output = entry.getValue().next();
                    if (output.chance() < bee.level.random.nextFloat()) continue;
                    if (output.activate(serverLevel, pos)) {
                        bee.incrementNumCropsGrownSincePollination();
                        break;
                    }
                }
            }
        }
    }
}
