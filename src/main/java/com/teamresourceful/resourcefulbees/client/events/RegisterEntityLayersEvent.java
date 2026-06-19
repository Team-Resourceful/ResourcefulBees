package com.teamresourceful.resourcefulbees.client.events;

import com.teamresourceful.resourcefulbees.events.base.EventHelper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public record RegisterEntityLayersEvent(Function<String, ? extends LivingEntityRenderer<? extends Player, ? extends LivingEntityRenderState, ? extends EntityModel<? extends EntityRenderState>>> getter) {

    public static final EventHelper<RegisterEntityLayersEvent> EVENT = new EventHelper<>();

    @Nullable
    @SuppressWarnings("unchecked")
    public <R extends LivingEntityRenderer<? extends Player, ? extends LivingEntityRenderState, ? extends EntityModel<? extends EntityRenderState>>> R getSkin(String skinName) {
        return (R) getter.apply(skinName);
    }


}
