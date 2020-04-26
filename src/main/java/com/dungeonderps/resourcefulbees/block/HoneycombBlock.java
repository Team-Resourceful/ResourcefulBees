
package com.dungeonderps.resourcefulbees.block;


import com.dungeonderps.resourcefulbees.RegistryHandler;
import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.tileentity.HoneycombBlockEntity;
import com.dungeonderps.resourcefulbees.utils.Color;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class HoneycombBlock extends Block {

    public String blockColor = "0xFFFFFF";
    public String beeType = "Default";

    public HoneycombBlock() {
        super(Block.Properties.from(Blocks.HONEYCOMB_BLOCK));
    }

    public void setBlockColor(String blockColor) {
        this.blockColor = blockColor;
    }

    public void setBeeType(String beeType) {
        this.beeType = beeType;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new HoneycombBlockEntity();}

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        ItemStack honeyCombBlockItemStack = new ItemStack(RegistryHandler.HONEYCOMBBLOCKITEM.get());
        final CompoundNBT honeyCombItemStackTag = honeyCombBlockItemStack.getOrCreateChildTag("ResourcefulBees");
        honeyCombItemStackTag.putString("Color", blockColor);
        honeyCombItemStackTag.putString("BeeType", beeType);

        return honeyCombBlockItemStack;
    }

    public static int getColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int tintIndex){
        HoneycombBlock combBlock = (HoneycombBlock)state.getBlock();
        return Color.parseInt(combBlock.blockColor);
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

        TileEntity blockEntity = worldIn.getTileEntity(pos);

        if(blockEntity instanceof HoneycombBlockEntity) {
            HoneycombBlockEntity honeycombBlockEntity = (HoneycombBlockEntity) blockEntity;
            honeycombBlockEntity.setBeeType(stack.getChildTag("ResourcefulBees").getString("BeeType"));
            honeycombBlockEntity.setCombColor(stack.getChildTag("ResourcefulBees").getString("Color"));
        }

    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public String getTranslationKey() {
        String name;
        if (!beeType.isEmpty()) {
            name = "block" + '.' + ResourcefulBees.MOD_ID + '.' + beeType.toLowerCase() + "_honeycomb_block";
        } else {
            name = "block" + '.' + ResourcefulBees.MOD_ID + '.' + "resourceful_honeycomb_block";
        }
        return name;
    }
}

