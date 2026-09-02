package com.teamresourceful.resourcefulbees.datagen.providers.lang;

import com.teamresourceful.resourcefulbees.common.lib.constants.translations.*;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModCreativeTabs;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEffects;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModPotions;
import com.teamresourceful.resourcefulbees.datagen.providers.base.BaseLanguageProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

public class RBeesLanguageProvider extends BaseLanguageProvider {

    public RBeesLanguageProvider(PackOutput output) {
        super(output,
                ApiaryTranslations.class,
                BeehiveTranslations.class,
                BeeLocatorTranslations.class,
                BeepediaTranslations.class,
                BeeconTranslations.class,
                FakeFlowerTranslations.class,
                FlowHiveTranslations.class,
                GuiTranslations.class,
                HoneyDipperTranslations.class,
                ItemTranslations.class,
                JeiTranslations.class,
                MissingRegistryTranslations.class,
                ModTranslations.class,
                TopTranslations.class
        );
        addModule(new AdvancementLanguageModule());
        addModule(new BlockLanguageModule());
        addModule(new BeeLanguageModule());
        addModule(new BookLanguageModule());
        addModule(new ItemLanguageModule());
        addModule(new EnchantmentLanguageModule());
    }

    @Override
    public @NotNull String getName() {
        return "Resourceful Bees Language Provider";
    }

    @Override
    protected void addTranslations() {
        addPotions();
        addCreativeTabs();
        add("tooltip.resourcefulbees.bee.creator", "§7- %s");
        add("entity.minecraft.villager.resourcefulbees.beekeeper", "Beekeeper");
        super.addTranslations();
    }

    private void addCreativeTabs() {
        add(ModCreativeTabs.RESOURCEFUL_BEES.get(), "Resourceful Bees");
        add(ModCreativeTabs.RESOURCEFUL_BEES_HONEY.get(), "Resourceful Bees - Honey");
        add(ModCreativeTabs.RESOURCEFUL_BEES_BEES.get(), "Resourceful Bees - Spawn Eggs");
        add(ModCreativeTabs.RESOURCEFUL_BEES_COMBS.get(), "Resourceful Bees - Combs");
        add(ModCreativeTabs.RESOURCEFUL_BEES_HIVES.get(), "Resourceful Bees - Hives");
    }

    private void addPotions() {
        addEffect(ModEffects.CALMING, "Calming");
        addPotion(ModPotions.CALMING_POTION, "Calming");
        addPotion(ModPotions.LONG_CALMING_POTION, "Calming");
    }

}
