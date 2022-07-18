package com.teamresourceful.resourcefulbees.common.mixin;

import com.teamresourceful.resourcefulbees.common.item.dispenser.ShearsDispenserBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraftforge.common.ToolActions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(DispenserBlock.class)
public abstract class MixinDispenserBlock {

    @Shadow protected abstract DispenseItemBehavior getDispenseMethod(ItemStack p_52667_);

    @Inject(
        method = "dispenseFrom",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/world/level/block/DispenserBlock;getDispenseMethod(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/core/dispenser/DispenseItemBehavior;"
        ),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true
    )
    public void onDispenseFromInject(ServerLevel level, BlockPos pos, CallbackInfo ci, BlockSourceImpl source, DispenserBlockEntity dispenser, int slot, ItemStack stack) {
        DispenseItemBehavior behavior = getDispenseMethod(stack);
        if (stack.canPerformAction(ToolActions.SHEARS_HARVEST)) {
            dispenser.setItem(slot, new ShearsDispenserBehavior(behavior).dispense(source, stack));
            ci.cancel();
        }
    }
}
