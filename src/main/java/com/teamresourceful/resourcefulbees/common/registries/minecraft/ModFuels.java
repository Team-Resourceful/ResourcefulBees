package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.predicates.LootPredicates;
import net.minecraft.world.level.storage.loot.providers.number.ints.ConditionalValue;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

public class ModFuels {

    private ModFuels() throws UtilityClassException {
        throw new UtilityClassException();
    }


    public static final ResourceKey<ContextIntProvider> WAX_BURN_TIME = ResourceKey.create(Registries.CONTEXT_INT_PROVIDER, ModIdentifier.of("cooking/wax_burn_time"));
    public static final ResourceKey<ContextIntProvider> WAX_BLOCK_BURN_TIME = ResourceKey.create(Registries.CONTEXT_INT_PROVIDER, ModIdentifier.of("cooking/wax_block_burn_time"));

    public static void bootstrap(BootstrapContext<ContextIntProvider> context) {
        context.register(
                WAX_BURN_TIME,
                ContextIntProviders.div(
                        ContextIntProviders.exactly(400),
                        Holder.direct(new ConditionalValue(
                                context.lookup(Registries.PREDICATE)
                                        .getOrThrow(LootPredicates.FAST_FURNACE),
                                context.lookup(Registries.CONTEXT_INT_PROVIDER)
                                        .getOrThrow(ContextIntProviders.COOKING_FAST_BURN_TIME_REDUCTION_FACTOR),
                                context.lookup(Registries.CONTEXT_INT_PROVIDER)
                                        .getOrThrow(ContextIntProviders.COOKING_NORMAL_BURN_TIME_REDUCTION_FACTOR)
                        ))
                ).value()
        );

        context.register(
                WAX_BLOCK_BURN_TIME,
                ContextIntProviders.div(
                        ContextIntProviders.exactly(4000),
                        Holder.direct(new ConditionalValue(
                                context.lookup(Registries.PREDICATE)
                                        .getOrThrow(LootPredicates.FAST_FURNACE),
                                context.lookup(Registries.CONTEXT_INT_PROVIDER)
                                        .getOrThrow(ContextIntProviders.COOKING_FAST_BURN_TIME_REDUCTION_FACTOR),
                                context.lookup(Registries.CONTEXT_INT_PROVIDER)
                                        .getOrThrow(ContextIntProviders.COOKING_NORMAL_BURN_TIME_REDUCTION_FACTOR)
                        ))
                ).value()
        );
    }
}
