package com.resourcefulbees.resourcefulbees.mixin;

import com.resourcefulbees.resourcefulbees.api.IEntity;
import com.resourcefulbees.resourcefulbees.utils.HoneyFluidUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author TelepathicGrunt
 * <p>Copied from Bumblezone.</p>
 */
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    public MixinLivingEntity(EntityType<?> p_i48580_1_, World p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
    }

    @Inject(at = @At("TAIL"), method = "baseTick()V")
    public void resourcefulbeesBaseTick(CallbackInfo info) {
        HoneyFluidUtils.breathing((LivingEntity)(Object)this);
    }

    @Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/LivingEntity;getFluidJumpThreshold()D", ordinal = 1), method = "aiStep")
    private void resourcefulbeesAIStep(CallbackInfo info) {
        HoneyFluidUtils.doJump(((IEntity) (Object) this).getTouchedHoney(), (LivingEntity) (Object) this);
    }
}
