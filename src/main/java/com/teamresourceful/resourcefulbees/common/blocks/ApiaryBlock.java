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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class ApiaryBlock extends BeeHouseBlock implements BeeHolderBlock {

  public static final MapCodec<ApiaryBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
          ApiaryTier.CODEC.fieldOf("tier").forGetter(ApiaryBlock::getTier),
          Properties.CODEC.fieldOf("properties").forGetter(ApiaryBlock::properties)
  ).apply(instance, ApiaryBlock::new));

  private final ApiaryTier tier;

  public ApiaryBlock(final ApiaryTier tier, Properties properties) {
    super(properties);
    this.tier = tier;
  }

  public ApiaryTier getTier() {
    return tier;
  }

//  @Override
//  protected @Nullable MenuProvider getMenuProvider(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos) {
//    return new SimpleMenuProvider((containerId, inventory, player) -> new ApiaryMenu(containerId, inventory, (ApiaryBlockEntity) level.getBlockEntity(pos)), Component.translatable("menu.title.resourcefulbees.apiary_menu"));
//  }

  /*  @Override
  @Environment(EnvType.CLIENT)
  public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
    components.add(Component.translatable(BeehiveTranslations.MAX_BEES, tier.maxBees()).withStyle(ChatFormatting.GOLD));
    components.add(Component.translatable(BeehiveTranslations.HIVE_TIME, tier.getTimeModificationAsPercent()).withStyle(ChatFormatting.GOLD));
  }*/


  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
    return new ApiaryBlockEntity(tier, pos, state);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
    return level.isClientSide() ? null : createTickerHelper(type, tier.getBlockEntityType(), (level1, pos, state1, blockEntity) -> ApiaryBlockEntity.serverTick(level1, pos, state1, (ApiaryBlockEntity) blockEntity));
  }

  @Override
  protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }
}
