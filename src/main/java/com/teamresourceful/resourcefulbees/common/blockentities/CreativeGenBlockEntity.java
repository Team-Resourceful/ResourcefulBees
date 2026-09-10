package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.blocks.base.InstanceBlockEntityTicker;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.InfiniteEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.HashSet;
import java.util.Set;

public class CreativeGenBlockEntity extends BlockEntity implements InstanceBlockEntityTicker {

    private final EnergyHandler battery = new InfiniteEnergyHandler();
    private final Set<BlockCapabilityCache<EnergyHandler, Direction>> energyCache = new HashSet<>();

    public CreativeGenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CREATIVE_GEN_ENTITY.get(), pos, state);
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level != null) {
            distributeEnergyNearby();
        }
    }

    private void distributeEnergyNearby() {
        energyCache.forEach(cache -> {
            try(Transaction transaction = Transaction.openRoot()) {
                int extracted = battery.extract(Integer.MAX_VALUE, transaction);
                var cap = cache.getCapability();
                int inserted = cap == null ? 0 : cap.insert(Integer.MAX_VALUE, transaction);
                if (inserted == extracted) {
                    transaction.commit();
                }
            }
        });
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel) {
            for (Direction direction : Direction.values()) {
                var cache = BlockCapabilityCache.create(
                        Capabilities.Energy.BLOCK,
                        serverLevel,
                        worldPosition.relative(direction),
                        direction,
                        () -> !isRemoved(),
                        () -> {
                        }
                );
                energyCache.add(cache);
            }
        }
    }
}
