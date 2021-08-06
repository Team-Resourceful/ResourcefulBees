
package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.api.beedata.HoneycombData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class HoneycombBlock extends Block {

    protected final HoneycombData honeycombData;
    protected final String beeType;

    public HoneycombBlock(String beeType, HoneycombData honeycombData, Properties properties) {
        super(properties);
        this.honeycombData = honeycombData;
        this.beeType = beeType;
    }

    public int getHoneycombColor() { return honeycombData.getColor().getValue(); }

    public String getBeeType() { return beeType; }

    public static int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int tintIndex){
        HoneycombBlock honeycombBlock = ((HoneycombBlock) state.getBlock());
        return honeycombBlock.getHoneycombColor();
    }

    public static int getItemColor(ItemStack stack, int tintIndex){
        BlockItem blockItem = (BlockItem) stack.getItem();
        HoneycombBlock honeycombBlock = (HoneycombBlock) blockItem.getBlock();
        return honeycombBlock.getHoneycombColor();
    }

    @NotNull
    @Override
    public List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        drops.add(honeycombData.getHoneycombBlock().getDefaultInstance());
        return drops;
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull World world, @NotNull BlockPos pos, @NotNull Random rand) {
        if (honeycombData.getColor().isRainbow())
            world.sendBlockUpdated(pos, stateIn, stateIn, 2);
        super.animateTick(stateIn, world, pos, rand);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return honeycombData.getHoneycombBlock().getDefaultInstance();
    }
}

