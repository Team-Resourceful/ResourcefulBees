package com.resourcefulbees.resourcefulbees.init;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.resourcefulbees.resourcefulbees.api.traitdata.BeeTrait;
import com.resourcefulbees.resourcefulbees.api.traitdata.JsonBeeTrait;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.TraitRegistry;
import net.minecraft.item.Item;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;

public class TraitSetup {

    private TraitSetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static Path dictionaryPath;

    public static void buildCustomTraits() {
        LOGGER.info("Registering Custom Traits...");
        addTraits();
    }

    private static void parseType(Reader reader, String name) {
        Gson gson = new Gson();
        name = name.toLowerCase(Locale.ENGLISH).replace(" ", "_");
        try {
            JsonBeeTrait.JsonTrait jsonTrait = gson.fromJson(reader, JsonBeeTrait.JsonTrait.class);
            BeeTrait.Builder builder = new BeeTrait.Builder(name);
            parseDamageImmunities(jsonTrait, builder);
            parseDamageTypes(jsonTrait, builder);
            parseSpecialAbilities(jsonTrait, builder);
            parseParticle(jsonTrait, builder);
            parsePotionImmunities(jsonTrait, builder);
            parsePotionDamageEffects(jsonTrait, builder);
            parseBeeAuras(jsonTrait, builder);
            parseBeepediaItem(jsonTrait, builder);
            TraitRegistry.getRegistry().register(name, builder.build());
        } catch (JsonSyntaxException e) {
            String exception = String.format("Error was found trying to parse trait: %s. Json is invalid, validate it here : https://jsonlint.com/", name);
            throw new JsonSyntaxException(exception);
        }
    }

    private static void parseDamageImmunities(JsonBeeTrait.JsonTrait jsonTrait, BeeTrait.Builder builder) {
        if (jsonTrait.getDamageImmunities() != null && jsonTrait.getDamageImmunities().length > 0) {
            Arrays.stream(jsonTrait.getDamageImmunities()).forEach(builder::addDamageImmunity);
        }
    }

    private static void parseDamageTypes(JsonBeeTrait.JsonTrait jsonTrait, BeeTrait.Builder builder) {
        if (jsonTrait.getDamageTypes() != null && !jsonTrait.getDamageTypes().isEmpty()) {
            jsonTrait.getDamageTypes().forEach(damageType ->
                    builder.addDamageType(Pair.of(damageType.getDamageType(), damageType.getAmplifier())));
        }
    }

    private static void parseSpecialAbilities(JsonBeeTrait.JsonTrait jsonTrait, BeeTrait.Builder builder) {
        if (jsonTrait.getSpecialAbilities() != null && jsonTrait.getSpecialAbilities().length > 0) {
            for (String ability : jsonTrait.getSpecialAbilities()) {
                builder.addSpecialAbility(ability);
            }
        }
    }

    private static void parseParticle(JsonBeeTrait.JsonTrait jsonTrait, BeeTrait.Builder builder) {
        if (jsonTrait.getParticleName() != null
                && !jsonTrait.getParticleName().isEmpty()
                && ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(jsonTrait.getParticleName())) instanceof BasicParticleType) {
            builder.setParticleEffect((BasicParticleType) ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(jsonTrait.getParticleName())));
        }
    }

    private static void parsePotionImmunities(JsonBeeTrait.JsonTrait jsonTrait, BeeTrait.Builder builder) {
        if (jsonTrait.getPotionImmunities() != null && jsonTrait.getPotionImmunities().length > 0) {
            for (String immunity : jsonTrait.getPotionImmunities()) {
                Effect potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(immunity));
                if (potion != null)
                    builder.addPotionImmunity(potion);
            }
        }
    }

    private static void parsePotionDamageEffects(JsonBeeTrait.JsonTrait jsonTrait, BeeTrait.Builder builder) {
        if (jsonTrait.getPotionDamageEffects() != null && !jsonTrait.getPotionDamageEffects().isEmpty()) {
            jsonTrait.getPotionDamageEffects().forEach((traitPotionDamageEffect -> {
                Effect potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(traitPotionDamageEffect.getEffectID()));
                if (potion != null)
                    builder.addDamagePotionEffect(Pair.of(potion, MathHelper.clamp(traitPotionDamageEffect.getStrength(), 0, 255)));
            }));
        }
    }

    private static void parseBeeAuras(JsonBeeTrait.JsonTrait jsonTrait, BeeTrait.Builder builder) {
        if (jsonTrait.getBeeAuras() != null && !jsonTrait.getBeeAuras().isEmpty()) {
            jsonTrait.getBeeAuras().forEach(beeAura -> {
                if (beeAura.effectID != null) {
                    beeAura.potionEffect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(beeAura.effectID));
                }
                builder.addAura(beeAura);
            });
        }
    }

    private static void parseBeepediaItem(JsonBeeTrait.JsonTrait jsonTrait, BeeTrait.Builder builder) {
        if (jsonTrait.getBeepediaItemID() != null && !jsonTrait.getBeepediaItemID().isEmpty()) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(jsonTrait.getBeepediaItemID()));
            if (item != null) {
                builder.setBeepediaItem(item);
            }
        }
    }

    private static void addTraits() {
        try (Stream<Path> zipStream = Files.walk(dictionaryPath);
             Stream<Path> jsonStream = Files.walk(dictionaryPath)) {
            zipStream.filter(f -> f.getFileName().toString().endsWith(".zip"))
                    .forEach(TraitSetup::addZippedType);
            jsonStream.filter(f -> f.getFileName().toString().endsWith(".json"))
                    .forEach(TraitSetup::addType);
        } catch (IOException e) {
            LOGGER.error("Could not stream custom traits!!", e);
        }
    }

    private static void addType(Path file) {
        File f = file.toFile();
        try {
            ModSetup.parseType(f, TraitSetup::parseType);
        } catch (IOException e) {
            LOGGER.error("File not found when parsing biome types");
        }
    }

    private static void addZippedType(Path file) {
        try (ZipFile zf = new ZipFile(file.toString())) {
            zf.stream().forEach(zipEntry -> {
                if (zipEntry.getName().endsWith(".json")) {
                    try {
                        ModSetup.parseType(zf, zipEntry, TraitSetup::parseType);
                    } catch (IOException e) {
                        String name = zipEntry.getName();
                        name = name.substring(name.lastIndexOf("/") + 1, name.indexOf('.'));
                        LOGGER.error("Could not parse {} biome type from ZipFile", name);
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.warn("Could not read ZipFile! ZipFile: {}", file.getFileName());
        }
    }

    public static void setDictionaryPath(Path dictionaryPath) {
        TraitSetup.dictionaryPath = dictionaryPath;
    }
}
