package com.teamresourceful.resourcefulbees.platform.client.events;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public record AddLayersEvent(Function<String, ? extends LivingEntityRenderer<? extends Player, ? extends EntityModel<? extends Player>>> getter) {

    @Nullable
    @SuppressWarnings("unchecked")
    public <R extends LivingEntityRenderer<? extends Player, ? extends EntityModel<? extends Player>>> R getSkin(String skinName) {
        return (R) getter.apply(skinName);
    }


}
