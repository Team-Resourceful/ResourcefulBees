package com.teamresourceful.resourcefulbees.common.mixin;

import com.teamresourceful.resourcefulbees.common.item.dispenser.ShearsDispenserBehavior;
import com.teamresourceful.resourcefulbees.common.mixin.invokers.DispenserBlockInvoker;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DispenserBlock.class)
public class MixinDispenserBlock {

    @Redirect(method = "dispenseFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/DispenserBlock;getDispenseMethod(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/core/dispenser/DispenseItemBehavior;"))
    public DispenseItemBehavior onDispenseFrom(DispenserBlock instance, ItemStack itemStack) {
        DispenseItemBehavior behavior = ((DispenserBlockInvoker) Blocks.DISPENSER).invokeGetBehavior(itemStack);
        return itemStack.getItem() instanceof ShearsItem ? new ShearsDispenserBehavior(behavior) : behavior;
    }
}
