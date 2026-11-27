package com.teamresourceful.resourcefulbees_test.api;

import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;
import com.teamresourceful.resourcefulbees.api.data.trait.abilities.TeleportAbility;
import com.teamresourceful.resourcefulbees.common.registries.custom.TraitAbilityRegistry;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class CustomTraitAbilityApiTests {

    private static final Identifier TEST_ABILITY = Identifier.fromNamespaceAndPath("resourcefulbees_test", "short_teleport");

    private CustomTraitAbilityApiTests() {
    }

    public static void datapackAbilityIsRegistered(GameTestHelper helper) {
        TraitAbilityRegistry registry = TraitAbilityRegistry.getRegistry();

        if (!registry.hasAbility(TEST_ABILITY)) {
            helper.fail("Datapack ability was not registered: " + TEST_ABILITY);
            return;
        }

        TraitAbility ability = registry.getAbility(TEST_ABILITY);

        if (!(ability instanceof TeleportAbility(Item displayItem, int cooldown, double range))) {
            helper.fail("Expected TeleportAbility for " + TEST_ABILITY + ", got " + ability.getClass().getName());
            return;
        }

        if (cooldown != 60) {
            helper.fail("Expected cooldown 60, got " + cooldown);
            return;
        }

        if (Double.compare(range, 4.0) != 0) {
            helper.fail("Expected range 4.0, got " + range);
            return;
        }

        if (displayItem != Items.ENDER_PEARL) {
            helper.fail("Expected display item minecraft:ender_eye");
            return;
        }

        helper.succeed();
    }
}
