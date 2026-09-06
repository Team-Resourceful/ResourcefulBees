package com.teamresourceful.resourcefulbees.mixin.common;

import com.teamresourceful.resourcefulbees.common.extensions.BeehiveBlockEntityExtension;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(BeehiveBlockEntity.class)
public abstract class BeehiveBlockEntityMixin implements BeehiveBlockEntityExtension {

    @Final
    @Shadow
    private List<BeehiveBlockEntity.Occupant> stored;

    @Override
    public void resourcefulbees$clearBees() {
        this.stored.clear();
    }
}