package com.teamresourceful.resourcefulbees.mixin.fabric;

import com.teamresourceful.resourcefulbees.mixin.client.PackRepositoryAccessor;
import com.teamresourceful.resourcefulbees.platform.common.events.registry.RegisterRepositorySourceEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * This is to add the vent to add custom RepositorySources.
 */
@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {

    @Inject(
        method = "openFresh",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;createDefaultLoadConfig(Lnet/minecraft/server/packs/repository/PackRepository;Lnet/minecraft/world/level/WorldDataConfiguration;)Lnet/minecraft/server/WorldLoader$InitConfig;",
            shift = At.Shift.BEFORE
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void rbees$onOpenFresh(Minecraft minecraft, Screen lastScreen, CallbackInfo ci, PackRepository repository) {
        PackRepositoryAccessor accessor = (PackRepositoryAccessor) repository;
        RegisterRepositorySourceEvent.EVENT.fire(new RegisterRepositorySourceEvent(PackType.SERVER_DATA, accessor.getSources()::add));
    }
}
