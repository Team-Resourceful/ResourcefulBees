package com.teamresourceful.resourcefulbees.block.multiblocks.apiary;

import com.teamresourceful.resourcefulbees.config.Config;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary.ApiaryBreederTileEntity;
import com.teamresourceful.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class ApiaryBreederBlock extends Block{

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
        return (MenuProvider)worldIn.getBlockEntity(pos);
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new ApiaryBreederTileEntity();
    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.addAll(new TooltipBuilder()
                .addTip(I18n.get("block.resourcefulbees.apiary_breeder.tooltip.info"), ChatFormatting.GOLD)
                .addTip(I18n.get("block.resourcefulbees.apiary_breeder.tooltip.info1"), ChatFormatting.GOLD)
                .appendText(String.format("%1$s ticks", Config.APIARY_MAX_BREED_TIME.get()), ChatFormatting.GOLD)
                .addTip(I18n.get("block.resourcefulbees.apiary_breeder.tooltip.info2"), ChatFormatting.GOLD)
                .build());
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
