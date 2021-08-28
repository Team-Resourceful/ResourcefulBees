
package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

@SuppressWarnings({"deprecation", "unused"})
public class HoneycombBlock extends Block {

    private final Color color;

    public HoneycombBlock(Color color, Properties properties) {
        super(properties);
        this.color = color;
    }

    public int getHoneycombColor() { return color.getValue(); }


    public static int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int tintIndex){
        return ((HoneycombBlock) state.getBlock()).getHoneycombColor();
    }

    public static int getItemColor(ItemStack stack, int tintIndex) {
        BlockItem blockItem = (BlockItem) stack.getItem();
        HoneycombBlock honeycombBlock = (HoneycombBlock) blockItem.getBlock();
        return honeycombBlock.getHoneycombColor();
    }

    @NotNull
    @Override
    public List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        drops.add(this.asItem().getDefaultInstance());
        return drops;
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull World world, @NotNull BlockPos pos, @NotNull Random rand) {
        if (color.isRainbow()) world.sendBlockUpdated(pos, stateIn, stateIn, 2);
        super.animateTick(stateIn, world, pos, rand);
    }
}

