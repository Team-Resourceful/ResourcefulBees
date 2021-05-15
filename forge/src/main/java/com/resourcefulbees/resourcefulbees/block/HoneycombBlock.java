
package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.api.beedata.HoneycombData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.HitResult;
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

    public static int getBlockColor(BlockState state, @Nullable BlockGetter world, @Nullable BlockPos pos, int tintIndex){
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
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level world, @NotNull BlockPos pos, @NotNull Random rand) {
        if (honeycombData.getColor().isRainbow())
            world.sendBlockUpdated(pos, stateIn, stateIn, 2);
        super.animateTick(stateIn, world, pos, rand);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return honeycombData.getHoneycombBlock().getDefaultInstance();
    }
}

