package com.dungeonderps.resourcefulbees.block;

import com.dungeonderps.resourcefulbees.config.Config;
import com.dungeonderps.resourcefulbees.tileentity.ApiaryBreederTileEntity;
import com.dungeonderps.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
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
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ApiaryBreederBlock extends Block{

    public ApiaryBreederBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState());
    }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult blockRayTraceResult) {
        if (!world.isRemote) {
            INamedContainerProvider blockEntity = state.getContainer(world,pos);
            if (blockEntity != null) {
                NetworkHooks.openGui((ServerPlayerEntity) player, blockEntity, pos);
            }
        }
        return ActionResultType.SUCCESS;
    }


    @Nullable
    @Override
    public INamedContainerProvider getContainer(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos) {
        return (INamedContainerProvider)worldIn.getTileEntity(pos);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ApiaryBreederTileEntity();
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable IBlockReader worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.addAll(new TooltipBuilder()
                .addTip(I18n.format("block.resourcefulbees.apiary_breeder.tooltip.info"), TextFormatting.GOLD)
                .addTip(I18n.format("block.resourcefulbees.apiary_breeder.tooltip.info1"), TextFormatting.GOLD)
                .appendText(String.format("%1$s ticks", Config.APIARY_MAX_BREED_TIME.get()), TextFormatting.GOLD)
                .addTip(I18n.format("block.resourcefulbees.apiary_breeder.tooltip.info2"), TextFormatting.GOLD)
                .build());
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
