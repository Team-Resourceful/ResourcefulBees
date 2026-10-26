package com.teamresourceful.resourcefulbees.datagen.providers.lang;

import com.teamresourceful.resourcefulbees.client.data.LangGenerator;
import com.teamresourceful.resourcefulbees.datagen.providers.base.BaseLanguageProvider;
import com.teamresourceful.resourcefulbees.datagen.providers.base.LanguageModule;

public class BeeLanguageModule implements LanguageModule {

    @Override
    public void addEntries(BaseLanguageProvider provider) {
        addBees(provider);
        provider.add(LangGenerator.ITEM_RESOURCEFULBEES+"rgbee_bee_spawn_egg", "RGBee Spawn Egg");
        provider.add(LangGenerator.ENTITY_RESOURCEFULBEES+"rgbee_bee", "RGBee");
        provider.add(LangGenerator.BEE_RESOURCEFULBEES+"rgbee_bee", "RGBee");
        addHoney(provider);
        addTraits(provider);
        addHoneycombs(provider);
        addTraitAbilities(provider);
        addTraitDamageTypes(provider);
        addMutations(provider);
    }

    private static void addMutations(BaseLanguageProvider provider) {
        provider.addMutation("gold", "Gold Mutation");
        provider.addMutation("iron", "Iron Mutation");
        provider.addMutation("emerald", "Emerald Mutation");
        provider.addMutation("diamond", "Diamond Mutation");
        provider.addMutation("redstone", "Redstone Mutation");
        provider.addMutation("quartz", "Nether Quartz Mutation");
        provider.addMutation("lapis", "Lapis Mutation");
        provider.addMutation("coal", "Coal Mutation");
        provider.addMutation("ender", "Ender Mutation");
        provider.addMutation("beeper", "Beeper Mutation");
        provider.addMutation("pigman", "Pigman Mutation");
        provider.addMutation("skeleton", "Skeleton Mutation");
        provider.addMutation("wither", "Wither Mutation");
        provider.addMutation("zombee", "Zombee Mutation");
        provider.addMutation("netherite", "Netherite Mutation");
        provider.addMutation("oreo", "Oreo Mutation");
        provider.addMutation("slimy", "Slimy Mutation");
        provider.addMutation("icy", "Icy Mutation");
        provider.addMutation("kitten", "Kitten Mutation");
        provider.addMutation("dragon", "Dragon Mutation");
        provider.addMutation("dungeon", "Dungeon Mutation");
        provider.addMutation("brown_mushroom", "Brown Mushroom Mutation");
        provider.addMutation("red_mushroom", "Red Mushroom Mutation");
        provider.addMutation("crimson_fungus", "Crimson Fungus Mutation");
        provider.addMutation("warped_fungus", "Warped Fungus Mutation");
        provider.addMutation("yeti", "Yeti Mutation");
        provider.addMutation("starry", "Starry Mutation");
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
        provider.addTraitType("sculk", "Sculk");
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
