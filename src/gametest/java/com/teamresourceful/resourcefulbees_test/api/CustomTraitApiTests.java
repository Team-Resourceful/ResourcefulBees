package com.teamresourceful.resourcefulbees_test.api;

import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.common.registries.custom.TraitRegistry;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;

public class CustomTraitApiTests {

    private static final Identifier TEST_TRAIT = Identifier.fromNamespaceAndPath("resourcefulbees_test", "datapack");

    public static void datapackTraitIsRegistered(GameTestHelper helper) {
        TraitRegistry registry = TraitRegistry.getRegistry();

        if (!registry.getTraits().containsKey(TEST_TRAIT)) {
            helper.fail("Datapack trait " + TEST_TRAIT + " was not found in TraitRegistry. Check that the test datapack is enabled and TraitReloadListener ran successfully.");
            return;
        }

        Trait trait = registry.getTrait(TEST_TRAIT);

        if (trait == Trait.DEFAULT) {
            helper.fail("Datapack trait " + TEST_TRAIT + " resolved to Trait.DEFAULT instead of a loaded trait.");
            return;
        }

        helper.succeed();
    }
}
