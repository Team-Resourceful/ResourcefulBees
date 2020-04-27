
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

    public String blockColor = "0x000000";
    public String beeType = "default";

    public HoneycombBlock() {
        super(Block.Properties.from(Blocks.HONEYCOMB_BLOCK));
    }

    public void setBlockColor(String blockColor){
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

    public static int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int tintIndex){
        LOGGER.info("Setting Block Color");
        /*
        if(world != null && pos != null) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof HoneycombBlockEntity) {
                return ((HoneycombBlockEntity) tile).getColor();
             }
        }
        */
        if(world != null && pos != null) {
            HoneycombBlock combBlock = (HoneycombBlock) world.getBlockState(pos).getBlock();
            return Color.parseInt(combBlock.blockColor);
        }
        return 0x000000;
    }



    public static int getItemColor(ItemStack stack, int tintIndex){
        CompoundNBT honeycombNBT = stack.getChildTag("ResourcefulBees");
        return honeycombNBT != null && honeycombNBT.contains("Color") ? Color.parseInt(honeycombNBT.getString("Color")) : 0x000000;
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

        TileEntity blockEntity = worldIn.getTileEntity(pos);

        if(blockEntity instanceof HoneycombBlockEntity) {
            HoneycombBlockEntity honeycombBlockEntity = (HoneycombBlockEntity) blockEntity;
            CompoundNBT stackNBT = stack.getChildTag("ResourcefulBees");

            if (stackNBT.contains("BeeType")) {
                honeycombBlockEntity.setBeeType(stack.getChildTag("ResourcefulBees").getString("BeeType"));
                setBeeType(stack.getChildTag("ResourcefulBees").getString("BeeType"));
            } else {
                honeycombBlockEntity.setBeeType("Default");
                setBeeType("Default");
            }

            if (stackNBT.contains("BeeType")) {
                honeycombBlockEntity.setBeeType(stack.getChildTag("ResourcefulBees").getString("Color"));
            } else {
                honeycombBlockEntity.setBeeType("0x000000");
            }
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

