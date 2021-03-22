package com.resourcefulbees.resourcefulbees.mixin;

import com.resourcefulbees.resourcefulbees.data.DataPackLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.DataPackConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @Inject(method = "configurePackRepository", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/resources/ResourcePackList;reload()V",
            shift = At.Shift.BEFORE))
    private static void onReloadDatapacks(PackRepository resourcePacks, DataPackConfig codec, boolean forceVanilla, CallbackInfoReturnable<DataPackConfig> info) {
        resourcePacks.addPackFinder(DataPackLoader.INSTANCE);
    }
}