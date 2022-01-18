package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.blockentity.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryOutputType;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ApiaryBlock extends BeeHouseBlock {

  private final ApiaryTier tier;

  public ApiaryBlock(final ApiaryTier tier) {
    super(BlockBehaviour.Properties.of(Material.WOOD).strength(5f, 6f).sound(SoundType.METAL));
    this.tier = tier;
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
    if(Screen.hasShiftDown()) {
      tooltip.add(new TranslatableComponent(TranslationConstants.BeeHive.MAX_BEES, CommonConfig.APIARY_MAX_BEES.get())
              .append(TranslationConstants.BeeHive.UNIQUE.withStyle(ChatFormatting.BOLD))
              .withStyle(ChatFormatting.GOLD)
      );

      int timeReduction = 100 - (int)(tier.getTimeModifier() * 100);
      tooltip.add(new TranslatableComponent(TranslationConstants.BeeHive.HIVE_TIME, "-", timeReduction).withStyle(ChatFormatting.GOLD));
      TranslatableComponent outputType = tier.getOutputType().equals(ApiaryOutputType.COMB) ? TranslationConstants.Apiary.HONEYCOMB : TranslationConstants.Apiary.HONEYCOMB_BLOCK;

      tooltip.add(new TranslatableComponent(TranslationConstants.Apiary.OUTPUT_TYPE, outputType).withStyle(ChatFormatting.GOLD));
      tooltip.add(new TranslatableComponent(TranslationConstants.Apiary.OUTPUT_QUANTITY, tier.getOutputAmount()).withStyle(ChatFormatting.GOLD));
    } else {
      tooltip.add(TranslationConstants.Items.MORE_INFO.withStyle(ChatFormatting.YELLOW));
    }

    super.appendHoverText(stack, worldIn, tooltip, flagIn);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
    return new ApiaryBlockEntity(tier, pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
    return level.isClientSide ?
            null :
            createTickerHelper(type, tier.getBlockEntityType(), ApiaryBlockEntity::serverTick);
  }
}
