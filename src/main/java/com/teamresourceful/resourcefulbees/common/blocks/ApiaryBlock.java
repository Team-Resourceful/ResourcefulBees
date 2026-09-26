package com.teamresourceful.resourcefulbees.common.blocks;

import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.blockentities.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.BeeHolderBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.BeeHouseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ApiaryBlock extends BeeHouseBlock implements BeeHolderBlock {

  private final ApiaryTier tier;

  public ApiaryBlock(final ApiaryTier tier, Properties properties) {
    super(properties);
    this.tier = tier;
  }

  public ApiaryTier getTier() {
    return tier;
  }

  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
    return new ApiaryBlockEntity(tier, pos, state);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
    return level.isClientSide() ? null : createTickerHelper(type, tier.getBlockEntityType(), (level1, pos, state1, blockEntity) -> ApiaryBlockEntity.serverTick(level1, pos, state1, (ApiaryBlockEntity) blockEntity));
  }
}
