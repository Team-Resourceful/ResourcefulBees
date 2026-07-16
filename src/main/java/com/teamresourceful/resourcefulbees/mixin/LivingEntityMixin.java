package com.teamresourceful.resourcefulbees.mixin;

import com.teamresourceful.resourcefulbees.common.blocks.CustomHoneyBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Redirect(method = "handleEntityEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/HoneyBlock;showJumpParticles(Lnet/minecraft/world/entity/Entity;)V"))
    public void resourcefulbees$handleEntityEvent(Entity entity) {
        CustomHoneyBlock.showJumpParticles(entity);
    }
}
