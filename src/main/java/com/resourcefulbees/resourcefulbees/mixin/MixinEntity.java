package com.resourcefulbees.resourcefulbees.mixin;

import com.resourcefulbees.resourcefulbees.api.IEntity;
import com.resourcefulbees.resourcefulbees.lib.TagConstants;
import com.resourcefulbees.resourcefulbees.utils.HoneyFluidUtils;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author TelepathicGrunt
 * <p>Copied from Bumblezone.</p>
 */
@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity {

    @Shadow
    public abstract boolean isEyeInFluid(ITag<Fluid> fluidITag);

    @Shadow
    public abstract boolean isSwimming();

    @Shadow
    public abstract boolean isSprinting();

    @Shadow
    public abstract boolean isUnderWater();

    @Shadow
    public abstract boolean isPassenger();

    @Shadow
    public abstract BlockPos blockPosition();

    @Shadow
    public abstract void setSwimming(boolean isSwimming);

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getZ();

    @Shadow
    protected boolean wasEyeInWater;

    @Shadow
    protected ITag<Fluid> fluidOnEyes;

    @Shadow
    public World level;

    @Unique
    private boolean wasTouchingHoney = false;

    @Override
    public boolean getTouchedHoney() {
        return wasTouchingHoney;
    }

    @Override
    public void setTouchingHoney(boolean b) {
        wasTouchingHoney = b;
    }

    // let honey fluid push entity
    @Inject(method = "updateInWaterStateAndDoWaterCurrentPushing()V",
            at = @At(value = "TAIL"))
    private void resourcefulbeesFluidPushing(CallbackInfo ci) {
        HoneyFluidUtils.doFluidMovement((Entity) (Object) this);
    }

    // make sure we set that we are in fluid
    @Inject(method = "updateFluidOnEyes()V",
            at = @At(value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/entity/Entity;isEyeInFluid(Lnet/minecraft/tags/ITag;)Z",
                    shift = At.Shift.AFTER))
    private void resourcefulbeesMarkEyesInFluid(CallbackInfo ci) {
        if (!this.wasEyeInWater) {
            this.wasEyeInWater = this.isEyeInFluid(TagConstants.RESOURCEFUL_HONEY);
        }
    }

    @Inject(method = "updateFluidOnEyes()V",
            at = @At(value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/World;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;",
                    shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    private void resourcefulbeesMarkEyesInFluid2(CallbackInfo ci, double eyeHeight) {
        // Have to get the fluid myself as the local capture here is uh broken. Dies on the vehicle entity variable
        BlockPos blockPos = new BlockPos(this.getX(), eyeHeight, this.getZ());
        FluidState fluidState = this.level.getFluidState(blockPos);
        if (fluidState.is(TagConstants.RESOURCEFUL_HONEY)) {
            double fluidHeight = (float) blockPos.getY() + fluidState.getHeight(this.level, blockPos);
            if (fluidHeight > eyeHeight) {
                this.fluidOnEyes = TagConstants.RESOURCEFUL_HONEY;
                ci.cancel();
            }
        }
    }

    // let honey fluid push entity
    @Inject(method = "updateSwimming()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSwimming()Z", ordinal = 1, shift = At.Shift.AFTER))
    private void resourcefulbeesSetSwimming(CallbackInfo ci) {
        // check if we were not set to swimming in water. If not, then check if we are swimming in honey fluid instead
        if (!this.isSwimming() && this.isSprinting() && this.isUnderWater() && !this.isPassenger()) {
            this.setSwimming(this.level.getFluidState(this.blockPosition()).is(TagConstants.RESOURCEFUL_HONEY));
        }
    }
}