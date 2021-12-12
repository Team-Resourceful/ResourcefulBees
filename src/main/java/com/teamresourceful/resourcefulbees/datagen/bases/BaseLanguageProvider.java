package com.teamresourceful.resourcefulbees.datagen.bases;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.data.LangGeneration;
import com.teamresourceful.resourcefulbees.common.lib.annotations.Translate;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseLanguageProvider extends LanguageProvider {

    private final List<LanguageModule> modules = new ArrayList<>();
    private final Class<?>[] miscTranslationsClazz;

    protected BaseLanguageProvider(DataGenerator gen, @Nullable Class<?>... miscTranslationsClazz) {
        super(gen, ResourcefulBees.MOD_ID, "en_us");
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
                else if (field.getType().isAssignableFrom(TranslatableComponent.class)) key = getTranslationKeyOrNull(field);

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
        try { return ((TranslatableComponent) field.get(null)).getKey(); }catch (Exception e) { return null; }
    }

    public void addAdvancement(String id, String title, String desc) {
        add(BaseAdvancementProvider.TRANSLATIONS_PREFIX+id+BaseAdvancementProvider.TITLE_SUFFIX, title);
        add(BaseAdvancementProvider.TRANSLATIONS_PREFIX+id+BaseAdvancementProvider.DESCRIPTION_SUFFIX, desc);
    }

    public void addBee(String id, String name) {
        add(LangGeneration.ITEM_RESOURCEFULBEES+id+"_bee_spawn_egg", name +" Bee Spawn Egg");
        add("entity.resourcefulbees."+id+"_bee", name +" Bee");
    }

    public void addHoneycomb(String id, String name) {
        add(LangGeneration.ITEM_RESOURCEFULBEES+id+"_honeycomb", name +" Honeycomb");
        add(LangGeneration.BLOCK_RESOURCEFULBEES+id+"_honeycomb_block", name +" Honeycomb Block");
    }

    public void addHoney(String id, String name, boolean fluid, boolean block) {
        if (block) add(LangGeneration.BLOCK_RESOURCEFULBEES+id+"_honey_block", name +" Honey Block");
        if (fluid) {
            add("item.resourcefulbees."+id+"_honey_bucket", name+" Honey Bucket");
            add("fluid.resourcefulbees."+id+"_honey", name+" Honey");
        }
        add(LangGeneration.ITEM_RESOURCEFULBEES+id+"_honey_bottle", name +" Honey Bottle");
    }

    public void addPotion(RegistryObject<Potion> key, String name){
        String id = key.getId().getPath();
        add("item.minecraft.potion.effect."+id, name+" Potion");
        add("item.minecraft.splash_potion.effect."+id, "Splash Potion of "+name);
        add("item.minecraft.lingering_potion.effect."+id, "Lingering Potion of "+name);
        add("item.minecraft.tipped_arrow.effect."+id, "Arrow of "+name);
    }

    public void add(CreativeModeTab group, String name) {
        final Component displayName = group.getDisplayName();
        if (displayName instanceof TranslatableComponent translatableComponent) {
            add(translatableComponent.getKey(), name);
        }
    }


    
}
