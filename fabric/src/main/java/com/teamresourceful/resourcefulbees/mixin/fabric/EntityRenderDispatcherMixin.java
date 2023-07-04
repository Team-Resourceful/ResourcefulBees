package com.teamresourceful.resourcefulbees.mixin.fabric;

import com.teamresourceful.resourcefulbees.platform.client.events.RegisterEntityLayersEvent;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Shadow private Map<String, EntityRenderer<? extends Player>> playerRenderers;

    @Inject(
            method = "onResourceManagerReload",
            at = @At("TAIL")
    )
    private void rbees$onResourceManagerReload(ResourceManager resourceManager, CallbackInfo ci) {
        RegisterEntityLayersEvent.EVENT.fire(new RegisterEntityLayersEvent(
                id -> (LivingEntityRenderer<? extends Player, ? extends EntityModel<? extends Player>>) this.playerRenderers.get(id)
        ));
    }
}
