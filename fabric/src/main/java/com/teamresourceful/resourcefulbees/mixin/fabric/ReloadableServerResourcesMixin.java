package com.teamresourceful.resourcefulbees.mixin.fabric;

import com.teamresourceful.resourcefulbees.common.fabric.ReloadableServerResourcesHook;
import com.teamresourceful.resourcefulbees.platform.common.events.RegisterReloadListenerEvent;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin implements ReloadableServerResourcesHook {

    @Unique
    private boolean rbees$settingUp = false;

    @Inject(
            method = "loadResources",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/packs/resources/SimpleReloadInstance;create(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Z)Lnet/minecraft/server/packs/resources/ReloadInstance;",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void rbees$onLoadResourcesBefore(ResourceManager resourceManager, RegistryAccess.Frozen frozen, FeatureFlagSet featureFlagSet, Commands.CommandSelection commandSelection, int i, Executor executor, Executor executor2, CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir, ReloadableServerResources resources) {
        if (resources instanceof ReloadableServerResourcesHook hook) {
            hook.rbees$setupReloadableServerResources(true);
        }
    }

    @Inject(
            method = "loadResources",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void rbees$onLoadResourcesAfter(ResourceManager resourceManager, RegistryAccess.Frozen frozen, FeatureFlagSet featureFlagSet, Commands.CommandSelection commandSelection, int i, Executor executor, Executor executor2, CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir, ReloadableServerResources resources) {
        if (resources instanceof ReloadableServerResourcesHook hook) {
            hook.rbees$setupReloadableServerResources(false);
        }
    }

    @Inject(
            method = "listeners",
            at = @At("RETURN"),
            cancellable = true
    )
    private void rbees$getListeners(CallbackInfoReturnable<List<PreparableReloadListener>> cir) {
        if (this.rbees$isReloadableServerResourcesSettingUp()) {
            List<PreparableReloadListener> listeners = new ArrayList<>(cir.getReturnValue());
            RegisterReloadListenerEvent.EVENT.fire(new RegisterReloadListenerEvent(listeners::add, (ReloadableServerResources) (Object) this));
            cir.setReturnValue(List.copyOf(listeners));
        }
    }

    @Override
    public void rbees$setupReloadableServerResources(boolean settingUp) {
        this.rbees$settingUp = settingUp;
    }

    @Override
    public boolean rbees$isReloadableServerResourcesSettingUp() {
        return this.rbees$settingUp;
    }
}
