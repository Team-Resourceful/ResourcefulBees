
package com.teamresourceful.resourcefulbees.common.blocks;

import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("deprecation")
public class HoneycombBlock extends Block {

    private final Color color;

    public HoneycombBlock(Color color, Properties properties) {
        super(properties);
        this.color = color;
    }

    public int getHoneycombColor() { return color.getValue(); }


    public static int getBlockColor(BlockState state){
        return ((HoneycombBlock) state.getBlock()).getHoneycombColor();
    }

    @SuppressWarnings("unused")
    public static int getItemColor(ItemStack stack, int tintIndex) {
        return ((HoneycombBlock) ((BlockItem) stack.getItem()).getBlock()).getHoneycombColor();
    }

    @NotNull
    @Override
    public List<ItemStack> getDrops(@NotNull BlockState blockState, @NotNull LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(blockState, builder);
        drops.add(this.asItem().getDefaultInstance());
        return drops;
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level world, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        if (color.isRainbow()) world.sendBlockUpdated(pos, stateIn, stateIn, Block.UPDATE_CLIENTS);
        super.animateTick(stateIn, world, pos, rand);
    }
}

