package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ModEnchantments {

    public static final ResourceKey<Enchantment> HIVE_BREAK =
            ResourceKey.create(
                    Registries.ENCHANTMENT,
                    ModIdentifier.of("hive_break")
            );

    private ModEnchantments() throws UtilityClassException {
        throw new UtilityClassException();
    }
}