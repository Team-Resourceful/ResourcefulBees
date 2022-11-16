package com.teamresourceful.resourcefulbees.common.registry.dynamic;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.spawndata.SpawnData;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public final class ModSpawnData {

    public static final DeferredRegister<SpawnData> SPAWN_DATA = DeferredRegister.create(new ResourceLocation(ResourcefulBees.MOD_ID, "spawndata"), ResourcefulBees.MOD_ID);
    public static final Supplier<IForgeRegistry<SpawnData>> SPAWN_DATA_REGISTRY = SPAWN_DATA.makeRegistry(() -> new RegistryBuilder<SpawnData>().dataPackRegistry(SpawnData.CODEC).disableSync());

    public static SpawnData getData(CommonLevelAccessor level, String bee) {
        return level.registryAccess()
                .registry(SPAWN_DATA_REGISTRY.get().getRegistryKey())
                .map(registry -> registry.get(new ResourceLocation(ResourcefulBees.MOD_ID, bee)))
                .orElse(SpawnData.DEFAULT);
    }

    private ModSpawnData() {
        throw new UtilityClassError();
    }
}
