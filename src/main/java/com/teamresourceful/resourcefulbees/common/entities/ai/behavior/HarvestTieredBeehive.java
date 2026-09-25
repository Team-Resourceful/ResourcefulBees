package com.teamresourceful.resourcefulbees.common.entities.ai.behavior;

import com.teamresourceful.resourcefulbees.common.blocks.TieredBeehiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Optional;

public class HarvestTieredBeehive extends Behavior<Villager> {

    // 20-40 seconds at 20 ticks/second.
    private static final int MIN_HARVEST_COOLDOWN = 2 * 20;
    private static final int MAX_HARVEST_COOLDOWN = 5 * 20;

    private static final int MAX_DURATION = 200;
    private static final double HARVEST_DISTANCE = 2.0D;

    private final float speedModifier;

    private BlockPos target;
    private long nextHarvestTime;

    public HarvestTieredBeehive(float speedModifier) {
        super(
            Map.of(
                MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED
            ),
            MAX_DURATION
        );

        this.speedModifier = speedModifier;
    }

    @Override
    protected boolean checkExtraStartConditions(
        ServerLevel level,
        Villager villager
    ) {
        if (level.getGameTime() < this.nextHarvestTime) {
            return false;
        }

        Optional<GlobalPos> jobSite =
            villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);

        if (jobSite.isEmpty()) {
            return false;
        }

        GlobalPos globalPos = jobSite.get();

        if (!globalPos.dimension().equals(level.dimension())) {
            return false;
        }

        BlockPos pos = globalPos.pos();
        BlockState state = level.getBlockState(pos);

        if (!isHarvestable(state)) {
            return false;
        }

        this.target = pos;
        return true;
    }

    @Override
    protected void start(
        ServerLevel level,
        Villager villager,
        long gameTime
    ) {
        if (this.target == null) {
            return;
        }

        villager.getBrain().setMemory(
            MemoryModuleType.LOOK_TARGET,
            new BlockPosTracker(this.target)
        );

        villager.getBrain().setMemory(
            MemoryModuleType.WALK_TARGET,
            new WalkTarget(
                this.target,
                this.speedModifier,
                1
            )
        );
    }

    @Override
    protected boolean canStillUse(
        ServerLevel level,
        Villager villager,
        long gameTime
    ) {
        if (this.target == null) {
            return false;
        }

        return isHarvestable(level.getBlockState(this.target));
    }

    @Override
    protected void tick(
        ServerLevel level,
        Villager villager,
        long gameTime
    ) {
        if (this.target == null) {
            return;
        }

        // Keep looking at the hive while approaching it.
        villager.getBrain().setMemory(
            MemoryModuleType.LOOK_TARGET,
            new BlockPosTracker(this.target)
        );

        if (!this.target.closerToCenterThan(
            villager.position(),
            HARVEST_DISTANCE
        )) {
            return;
        }

        BlockState state = level.getBlockState(this.target);

        if (!(state.getBlock() instanceof TieredBeehiveBlock hive)) {
            this.target = null;
            return;
        }

        if (state.getValue(BeehiveBlock.HONEY_LEVEL) < 5) {
            this.target = null;
            return;
        }

        level.playSound(
            null,
            this.target,
            SoundEvents.BEEHIVE_SHEAR,
            SoundSource.BLOCKS,
            1.0F,
            1.0F
        );

        boolean harvested = hive.harvestWithScraper(
            level,
            this.target
        );

        if (harvested) {
            this.nextHarvestTime =
                level.getGameTime()
                    + Mth.nextInt(
                        level.getRandom(),
                        MIN_HARVEST_COOLDOWN,
                        MAX_HARVEST_COOLDOWN
                    );
        }

        this.target = null;
    }

    @Override
    protected void stop(
        ServerLevel level,
        Villager villager,
        long gameTime
    ) {
        this.target = null;
    }

    private static boolean isHarvestable(BlockState state) {
        return state.getBlock() instanceof TieredBeehiveBlock
            && state.getValue(BeehiveBlock.HONEY_LEVEL) >= 5;
    }
}