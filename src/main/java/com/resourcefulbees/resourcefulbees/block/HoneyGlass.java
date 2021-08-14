package com.resourcefulbees.resourcefulbees.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.GlassBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HoneyGlass extends GlassBlock {

    private final boolean collidePlayer; //if true player cannot go through block but bee can - if false player can go through block but bee cannot

    public HoneyGlass(Properties properties, boolean collidePlayer) {
        super(properties);
        this.collidePlayer = collidePlayer;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull IBlockReader world, @NotNull BlockPos pos, @NotNull ISelectionContext context) {
        if (isEntityContext(context)) {
            if ((collidePlayer && isBeeContext(context)) || (isPlayerContext(context) && !collidePlayer)) return VoxelShapes.empty();
            else return state.getShape(world, pos);
        }
        return VoxelShapes.empty();
    }

    private static boolean isEntityContext(ISelectionContext context) {
        return context instanceof EntitySelectionContext && !context.equals(ISelectionContext.empty());
    }

    private static boolean isPlayerContext(ISelectionContext context) {
        return context.getEntity() instanceof PlayerEntity;
    }

    private static boolean isBeeContext(ISelectionContext context) {
        return context.getEntity() instanceof BeeEntity;
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable IBlockReader reader, @NotNull List<ITextComponent> componentList, @NotNull ITooltipFlag tooltipFlag) {
        super.appendHoverText(stack, reader, componentList, tooltipFlag);
        componentList.add((new TranslationTextComponent(collidePlayer ? "block.resourcefulbees.honey_glass.tooltip" : "block.resourcefulbees.honey_glass_player.tooltip")).withStyle(TextFormatting.GOLD));
    }
}
