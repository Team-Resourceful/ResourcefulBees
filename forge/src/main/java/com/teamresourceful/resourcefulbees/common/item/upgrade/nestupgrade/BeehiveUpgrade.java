package com.teamresourceful.resourcefulbees.common.item.upgrade.nestupgrade;

import com.teamresourceful.resourcefulbees.common.blockentity.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.builders.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultBeehiveTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IExtensibleEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum BeehiveUpgrade implements IExtensibleEnum {
    T1_TO_T2(DefaultBeehiveTiers.T1_NEST, (state, level, pos, stack) -> performUpgrade(state, level, pos, block -> getUpdateFor(block, '2'))),
    T2_TO_T3(DefaultBeehiveTiers.T1_NEST, (state, level, pos, stack) -> performUpgrade(state, level, pos, block -> getUpdateFor(block, '3'))),
    T3_TO_T4(DefaultBeehiveTiers.T1_NEST, (state, level, pos, stack) -> performUpgrade(state, level, pos, block -> getUpdateFor(block, '4')));

    public final BeehiveTier from;
    public final NestUpgrader upgrader;

    BeehiveUpgrade(BeehiveTier from, NestUpgrader upgrader) {
        this.from = from;
        this.upgrader = upgrader;
    }

    @SuppressWarnings("unused")
    public static BeehiveUpgrade create(String name, BeehiveTier from, NestUpgrader upgrader) {
        throw new IllegalStateException("Enum not extended");
    }

    private static InteractionResult performUpgrade(BlockState state, Level level, BlockPos pos, NestGetter getter) {
        if (state.isAir()) return InteractionResult.FAIL;
        Block nest = getter.getNest(state.getBlock());
        if (nest == null) return InteractionResult.FAIL;
        if (performBlockReplacementAndDataMerge(nest, state, level, pos)) return InteractionResult.sidedSuccess(level.isClientSide());
        return InteractionResult.FAIL;
    }

    public static boolean performBlockReplacementAndDataMerge(Block newBlock, BlockState old, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) return false;
        CompoundTag data = blockEntity.saveWithoutMetadata();

        BlockState newBlockState = newBlock.withPropertiesOf(old);
        level.setBlock(pos, newBlockState, Block.UPDATE_ALL);
        if (newBlock instanceof EntityBlock entityBlock) {
            BlockEntity newBlockEntity = entityBlock.newBlockEntity(pos, newBlockState);
            if (newBlockEntity != null) {
                CompoundTag freshData = newBlockEntity.saveWithoutMetadata();
                freshData.merge(data);
                newBlockEntity.load(freshData);
                newBlockEntity.setChanged();
                level.setBlockEntity(newBlockEntity);
                if (newBlockEntity instanceof TieredBeehiveBlockEntity hiveBlockEntity) {
                    TieredBeehiveBlockEntity.recalculateHoneyLevel(hiveBlockEntity);
                }
                return true;
            }
        }
        return false;
    }

    public static Block getUpdateFor(Block block, char i) {
        ResourceLocation id = Registry.BLOCK.getKey(block);
        return Registry.BLOCK.getOptional(new ResourceLocation(id.getNamespace(), id.getPath().substring(0, id.getPath().length() - 1) + i)).orElse(null);
    }

    @FunctionalInterface
    public interface NestUpgrader {
        InteractionResult performUpgrade(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull ItemStack itemstack);
    }

    @FunctionalInterface
    private interface NestGetter {
        @Nullable Block getNest(Block block);
    }
}
