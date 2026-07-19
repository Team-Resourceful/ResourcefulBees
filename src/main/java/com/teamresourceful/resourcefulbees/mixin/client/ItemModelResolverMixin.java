package com.teamresourceful.resourcefulbees.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.hooks.client.ModelManagerHook;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModelResolver.class)
public class ItemModelResolverMixin {

    @Unique
    private ModelManagerHook manager;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void rbees$storeModelManager(ModelManager modelManager, CallbackInfo ci) {
        this.manager = (ModelManagerHook) modelManager;
    }

    @ModifyExpressionValue(method = "appendItemLayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    private Object rbees$modifyDataComponentType1(
            Object original,
            @Local(argsOnly = true) ItemStack stack
    ) {
        return rbees$getModel(original, stack);
    }


    @ModifyExpressionValue(
            method = {"shouldPlaySwapAnimation", "swapAnimationScale"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;")
    )
    private Object rbees$modifyDataComponentType2(
            Object original,
            @Local(argsOnly = true) ItemStack stack
    ) {
        return rbees$getModel(original, stack);
    }

    @Unique
    private Object rbees$getModel(Object original, ItemStack stack) {
        if (original instanceof Identifier id && !manager.rbees$hasCustomModel(id)) {
            return stack.getOrDefault(ModDataComponents.FALLBACK_ITEM_MODEL.get(), id);
        }
        return original;
    }
}
