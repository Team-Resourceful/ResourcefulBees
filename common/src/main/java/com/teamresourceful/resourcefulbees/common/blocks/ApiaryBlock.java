package com.teamresourceful.resourcefulbees.common.blocks;

import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.blockentities.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.BeeHolderBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.BeeHouseBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeehiveTranslations;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ApiaryBlock extends BeeHouseBlock implements BeeHolderBlock {

  private final ApiaryTier tier;

  public ApiaryBlock(final ApiaryTier tier) {
    super(Properties.of(Material.WOOD).strength(5f, 6f).sound(SoundType.METAL));
    this.tier = tier;
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
    components.add(Component.translatable(BeehiveTranslations.MAX_BEES, tier.maxBees()).withStyle(ChatFormatting.GOLD));
    components.add(Component.translatable(BeehiveTranslations.HIVE_TIME, tier.getTimeModificationAsPercent()).withStyle(ChatFormatting.GOLD));
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
    return new ApiaryBlockEntity(tier, pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
    return level.isClientSide ? null : createTickerHelper(type, tier.getBlockEntityType(), (level1, pos, state1, blockEntity) -> ApiaryBlockEntity.serverTick(level1, pos, state1, (ApiaryBlockEntity) blockEntity));
  }
}
