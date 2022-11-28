package com.teamresourceful.resourcefulbees.common.mixin.compat;

import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
import com.teamresourceful.resourcefulbees.common.compat.productivebees.ProductiveBeesCompat;
import com.teamresourceful.resourcefulbees.common.lib.builders.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.lib.builders.BeehiveTier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Pseudo
@Mixin(targets = "cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee")
public abstract class ProductiveBeeMixin extends Bee implements BeeCompat {

    @Shadow
    public abstract int getTimeInHive(boolean hasNectar);

    public ProductiveBeeMixin(EntityType<? extends Bee> arg, Level arg2) {
        super(arg, arg2);
    }

    @Override
    public ItemStack getHiveOutput(BeehiveTier tier) {
        List<ItemStack> output = ProductiveBeesCompat.getBeeProduce(this.level, this, false);
        if (output.isEmpty()) return ItemStack.EMPTY;
        return output.get(this.getRandom().nextInt(output.size()));
    }

    @Override
    public ItemStack getApiaryOutput(ApiaryTier tier) {
        List<ItemStack> output = ProductiveBeesCompat.getBeeProduce(this.level, this, true);
        if (output.isEmpty()) return ItemStack.EMPTY;
        return output.get(this.getRandom().nextInt(output.size()));
    }

    @Override
    public int getMaxTimeInHive() {
        return getTimeInHive(hasNectar());
    }

    @Override
    public void nectarDroppedOff() {
        dropOffNectar();
    }

    @Override
    public void setOutOfHiveCooldown(int cooldown) {
        setStayOutOfHiveCountdown(cooldown);
    }
}
