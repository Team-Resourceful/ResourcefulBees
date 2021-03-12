package com.resourcefulbees.resourcefulbees.mixin;

import com.resourcefulbees.resourcefulbees.data.DataPackLoader;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.codec.DatapackCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @Inject(method = "configurePackRepository", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/resources/ResourcePackList;reload()V",
            shift = At.Shift.BEFORE))
    private static void onReloadDatapacks(ResourcePackList resourcePacks, DatapackCodec codec, boolean forceVanilla, CallbackInfoReturnable<DatapackCodec> info) {
        resourcePacks.addPackFinder(DataPackLoader.INSTANCE);
    }
}