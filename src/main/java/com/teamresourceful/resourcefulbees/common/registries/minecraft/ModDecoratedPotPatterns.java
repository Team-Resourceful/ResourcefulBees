package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;

public final class ModDecoratedPotPatterns {

    public static final ResourceKey<DecoratedPotPattern> HONEYCOMB = create("honeycomb");
    public static final ResourceKey<DecoratedPotPattern> BEE = create("bee");

    private static ResourceKey<DecoratedPotPattern> create(String id) {
        return ResourceKey.create(Registries.DECORATED_POT_PATTERN, ModIdentifier.of(id));
    }

    public static void bootstrap(BootstrapContext<DecoratedPotPattern> registry) {
        registerWithDefaultAsset(registry, HONEYCOMB);
        registerWithDefaultAsset(registry, BEE);
    }

    private static void registerWithDefaultAsset(BootstrapContext<DecoratedPotPattern> registry, ResourceKey<DecoratedPotPattern> key) {
        registry.register(key, new DecoratedPotPattern(key.identifier().withSuffix("_pottery_pattern")));
    }

    private ModDecoratedPotPatterns() {}
}