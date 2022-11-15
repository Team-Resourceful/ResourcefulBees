package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.enchantments.HiveBreakEnchantment;
import com.teamresourceful.resourcefulbees.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.common.registry.api.ResourcefulRegistries;
import com.teamresourceful.resourcefulbees.common.registry.api.ResourcefulRegistry;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.core.Registry;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ModEnchantments {

    private ModEnchantments() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<Enchantment> ENCHANTMENTS = ResourcefulRegistries.create(Registry.ENCHANTMENT, ResourcefulBees.MOD_ID);

    public static final RegistryEntry<HiveBreakEnchantment> HIVE_BREAK = ENCHANTMENTS.register("hive_break", HiveBreakEnchantment::new);
}
