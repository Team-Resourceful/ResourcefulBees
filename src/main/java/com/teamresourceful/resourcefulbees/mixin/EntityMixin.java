package com.teamresourceful.resourcefulbees.mixin;

import com.teamresourceful.resourcefulbees.common.blocks.CustomHoneyBlock;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Redirect(method = "handleEntityEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/HoneyBlock;showSlideParticles(Lnet/minecraft/world/entity/Entity;)V"))
    public void resourcefulbees$handleEntityEvent(Entity entity) {
        CustomHoneyBlock.showSlideParticles(entity);
    }
}
