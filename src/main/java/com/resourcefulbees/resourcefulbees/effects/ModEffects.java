package com.resourcefulbees.resourcefulbees.effects;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEffects {

    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, ResourcefulBees.MOD_ID);

    public static final RegistryObject<Effect> CALMING = EFFECTS.register("calming", () -> new Calming(EffectType.BENEFICIAL, 16763783));
}
