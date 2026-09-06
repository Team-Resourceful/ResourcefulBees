package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import net.minecraft.advancements.triggers.CriteriaTriggers;
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
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        Optional<BlockState> waxedState = getWaxed(level.getBlockState(pos));
        if (waxedState.isEmpty()) {
            return InteractionResult.PASS;
        }

        BlockState state = waxedState.get();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
        }

        stack.shrink(1);
        level.setBlock(pos, state, 11);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
        level.levelEvent(player, 3003, pos, 0);

        return InteractionResult.SUCCESS_SERVER;
    }

    private static Optional<BlockState> getWaxed(BlockState state) {
        if (state.is(ModBlocks.WAXED_PLANKS.get())) {
            return Optional.of(ModBlocks.TRIMMED_WAXED_PLANKS.get().withPropertiesOf(state));
        }
        return HoneycombItem.getWaxed(state);
    }
}
