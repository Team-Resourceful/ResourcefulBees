package com.teamresourceful.resourcefulbees.datagen.providers.lang;

import com.teamresourceful.resourcefulbees.client.data.LangGeneration;
import com.teamresourceful.resourcefulbees.datagen.bases.BaseLanguageProvider;
import com.teamresourceful.resourcefulbees.datagen.bases.LanguageModule;

public class BeeLanguageModule implements LanguageModule {

    @Override
    public void addEntries(BaseLanguageProvider provider) {
        addBees(provider);
        //TODO why are the two lines below this the only ones like this?
        provider.add(LangGeneration.ITEM_RESOURCEFULBEES+"rgbee_bee_spawn_egg", "RGBee Spawn Egg");
        provider.add(LangGeneration.ENTITY_RESOURCEFULBEES+"rgbee_bee", "RGBee");
        addHoney(provider);
        addTraits(provider);
        addHoneycombs(provider);
        addTraitAbilities(provider);
        addTraitDamageTypes(provider);
        addMutations(provider);
    }

    private static void addMutations(BaseLanguageProvider provider) {
        provider.addMutation("gold", "Gold");
        provider.addMutation("iron", "Iron");
        provider.addMutation("emerald", "Emerald");
        provider.addMutation("diamond", "Diamond");
        provider.addMutation("redstone", "Redstone");
        provider.addMutation("nether_quartz", "Nether Quartz");
        provider.addMutation("lapis", "Lapis");
        provider.addMutation("coal", "Coal");
        provider.addMutation("ender", "Ender");
        provider.addMutation("beeper", "Beeper");
        provider.addMutation("pigman", "Pigman");
        provider.addMutation("skeleton", "Skeleton");
        provider.addMutation("wither", "Wither");
        provider.addMutation("zombee", "Zombee");
        provider.addMutation("netherite", "Netherite");
        provider.addMutation("oreo", "Oreo");
        provider.addMutation("slimy", "Slimy");
        provider.addMutation("icy", "Icy");
        provider.addMutation("kitten", "Kitten");
        provider.addMutation("dragon", "Dragon");
        provider.addMutation("dungeon", "Dungeon");
        provider.addMutation("brown_mushroom", "Brown Mushroom");
        provider.addMutation("red_mushroom", "Red Mushroom");
        provider.addMutation("crimson_fungus", "Crimson Fungus");
        provider.addMutation("warped_fungus", "Warped Fungus");
        provider.addMutation("yeti", "Yeti");
        provider.addMutation("starry", "Starry");
    }

    private static void addTraitDamageTypes(BaseLanguageProvider provider) {
        provider.addTraitDamageType("setOnFire", "Set On Fire");
        provider.addTraitDamageTypeDescription("setOnFire", "Sets player on fire on attack.");
        provider.addTraitDamageType("explosive", "Explosive");
        provider.addTraitDamageTypeDescription("explosive", "Explodes on attack.");
    }

    private static void addTraitAbilities(BaseLanguageProvider provider) {
        provider.addTraitAbility("teleport", "Teleport");
        provider.addTraitAbilityDescription("teleport", "During the day the bee can randomly teleport to an empty position within 4 blocks. This ability is stopped by the Ender Beecon and Bee Hives.");
        provider.addTraitAbility("flammable", "Flammable");
        provider.addTraitAbilityDescription("flammable", "This effect will randomly light the bee on fire.");
        provider.addTraitAbility("angry", "Angry");
        provider.addTraitAbilityDescription("angry", "Bees with this effect will become aggressive and cause bees around to join them. They can and will attack you, Calming effects will stop them from being angry.");
        provider.addTraitAbility("slimy", "Slimy");
        provider.addTraitAbilityDescription("slimy", "Slimy bees will make squishy sounds and spurt out slime particles at random.");
        provider.addTraitAbility("spider", "Spider");
        provider.addTraitAbilityDescription("spider", "Allows bees to fly through spiderwebs without issues.");
        provider.addTraitAbility("clingy", "Clingy");
        //todo add descr for clingy
    }

    private static void addHoneycombs(BaseLanguageProvider provider) {
        provider.addHoneycomb("iron", "Iron");
        provider.addHoneycomb("gold", "Gold");
        provider.addHoneycomb("emerald", "Emerald");
        provider.addHoneycomb("diamond", "Diamond");
        provider.addHoneycomb("redstone", "Redstone");
        provider.addHoneycomb("nether_quartz", "Nether Quartz");
        provider.addHoneycomb("lapis", "Lapis");
        provider.addHoneycomb("coal", "Coal");
        provider.addHoneycomb("ender", "Ender");
        provider.addHoneycomb("beeper", "Beeper");
        provider.addHoneycomb("pigman", "Pigman");
        provider.addHoneycomb("skeleton", "Skeleton");
        provider.addHoneycomb("wither", "Wither");
        provider.addHoneycomb("zombee", "Zombee");
        provider.addHoneycomb("netherite", "Netherite");
        provider.addHoneycomb("rgbee", "RGBee");
        provider.addHoneycomb("dragon", "Dragon");
        provider.addHoneycomb("catnip", "Catnip");
        provider.addHoneycomb("slimy", "Slimy");
        provider.addHoneycomb("starry", "Starry");
        provider.addHoneycombType("oreo", "Oreo");
        provider.addHoneycombType("abbee", "AbBee");
    }

    private static void addTraits(BaseLanguageProvider provider) {
        provider.addTraitType("wither", "Wither");
        provider.addTraitType("blaze", "Blaze");
        provider.addTraitType("can_swim", "Can Swim");
        provider.addTraitType("creeper", "Creeper");
        provider.addTraitType("zombee", "Zombee");
        provider.addTraitType("pigman", "Pigman");
        provider.addTraitType("ender", "Ender");
        provider.addTraitType("nether", "Nether");
        provider.addTraitType("oreo", "Oreo");
        provider.addTraitType("kitten", "Kitten");
        provider.addTraitType("slimy", "Slimy");
        provider.addTraitType("desert", "Desert");
        provider.addTraitType("angry", "Angry");
        provider.addTraitType("teleport", "Teleport");
        provider.addTraitType("flammable", "Flammable");
        provider.addTraitType("spider", "Spider");
        provider.addTraitType("zombie", "Hunger");
        provider.addTraitType("healer", "Healer");
        provider.addTraitType("starry", "Starry");
        provider.addTraitType("abbee", "AbBee");
    }

    private static void addHoney(BaseLanguageProvider provider) {
        provider.addHoney("catnip", "Catnip", true, true);
        provider.addHoney("rainbow", "Rainbow", true, true);
        provider.addHoney("starry", "Starry", true, true);
        provider.add("fluid.resourcefulbees.honey", "Honey");
    }

    private static void addBees(BaseLanguageProvider provider) {
        provider.addBee("gold", "Gold");
        provider.addBee("iron", "Iron");
        provider.addBee("emerald", "Emerald");
        provider.addBee("diamond", "Diamond");
        provider.addBee("redstone", "Redstone");
        provider.addBee("nether_quartz", "Nether Quartz");
        provider.addBee("lapis", "Lapis");
        provider.addBee("coal", "Coal");
        provider.addBee("ender", "Ender");
        provider.addBee("beeper", "Beeper");
        provider.addBee("pigman", "Pigman");
        provider.addBee("skeleton", "Skeleton");
        provider.addBee("wither", "Wither");
        provider.addBee("zombee", "Zombee");
        provider.addBee("netherite", "Netherite");
        provider.addBee("oreo", "Oreo");
        provider.addBee("slimy", "Slimy");
        provider.addBee("starry", "Starry");
        provider.addBee("icy", "Icy");
        provider.addBee("kitten", "Kitten");
        provider.addBee("dragon", "Dragon");
        provider.addBee("dungeon", "Dungeon");
        provider.addBee("brown_mushroom", "Brown Mushroom");
        provider.addBee("red_mushroom", "Red Mushroom");
        provider.addBee("crimson_fungus", "Crimson Fungus");
        provider.addBee("warped_fungus", "Warped Fungus");
        provider.addBee("yeti", "Yeti");
        provider.addBee("abbee", "AbBee");
    }
}
