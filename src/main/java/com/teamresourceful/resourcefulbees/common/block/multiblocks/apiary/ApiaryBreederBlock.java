package com.teamresourceful.resourcefulbees.common.block.multiblocks.apiary;

import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryBreederTileEntity;
import com.teamresourceful.resourcefulbees.common.utils.TooltipBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class ApiaryBreederBlock extends Block {

    public ApiaryBreederBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any());
    }



    @NotNull
    @Override
    public ActionResultType use(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull BlockRayTraceResult blockRayTraceResult) {
        if (!player.isShiftKeyDown() && !world.isClientSide) {
            INamedContainerProvider blockEntity = state.getMenuProvider(world,pos);
            NetworkHooks.openGui((ServerPlayerEntity) player, blockEntity, pos);
        }
        return ActionResultType.SUCCESS;
    }


    @Nullable
    @Override
    public INamedContainerProvider getMenuProvider(@NotNull BlockState state, World worldIn, @NotNull BlockPos pos) {
        return (INamedContainerProvider) worldIn.getBlockEntity(pos);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ApiaryBreederTileEntity();
    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable IBlockReader worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        tooltip.addAll(new TooltipBuilder()
                .addTranslatableTip("block.resourcefulbees.apiary_breeder.tooltip.info", TextFormatting.GOLD)
                .addTip("block.resourcefulbees.apiary_breeder.tooltip.info1", TextFormatting.GOLD)
                .appendText(String.format("%1$s ticks", CommonConfig.APIARY_MAX_BREED_TIME.get()), TextFormatting.GOLD)
                .addTip("block.resourcefulbees.apiary_breeder.tooltip.info2", TextFormatting.GOLD)
                .build());
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
