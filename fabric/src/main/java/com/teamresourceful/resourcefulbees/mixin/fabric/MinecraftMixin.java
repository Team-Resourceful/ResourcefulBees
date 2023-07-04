package com.teamresourceful.resourcefulbees.mixin.fabric;

import com.teamresourceful.resourcefulbees.mixin.client.PackRepositoryAccessor;
import com.teamresourceful.resourcefulbees.platform.common.events.registry.RegisterRepositorySourceEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow @Final private PackRepository resourcePackRepository;

    @Inject(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/packs/repository/PackRepository;reload()V",
                    shift = At.Shift.BEFORE
            )
    )
    public void rbees$init(GameConfig gameConfig, CallbackInfo ci) {
        PackRepositoryAccessor accessor = (PackRepositoryAccessor) this.resourcePackRepository;
        RegisterRepositorySourceEvent.EVENT.fire(new RegisterRepositorySourceEvent(PackType.CLIENT_RESOURCES, accessor.getSources()::add));
    }
}
