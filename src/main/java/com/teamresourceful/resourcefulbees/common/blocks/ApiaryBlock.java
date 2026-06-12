package com.teamresourceful.resourcefulbees.common.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.blockentities.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.BeeHolderBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.BeeHouseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class ApiaryBlock extends BeeHouseBlock implements BeeHolderBlock {

  public static final MapCodec<ApiaryBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
          ApiaryTier.CODEC.fieldOf("tier").forGetter(ApiaryBlock::getTier)
  ).apply(instance, ApiaryBlock::new));

  private final ApiaryTier tier;

  public ApiaryBlock(final ApiaryTier tier) {
    super(Properties.of().sound(SoundType.WOOD).strength(5f, 6f).sound(SoundType.METAL));
    this.tier = tier;
  }

  public ApiaryTier getTier() {
    return tier;
  }

/*  @Override
  @Environment(EnvType.CLIENT)
  public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
    components.add(Component.translatable(BeehiveTranslations.MAX_BEES, tier.maxBees()).withStyle(ChatFormatting.GOLD));
    components.add(Component.translatable(BeehiveTranslations.HIVE_TIME, tier.getTimeModificationAsPercent()).withStyle(ChatFormatting.GOLD));
  }*/

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
    return new ApiaryBlockEntity(tier, pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
    return level.isClientSide() ? null : createTickerHelper(type, tier.getBlockEntityType(), (level1, pos, state1, blockEntity) -> ApiaryBlockEntity.serverTick(level1, pos, state1, (ApiaryBlockEntity) blockEntity));
  }

  @Override
  protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }
}
