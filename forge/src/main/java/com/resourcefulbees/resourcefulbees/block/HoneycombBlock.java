
package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.api.beedata.ColorData;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class HoneycombBlock extends Block {

    protected final ColorData colorData;
    protected final String beeType;

    public HoneycombBlock(String beeType, ColorData colorData, Properties properties) {
        super(properties);
        this.colorData = colorData;
        this.beeType = beeType;
    }

    public int getHoneycombColor() { return colorData.getHoneycombColorInt(); }

    public String getBeeType() { return beeType; }

    public static int getBlockColor(BlockState state, @Nullable BlockGetter world, @Nullable BlockPos pos, int tintIndex){
        HoneycombBlock honeycombBlock = ((HoneycombBlock) state.getBlock());
        return honeycombBlock.colorData.isRainbowBee() ? RainbowColor.getRGB() : honeycombBlock.getHoneycombColor();
    }

    public static int getItemColor(ItemStack stack, int tintIndex){
        BlockItem blockItem = (BlockItem) stack.getItem();
        HoneycombBlock honeycombBlock = (HoneycombBlock) blockItem.getBlock();
        return honeycombBlock.colorData.isRainbowBee() ? RainbowColor.getRGB() : honeycombBlock.getHoneycombColor();
    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        drops.add(BeeRegistry.getRegistry().getBeeData(beeType).getCombBlockItemRegistryObject().get().getDefaultInstance());
        return drops;
    }

    @Override
    public void animateTick(@Nonnull BlockState stateIn, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Random rand) {
        if (colorData.isRainbowBee())
            world.sendBlockUpdated(pos, stateIn, stateIn, 2);
        super.animateTick(stateIn, world, pos, rand);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return BeeRegistry.getRegistry().getBeeData(beeType).getCombBlockItemRegistryObject().get().getDefaultInstance();
    }
}

