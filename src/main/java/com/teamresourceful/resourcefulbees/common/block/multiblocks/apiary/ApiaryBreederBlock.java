package com.teamresourceful.resourcefulbees.common.block.multiblocks.apiary;

import com.teamresourceful.resourcefulbees.common.block.RenderingBaseEntityBlock;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class ApiaryBreederBlock extends RenderingBaseEntityBlock {

    public ApiaryBreederBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any());
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult blockRayTraceResult) {
        if (!player.isShiftKeyDown() && !world.isClientSide) {
            MenuProvider blockEntity = state.getMenuProvider(world,pos);
            NetworkHooks.openGui((ServerPlayer) player, blockEntity, pos);
        }
        return InteractionResult.SUCCESS;
    }


    @Nullable
    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState state, Level worldIn, @NotNull BlockPos pos) {
        return (MenuProvider) worldIn.getBlockEntity(pos);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.add(TranslationConstants.Items.BREEDER_TOOLTIP.withStyle(ChatFormatting.GOLD));
        tooltip.add(new TranslatableComponent(TranslationConstants.Items.BREEDER_TOOLTIP_1, CommonConfig.APIARY_MAX_BREED_TIME.get()).withStyle(ChatFormatting.GOLD));
        tooltip.add(TranslationConstants.Items.BREEDER_TOOLTIP_2.withStyle(ChatFormatting.GOLD));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ApiaryTileEntity(pos, state);
    }
}
