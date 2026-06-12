package com.teamresourceful.resourcefulbees.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.server.level.ServerPlayer.class)
public interface ServerPlayerAccessor {
    @Accessor
    MinecraftServer getServer();
}
