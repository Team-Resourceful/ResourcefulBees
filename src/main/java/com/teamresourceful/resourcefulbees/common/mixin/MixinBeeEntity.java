package com.teamresourceful.resourcefulbees.common.mixin;

import com.teamresourceful.resourcefulbees.api.IBeeCompat;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.blockentity.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentity.ApiaryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Bee.class)
public abstract class MixinBeeEntity extends Animal implements IBeeCompat {

    protected MixinBeeEntity(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }

    @Shadow
    private BlockPos hivePos;

    @Shadow
    public boolean hasHive() {
        return this.hivePos != null;
    }

    @Shadow public abstract boolean hasNectar();

    @Shadow public abstract void dropOffNectar();

    @Shadow public abstract void setStayOutOfHiveCountdown(int p_27916_);

    @Inject(at = @At("HEAD"), method = "doesHiveHaveSpace", cancellable = true)
    private void doesHiveHaveSpace(BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
        BlockEntity blockEntity = this.level.getBlockEntity(pos);
        if ((blockEntity instanceof TieredBeehiveBlockEntity tieredHive && !tieredHive.isFull())
                || (blockEntity instanceof ApiaryBlockEntity apiary && apiary.hasSpace())
                || (blockEntity instanceof BeehiveBlockEntity beeHive && !beeHive.isFull())) {
            callback.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "isHiveValid()Z", cancellable = true)
    public void isHiveValid(CallbackInfoReturnable<Boolean> callback) {
        if (this.hasHive()) {
            BlockPos pos = this.hivePos;
            if (pos != null) {
                BlockEntity blockEntity = this.level.getBlockEntity(this.hivePos);
                if ((blockEntity instanceof TieredBeehiveBlockEntity tieredHive && tieredHive.isAllowedBee())
                        || (blockEntity instanceof ApiaryBlockEntity apiary && apiary.isAllowedBee())) {
                    callback.setReturnValue(true);
                }
            }
        }
    }

    @Override
    public ItemStack getHiveOutput(BeehiveTier tier) {
        return new ItemStack(Items.HONEYCOMB);
    }

    @Override
    public ItemStack getApiaryOutput(ApiaryTier tier) {
        return new ItemStack(tier.getOutputType().isComb() ? Items.HONEYCOMB : Items.HONEYCOMB_BLOCK, tier.getOutputAmount());
    }

    @Override
    public int getMaxTimeInHive() {
        return this.hasNectar() ? 1200 : 600;
    }

    @Override
    public void nectarDroppedOff() {
        this.dropOffNectar();
    }

    @Override
    public void setOutOfHiveCooldown(int cooldown) {
        this.setStayOutOfHiveCountdown(cooldown);
    }
}
