package com.resourcefulbees.resourcefulbees.mixin;

import com.resourcefulbees.resourcefulbees.item.dispenser.ShearsDispenserBehavior;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DispenserBlock.class)
public class MixinDispenserBlock {

    @Redirect(method = "dispenseFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/DispenserBlock;getDispenseMethod(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/dispenser/IDispenseItemBehavior;"))
    public IDispenseItemBehavior onDispenseFrom(DispenserBlock instance, ItemStack itemStack) {
        IDispenseItemBehavior behavior = ((DispenserBlockInvoker) Blocks.DISPENSER).invokeGetBehavior(itemStack);
        return itemStack.getItem() instanceof ShearsItem ? new ShearsDispenserBehavior(behavior) : behavior;
    }
}
