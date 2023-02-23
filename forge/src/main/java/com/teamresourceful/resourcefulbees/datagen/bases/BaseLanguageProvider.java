package com.teamresourceful.resourcefulbees.datagen.bases;

import com.teamresourceful.resourcefulbees.client.data.LangGeneration;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class BaseLanguageProvider extends LanguageProvider {

    private final List<LanguageModule> modules = new ArrayList<>();
    private final Class<?>[] miscTranslationsClazz;

    protected BaseLanguageProvider(DataGenerator gen, @Nullable Class<?>... miscTranslationsClazz) {
        super(gen, ModConstants.MOD_ID, "en_us");
        this.miscTranslationsClazz = miscTranslationsClazz;
    }

    protected void addModule(LanguageModule module) {
        modules.add(module);
    }

    @Override
    protected void addTranslations() {
        if (miscTranslationsClazz != null) for (Class<?> clazz : miscTranslationsClazz) addTranslationsForClass(clazz);
        for (LanguageModule module : modules) {
            module.addEntries(this);
        }
    }

    private void addTranslationsForClass(Class<?> clazz) {
        for (Class<?> clazzz : clazz.getDeclaredClasses()) {
            addTranslationsForClass(clazzz);
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Translate.class)) {
                String key = null;
                if (field.getType().isAssignableFrom(String.class)) key = getOrNull(field);
                else if (field.getType().isAssignableFrom(MutableComponent.class)) key = getTranslationKeyOrNull(field);

                if (key != null) add(key, field.getAnnotation(Translate.class).value());
            }
        }
    }

    @Nullable
    private static String getOrNull(Field field) {
        try { return (String) field.get(null); }catch (Exception e) { return null; }
    }

    @Nullable
    private static String getTranslationKeyOrNull(Field field) {
        try {
            if (field.get(null) instanceof MutableComponent component && component.getContents() instanceof TranslatableContents translation) {
                return translation.getKey();
            }
            return null;
        }catch (Exception e) {
            return null;
        }
    }

    public void addAdvancement(String id, String title, String desc) {
        add(BaseAdvancementProvider.TRANSLATIONS_PREFIX+id+BaseAdvancementProvider.TITLE_SUFFIX, title);
        add(BaseAdvancementProvider.TRANSLATIONS_PREFIX+id+BaseAdvancementProvider.DESCRIPTION_SUFFIX, desc);
    }

    public void addBee(String id, String name) {
        add(LangGeneration.ITEM_RESOURCEFULBEES+id+"_bee_spawn_egg", name +" Bee Spawn Egg");
        add("entity.resourcefulbees."+id+"_bee", name +" Bee");
        add("bee_type.resourcefulbees."+id, name);
    }

    public void addHoneycomb(String id, String name) {
        add(LangGeneration.ITEM_RESOURCEFULBEES+id+"_honeycomb", name +" Honeycomb");
        add(LangGeneration.BLOCK_RESOURCEFULBEES+id+"_honeycomb_block", name +" Honeycomb Block");
        addHoneycombType(id, name);
    }

    public void addHoneycombType(String id, String name) {
        add(LangGeneration.COMB_RESOURCEFULBEES+id, name);
    }

    public void addTraitType(String id, String name) {
        add(LangGeneration.TRAIT_RESOURCEFULBEES+id, name);
    }

    public void addTraitAbility(String id, String name) {
        add("trait_ability.resourcefulbees."+id, name);
    }

    public void addTraitAbilityDescription(String id, String name) {
        add("trait_ability.resourcefulbees.desc."+id, name);
    }

    public void addTraitDamageType(String id, String name) {
        add("damage_type.resourcefulbees."+id, name);
    }

    public void addTraitDamageTypeDescription(String id, String name) {
        add("damage_type.resourcefulbees.desc."+id, name);
    }

    public void addHoney(String id, String name, boolean fluid, boolean block) {
        if (block) add(LangGeneration.BLOCK_RESOURCEFULBEES+id+"_honey_block", name +" Honey Block");
        if (fluid) {
            add("item.resourcefulbees."+id+"_honey_bucket", name+" Honey Bucket");
            add("fluid.resourcefulbees."+id+"_honey", name+" Honey");
            add("fluid_type.resourcefulbees."+id+"_honey", name+" Honey");
        }
        add(LangGeneration.ITEM_RESOURCEFULBEES+id+"_honey_bottle", name +" Honey Bottle");
        add(LangGeneration.HONEY_RESOURCEFULBEES+id, name);
    }

    public void addMutation(String id, String name) {
        add("mutation.resourcefulbees.mutation." + id, name);
    }

    public void addFluid(Supplier<FluidType> fluid, String name) {
        add(fluid.get().getDescriptionId(), name);
    }

    public void addPotion(RegistryEntry<Potion> key, String name){
        String id = key.getId().getPath();
        add("item.minecraft.potion.effect."+id, name+" Potion");
        add("item.minecraft.splash_potion.effect."+id, "Splash Potion of "+name);
        add("item.minecraft.lingering_potion.effect."+id, "Lingering Potion of "+name);
        add("item.minecraft.tipped_arrow.effect."+id, "Arrow of "+name);
    }

    public void add(CreativeModeTab group, String name) {
        final ComponentContents displayName = group.getDisplayName().getContents();
        if (displayName instanceof TranslatableContents translatableComponent) {
            add(translatableComponent.getKey(), name);
        }
    }

    public void addEnchantmentDesc(Supplier<? extends Enchantment> key, String desc) {
        add(key.get().getDescriptionId() + ".desc", desc);
    }
}
