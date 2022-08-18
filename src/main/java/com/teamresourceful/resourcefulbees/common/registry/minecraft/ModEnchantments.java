package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.enchantments.HiveBreakEnchantment;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEnchantments {

    private ModEnchantments() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ResourcefulBees.MOD_ID);

    public static final RegistryObject<HiveBreakEnchantment> HIVE_BREAK = ENCHANTMENTS.register("hive_break", HiveBreakEnchantment::new);
}
