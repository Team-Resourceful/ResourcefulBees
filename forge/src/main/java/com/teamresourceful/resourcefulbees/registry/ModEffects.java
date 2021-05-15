package com.teamresourceful.resourcefulbees.registry;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.effects.Calming;
import com.teamresourceful.resourcefulbees.lib.ModConstants;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEffects {

    private ModEffects() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, ResourcefulBees.MOD_ID);

    public static final RegistryObject<MobEffect> CALMING = EFFECTS.register("calming", () -> new Calming(MobEffectCategory.BENEFICIAL, 16763783));
}
