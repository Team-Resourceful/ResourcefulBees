
package com.dungeonderps.resourcefulbees.block;

import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.lib.NBTConstants;
import com.dungeonderps.resourcefulbees.registry.ColorHandler;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.HoneycombTileEntity;
import com.dungeonderps.resourcefulbees.utils.NBTHelper;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HoneycombBlock extends Block {

    public HoneycombBlock() {
        super(Block.Properties.from(Blocks.HONEYCOMB_BLOCK));
    }

    public static int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int tintIndex){
        if (world != null && pos != null){
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof HoneycombTileEntity) {
                return  ((HoneycombTileEntity) tile).getColor();
            }
        }

        return BeeConstants.DEFAULT_ITEM_COLOR;
    }

    public static int getItemColor(ItemStack stack, int tintIndex){
        return ColorHandler.getItemColor(stack, tintIndex);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new HoneycombTileEntity();}

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        if (world != null && pos != null) {
            HoneycombTileEntity tile = (HoneycombTileEntity) world.getTileEntity(pos);
            if (tile != null) {
                CompoundNBT combData = tile.serializeNBT();
                ItemStack honeyCombBlockItemStack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());
                honeyCombBlockItemStack.setTag(NBTHelper.createHoneycombItemTag(combData.getString(NBTConstants.NBT_BEE_TYPE), combData.getString(NBTConstants.NBT_COLOR)));
                return honeyCombBlockItemStack;
            }
        }
        return RegistryHandler.HONEYCOMB_BLOCK_ITEM.get().getDefaultInstance();
    }

    public void onBlockPlacedBy(World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {

        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile instanceof HoneycombTileEntity) {
            HoneycombTileEntity honeycombTileEntity = (HoneycombTileEntity) tile;
            honeycombTileEntity.loadFromNBT(stack.getOrCreateChildTag(NBTConstants.NBT_ROOT));
        }
    }
}

