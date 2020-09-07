
package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.api.beedata.ColorData;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.Color;
import com.resourcefulbees.resourcefulbees.utils.RainbowColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class HoneycombBlock extends Block {

    protected final ColorData colorData;
    protected final String beeType; //TODO added just in case but may not actually be needed and can be removed

    //TODO make Rainbow Honeycomb Block class?

    public HoneycombBlock(String beeType, ColorData colorData) {
        super(Block.Properties.from(Blocks.HONEYCOMB_BLOCK));
        this.colorData = colorData;
        this.beeType = beeType;
    }

    public int getHoneycombColor() { return Color.parseInt(colorData.getHoneycombColor()); }

    public String getBeeType() { return beeType; }

    public static int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int tintIndex){
        return ((HoneycombBlock) state.getBlock()).getHoneycombColor();
    }

    public static int getItemColor(ItemStack stack, int tintIndex){
        BlockItem blockItem = (BlockItem) stack.getItem();
        HoneycombBlock honeycombBlock = (HoneycombBlock) blockItem.getBlock();
        return honeycombBlock.colorData.isRainbowBee() ? RainbowColor.getRGB() : honeycombBlock.getHoneycombColor();
    }

    //TODO check if this still applies after changing to registry objects - see todo above

    @Override
    public void animateTick(@Nonnull BlockState stateIn, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
            if (colorData.isRainbowBee())
                world.notifyBlockUpdate(pos, stateIn, stateIn, 2);
        super.animateTick(stateIn, world, pos, rand);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return BeeRegistry.getBeeData(beeType).getCombBlockItemRegistryObject().get().getDefaultInstance();
    }
}

