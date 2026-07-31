package com.teamresourceful.resourcefulbees.common.items.dispenser;

import com.teamresourceful.resourcefulbees.common.blocks.TieredBeehiveBlock;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public class ShearsDispenserBehavior implements DispenseItemBehavior {

    private final DispenseItemBehavior vanillaBehavior;
    private final DispenseItemBehavior tieredHiveBehavior =
            new TieredHiveShearingBehavior();

    public ShearsDispenserBehavior(DispenseItemBehavior vanillaBehavior) {
        this.vanillaBehavior = vanillaBehavior;
    }

    @Override
    public @NonNull ItemStack dispense(@NonNull BlockSource source, @NonNull ItemStack stack) {
        BlockPos targetPos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
        BlockState targetState = source.level().getBlockState(targetPos);

        if (canShearTieredHive(targetState)) {
            return tieredHiveBehavior.dispense(source, stack);
        }

        return vanillaBehavior.dispense(source, stack);
    }

    private static boolean canShearTieredHive(BlockState state) {
        return GeneralConfig.allowShears
                && state.getBlock() instanceof TieredBeehiveBlock
                && state.getValue(BeehiveBlock.HONEY_LEVEL) >= 5;
    }

    private static class TieredHiveShearingBehavior
            extends DefaultDispenseItemBehavior {

        @Override
        protected @NonNull ItemStack execute(@NonNull BlockSource source, @NonNull ItemStack stack) {
            ServerLevel level = source.level();
            BlockPos targetPos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
            BlockState targetState = level.getBlockState(targetPos);

            if (!(targetState.getBlock() instanceof TieredBeehiveBlock hiveBlock)) {
                return stack;
            }

            stack.hurtAndBreak(1, level, (LivingEntity) null, ignored -> {});
            TieredBeehiveBlock.dropResourceHoneycomb(hiveBlock, level, targetPos, false);
            hiveBlock.releaseBeesAndResetHoneyLevel(level, targetState, targetPos, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);

            return stack;
        }
    }
}
