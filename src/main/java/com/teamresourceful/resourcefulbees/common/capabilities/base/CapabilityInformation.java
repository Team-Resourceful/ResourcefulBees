package com.teamresourceful.resourcefulbees.common.capabilities.base;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.HashSet;
import java.util.Set;

public class CapabilityInformation {

    private static final BiMap<ResourceLocation, Capability<? extends INBTSerializable<CompoundTag>>> SYNC_CAPABILITIES = HashBiMap.create();
    private static final Set<Capability<? extends INBTSerializable<CompoundTag>>> SAVED_CAPABILITIES = new HashSet<>();

    public static <D extends INBTSerializable<CompoundTag>, T extends Capability<D>> T registerSynced(ResourceLocation id, T capability) {
        if (SYNC_CAPABILITIES.containsKey(id)) throw new IllegalArgumentException("Capability " + id + " is already registered");
        SYNC_CAPABILITIES.put(id, capability);
        return capability;
    }

    public static <D extends INBTSerializable<CompoundTag>, T extends Capability<D>> T registerSaved(T capability) {
        SAVED_CAPABILITIES.add(capability);
        return capability;
    }

    public static Capability<? extends INBTSerializable<CompoundTag>> get(ResourceLocation id) {
        return SYNC_CAPABILITIES.get(id);
    }

    public static ResourceLocation get(Capability<? extends INBTSerializable<CompoundTag>> capability) {
        return SYNC_CAPABILITIES.inverse().get(capability);
    }

    public static void cloneEvent(PlayerEvent.Clone event) {
        for (var capability : SAVED_CAPABILITIES) {
            copy(event, capability);
        }
    }

    private static <D extends INBTSerializable<CompoundTag>, T extends Capability<D>> void copy(PlayerEvent.Clone event, T capability) {
        event.getOriginal().getCapability(capability).ifPresent(cap -> event.getEntity().getCapability(capability).ifPresent(c -> c.deserializeNBT(cap.serializeNBT())));
    }
}
