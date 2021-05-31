package com.teamresourceful.resourcefulbees.registry;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.lib.constants.ModConstants;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class ModEffects {

    private ModEffects() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, ResourcefulBees.MOD_ID);

    public static final RegistryObject<MobEffect> CALMING = EFFECTS.register("calming", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 16763783) {
        @Override
        public void applyEffectTick(@NotNull LivingEntity entity, int level) {
            if (entity instanceof NeutralMob) ((NeutralMob) entity).stopBeingAngry();
            super.applyEffectTick(entity, level);
        }

        @Override
        public boolean isDurationEffectTick(int duration, int level) {
            return duration % 5 == 0;
        }
    });
}
