package com.teamresourceful.resourcefulbees.common.mixin;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ItemGroupResourcefulBees;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class MixinItem {
    @Inject(at = @At("RETURN"), method = "getItemCategory", cancellable = true)
    private void getItemCategory(CallbackInfoReturnable<CreativeModeTab> callback) {
        if (ModUtils.IS_DATAGEN && callback.getReturnValue() == null) callback.setReturnValue(ItemGroupResourcefulBees.RESOURCEFUL_BEES);
    }
}
