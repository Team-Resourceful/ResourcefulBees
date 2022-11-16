package com.teamresourceful.resourcefulbees.platform.client.events;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class AddLayersEvent {

    public final Function<String, ? extends @Nullable LivingEntityRenderer<? extends Player, ? extends EntityModel<? extends Player>>> getter;

    public AddLayersEvent(Function<String, ? extends @Nullable LivingEntityRenderer<? extends Player, ? extends EntityModel<? extends Player>>> getter) {
        this.getter = getter;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <R extends LivingEntityRenderer<? extends Player, ? extends EntityModel<? extends Player>>> R getSkin(String skinName) {
        return (R) getter.apply(skinName);
    }


}
