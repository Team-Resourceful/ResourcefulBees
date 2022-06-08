
package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class HoneycombBlock extends Block {

    private final Color color;

    public HoneycombBlock(Color color, Properties properties) {
        super(properties);
        this.color = color;
    }

    public int getHoneycombColor() { return color.getValue(); }


    @SuppressWarnings("unused")
    public static int getBlockColor(BlockState state, @Nullable BlockAndTintGetter world, @Nullable BlockPos pos, int tintIndex){
        return ((HoneycombBlock) state.getBlock()).getHoneycombColor();
    }

    @SuppressWarnings("unused")
    public static int getItemColor(ItemStack stack, int tintIndex) {
        return ((HoneycombBlock) ((BlockItem) stack.getItem()).getBlock()).getHoneycombColor();
    }

    @NotNull
    @Override
    public List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        drops.add(this.asItem().getDefaultInstance());
        return drops;
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level world, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        if (color.isRainbow()) world.sendBlockUpdated(pos, stateIn, stateIn, 2);
        super.animateTick(stateIn, world, pos, rand);
    }
}

