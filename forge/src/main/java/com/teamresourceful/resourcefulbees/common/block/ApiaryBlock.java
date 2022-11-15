package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.block.base.BeeHouseBlock;
import com.teamresourceful.resourcefulbees.common.blockentity.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.item.IShiftingToolTip;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.lib.builders.ApiaryTier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ApiaryBlock extends BeeHouseBlock implements IShiftingToolTip {

  private final ApiaryTier tier;

  public ApiaryBlock(final ApiaryTier tier) {
    super(Properties.of(Material.WOOD).strength(5f, 6f).sound(SoundType.METAL));
    this.tier = tier;
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
    setupTooltip(stack, level, components, flag);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
    return new ApiaryBlockEntity(tier, pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
    //TODO fix when we move this class to common
    return level.isClientSide ? null : createTickerHelper(type, tier.getBlockEntityType(), (level1, pos, state1, blockEntity) -> ApiaryBlockEntity.serverTick(level1, pos, state1, (ApiaryBlockEntity) blockEntity));
  }

  @Override
  public Component getShiftingDisplay() {
    return TranslationConstants.Items.TOOLTIP_STATS;
  }

  @Override
  public void appendShiftTooltip(@NotNull ItemStack stack, @Nullable BlockGetter pLevel, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
    components.add(Component.translatable(TranslationConstants.BeeHive.MAX_BEES, CommonConfig.APIARY_MAX_BEES.get())
            .append(TranslationConstants.BeeHive.UNIQUE.withStyle(ChatFormatting.BOLD))
            .withStyle(ChatFormatting.GOLD)
    );

    int timeReduction = 100 - (int)(tier.time() * 100);
    components.add(Component.translatable(TranslationConstants.BeeHive.HIVE_TIME, "-", timeReduction).withStyle(ChatFormatting.GOLD));
    MutableComponent outputType = tier.output().get().isComb() ? TranslationConstants.Apiary.HONEYCOMB : TranslationConstants.Apiary.HONEYCOMB_BLOCK;

    components.add(Component.translatable(TranslationConstants.Apiary.OUTPUT_TYPE, outputType).withStyle(ChatFormatting.GOLD));
    components.add(Component.translatable(TranslationConstants.Apiary.OUTPUT_QUANTITY, tier.amount().getAsInt()).withStyle(ChatFormatting.GOLD));
  }
}
