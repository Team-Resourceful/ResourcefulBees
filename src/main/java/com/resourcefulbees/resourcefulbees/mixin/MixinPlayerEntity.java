package com.resourcefulbees.resourcefulbees.mixin;

import com.resourcefulbees.resourcefulbees.lib.TagConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends Entity {
    @Shadow
    protected boolean wasUnderwater;

    public MixinPlayerEntity(EntityType<?> p_i48580_1_, World p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
    }

    //allow underwater stuff to run properly when inside honey fluid
    @Inject(method = "updateIsUnderwater()Z",
            at = @At(value = "RETURN"), cancellable = true)
    private void resourcefulbeesHoneyUnderwater(CallbackInfoReturnable<Boolean> cir) {
        if(!cir.getReturnValue()) {
            this.wasUnderwater = this.isEyeInFluid(TagConstants.RESOURCEFUL_HONEY);
            if(this.wasUnderwater) cir.setReturnValue(true);
        }
    }
}