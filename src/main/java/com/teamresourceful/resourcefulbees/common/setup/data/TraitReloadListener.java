package com.teamresourceful.resourcefulbees.common.setup.data;

import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registries.custom.TraitAbilityRegistry;
import com.teamresourceful.resourcefulbees.common.registries.custom.TraitRegistry;
import com.teamresourceful.resourcefulbees.common.setup.data.traitdata.PreparedTraitData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public final class TraitReloadListener extends SimplePreparableReloadListener<PreparedTraitData> {

    @Override
    protected @NonNull PreparedTraitData prepare(@NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profiler) {
        return new PreparedTraitData(
                TraitAbilitySetup.loadAbilities(resourceManager),
                TraitSetup.loadTraits(resourceManager)
        );
    }

    @Override
    protected void apply(@NonNull PreparedTraitData prepared, @NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profiler) {
        TraitAbilityRegistry abilityRegistry = TraitAbilityRegistry.getRegistry();
        TraitRegistry traitRegistry = TraitRegistry.getRegistry();
        BeeRegistry beeRegistry = BeeRegistry.getRegistry();
        Map<Identifier, TraitAbility> previousAbilities = abilityRegistry.getDatapackAbilities();
        Map<Identifier, Trait> previousTraits = traitRegistry.getTraits();

        try {
            abilityRegistry.replaceDatapackAbilities(prepared.abilities());
            validateAbilityReferences(prepared.traits(), abilityRegistry);
            traitRegistry.replace(prepared.traits());

            beeRegistry.regenerateCustomBeeData(getRegistryLookup());

            ModConstants.LOGGER.info(
                    "Reloaded {} datapack abilities and {} traits. "
                            + "Bee registry revision: {}",
                    prepared.abilities().size(),
                    prepared.traits().size(),
                    beeRegistry.revision()
            );

        } catch (RuntimeException exception) {
            try {
                abilityRegistry.replaceDatapackAbilities(previousAbilities);
            } catch (RuntimeException rollbackException) {
                exception.addSuppressed(rollbackException);
            }

            try {
                traitRegistry.replace(previousTraits);
            } catch (RuntimeException rollbackException) {
                exception.addSuppressed(rollbackException);
            }

            ModConstants.LOGGER.error(
                    "Failed to reload Resourceful Bees traits and abilities.",
                    exception
            );

            throw exception;
        }
    }

    private static void validateAbilityReferences(Map<Identifier, Trait> traits, TraitAbilityRegistry registry) {
        traits.forEach((traitId, trait) -> {
            for (Identifier abilityId : trait.specialAbilities()) {
                if (!registry.hasAbility(abilityId)) {
                    throw new IllegalStateException(
                            "Trait " + traitId
                                    + " references unknown ability "
                                    + abilityId
                    );
                }
            }
        });
    }
}