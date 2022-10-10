package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WaxItem extends Item {

    public WaxItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        return getWaxed(blockstate).map((p_238251_) -> {
            Player player = pContext.getPlayer();
            ItemStack itemstack = pContext.getItemInHand();
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
            }

            itemstack.shrink(1);
            level.setBlock(blockpos, p_238251_, 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, p_238251_));
            level.levelEvent(player, 3003, blockpos, 0);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }).orElse(InteractionResult.PASS);
    }

    private static Optional<BlockState> getWaxed(BlockState state) {
        if (state.is(ModBlocks.WAXED_PLANKS.get())) {
            return Optional.of(ModBlocks.TRIMMED_WAXED_PLANKS.get().withPropertiesOf(state));
        }
        return HoneycombItem.getWaxed(state);
    }
}
