package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            if ((collidePlayer && isBeeContext(entityContext)) || (isPlayerContext(entityContext) && !collidePlayer)) return Shapes.empty();
            else return state.getShape(level, pos);
        }
        return Shapes.empty();
    }

    private static boolean isPlayerContext(EntityCollisionContext context) {
        return context.getEntity() instanceof Player;
    }

    private static boolean isBeeContext(EntityCollisionContext context) {
        return context.getEntity() instanceof Bee;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(collidePlayer ? TranslationConstants.Items.HONEY_GLASS : TranslationConstants.Items.HONEY_GLASS_PLAYER);
    }
}
