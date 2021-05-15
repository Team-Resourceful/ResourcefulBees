package com.teamresourceful.resourcefulbees.mixin;

import com.teamresourceful.resourcefulbees.data.DataPackLoader;
import com.teamresourceful.resourcefulbees.lib.ModConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.DataPackConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    private MixinMinecraftServer() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    @Inject(method = "configurePackRepository", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/packs/repository/PackRepository;reload()V",
            shift = At.Shift.BEFORE))
    private static void onReloadDatapacks(PackRepository resourcePacks, DataPackConfig codec, boolean forceVanilla, CallbackInfoReturnable<DataPackConfig> info) {
        resourcePacks.addPackFinder(DataPackLoader.INSTANCE);
    }
}