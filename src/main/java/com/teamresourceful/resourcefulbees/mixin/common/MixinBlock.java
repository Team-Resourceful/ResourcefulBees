package com.teamresourceful.resourcefulbees.mixin.common;

import com.teamresourceful.resourcefulbees.platform.common.events.PlayerBrokeBlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class MixinBlock {

    @Inject(method = "playerDestroy", at = @At("HEAD"))
    public void onPlayerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity be, ItemStack stack, CallbackInfo ci) {
        PlayerBrokeBlockEvent.EVENT.fire(new PlayerBrokeBlockEvent(level, player, pos, state));
    }
}
