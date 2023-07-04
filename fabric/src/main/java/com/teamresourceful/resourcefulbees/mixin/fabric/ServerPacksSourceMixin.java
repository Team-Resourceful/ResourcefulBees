package com.teamresourceful.resourcefulbees.mixin.fabric;

import com.teamresourceful.resourcefulbees.mixin.client.PackRepositoryAccessor;
import com.teamresourceful.resourcefulbees.platform.common.events.registry.RegisterRepositorySourceEvent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

/**
 * This is to add the vent to add custom RepositorySources.
 */
@Mixin(ServerPacksSource.class)
public class ServerPacksSourceMixin {

    @Inject(
            method = "createPackRepository(Ljava/nio/file/Path;)Lnet/minecraft/server/packs/repository/PackRepository;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void rbees$onCreatePackRepository(Path path, CallbackInfoReturnable<PackRepository> cir) {
        PackRepository repository = cir.getReturnValue();
        PackRepositoryAccessor accessor = (PackRepositoryAccessor) repository;
        RegisterRepositorySourceEvent.EVENT.fire(new RegisterRepositorySourceEvent(PackType.SERVER_DATA, accessor.getSources()::add));
        cir.setReturnValue(repository);
    }
}
