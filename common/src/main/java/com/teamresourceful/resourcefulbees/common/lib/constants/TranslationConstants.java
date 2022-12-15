package com.teamresourceful.resourcefulbees.common.lib.constants;

import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class TranslationConstants {

    /**
     * NOTE: TranslatableComponents should be USED ONLY for static contexts they should be never used if .append is happening to them.
     * Use a string of the translation key if appending is needed and use string formats where possible instead of .append
     *
     * This class is used to store translation components and keys and every key and component should be annotated with @Translate
     * with their respective english translation this annotation is used in the data generator to generate the english language json.
     */

    private TranslationConstants() {
        throw new UtilityClassError();
    }

    public static class LightLevel {

        @Translate("Any")
        public static final MutableComponent ANY = Component.translatable("gui.resourcefulbees.light.any");

        @Translate("Day")
        public static final MutableComponent DAY = Component.translatable("gui.resourcefulbees.light.day");

        @Translate("Night")
        public static final MutableComponent NIGHT = Component.translatable("gui.resourcefulbees.light.night");
    }

    public static class Booleans {

        @Translate("Yes")
        public static final MutableComponent YES = Component.translatable("gui.resourcefulbees.yes");

        @Translate("No")
        public static final MutableComponent NO = Component.translatable("gui.resourcefulbees.no");

    }

    public static class Sizes {

        @Translate("Tiny")
        public static final MutableComponent TINY = Component.translatable("gui.resourcefulbees.size.tiny");

        @Translate("Small")
        public static final MutableComponent SMALL = Component.translatable("gui.resourcefulbees.size.small");

        @Translate("Regular")
        public static final MutableComponent REGULAR = Component.translatable("gui.resourcefulbees.size.regular");

        @Translate("Large")
        public static final MutableComponent LARGE = Component.translatable("gui.resourcefulbees.size.large");

        @Translate("Giant")
        public static final MutableComponent GIANT = Component.translatable("gui.resourcefulbees.size.giant");
    }

    public static class Jei {

        @Translate("Bee Breeding")
        public static final MutableComponent BREEDING = Component.translatable("gui.resourcefulbees.jei.category.breeding");

        @Translate("Centrifuge")
        public static final MutableComponent CENTRIFUGE = Component.translatable("gui.resourcefulbees.jei.category.centrifuge");

        @Translate("Bee Flowers")
        public static final MutableComponent FLOWERS = Component.translatable("gui.resourcefulbees.jei.category.bee_pollination_flowers");

        @Translate("Beehive Outputs")
        public static final MutableComponent HIVE = Component.translatable("gui.resourcefulbees.jei.category.hive");

        @Translate("Mutations")
        public static final MutableComponent MUTATIONS = Component.translatable("gui.resourcefulbees.jei.category.mutations");

        @Translate("Solidification Chamber")
        public static final MutableComponent SOLIDIFICATION = Component.translatable("gui.resourcefulbees.jei.category.solidification");

        @Translate("Chance that this will succeed in creating a new bee.")
        public static final MutableComponent BREED_CHANCE_INFO = Component.translatable("gui.resourcefulbees.jei.category.breed_chance.info");

        @Translate("[Press Shift to show NBT]")
        public static final MutableComponent NBT = Component.translatable("gui.resourcefulbees.jei.tooltip.show_nbt");

        @Translate("Bee block mutation requires there to be a valid hive for the bee to go into & the bee to have nectar.")
        public static final MutableComponent MUTATION_INFO = Component.translatable("gui.resourcefulbees.jei.category.mutation.info");

        @Translate("Chance that this will be chosen succeed in mutating.")
        public static final MutableComponent MUTATION_WEIGHT_CHANCE_INFO = Component.translatable("gui.resourcefulbees.jei.category.mutation_weight_chance.info");

        @Translate("Weight: %d")
        public static final String CENTRIFUGE_WEIGHT = "gui.resourcefulbees.jei.category.centrifuge.weight";

        @Translate("Weight: SLOT EMPTY")
        public static final MutableComponent CENTRIFUGE_WEIGHT_EMPTY = Component.translatable("gui.resourcefulbees.jei.category.centrifuge.weight.empty");

        @Translate("Pool Chance: %d")
        public static final String CENTRIFUGE_CHANCE = "gui.resourcefulbees.jei.category.centrifuge.chance";

        @Translate("Pool Chance: SLOT EMPTY")
        public static final MutableComponent CENTRIFUGE_CHANCE_EMPTY = Component.translatable("gui.resourcefulbees.jei.category.centrifuge.chance.empty");

        @Translate("Click or Right-Click for more info!")
        public static final MutableComponent CLICK_INFO = Component.translatable("tooltip.resourcefulbees.jei.click_bee_info");
    }

    public static class Top {

        @Translate("Smoke Time:")
        public static final MutableComponent SMOKE_TIME = Component.translatable("gui.resourcefulbees.top.smoke_time");

        @Translate("Tier: %s")
        public static final String TIER = "gui.resourcefulbees.top.tier";

        @Translate("Bees: %s / %s")
        public static final String BEES = "gui.resourcefulbees.top.bees";

        @Translate("Smoked: %s")
        public static final String SMOKED = "gui.resourcefulbees.top.smoked";

        @Translate("Honey Level: %s")
        public static final String HONEY_LEVEL = "gui.resourcefulbees.top.honeylevel";
    }

    public static class Apiary {

        @Translate("Honeycomb")
        public static final MutableComponent HONEYCOMB = Component.translatable("item.resourcefulbees.tooltip.honeycomb");

        @Translate("Honeycomb Block")
        public static final MutableComponent HONEYCOMB_BLOCK = Component.translatable("item.resourcefulbees.tooltip.honeycomb_block");

        @Translate("Output Quantity: %s")
        public static final String OUTPUT_QUANTITY = "block.resourcefulbees.apiary.tooltip.output_quantity";

        @Translate("Output Type: %s")
        public static final String OUTPUT_TYPE = "block.resourcefulbees.apiary.tooltip.output_type";

        @Translate("Ticks in Hive: %s")
        public static final String TICKS_HIVE = "gui.resourcefulbees.apiary.bee.ticks_in_hive";

        @Translate("Ticks Left: %s")
        public static final String TICKS_LEFT = "gui.resourcefulbees.apiary.bee.ticks_left";
    }

    public static class BeeHive {

        @Translate("Max Bees: %s")
        public static final String MAX_BEES = "block.resourcefulbees.beehive.tooltip.max_bees";

        @Translate("Max Honeycombs: %s")
        public static final String MAX_COMBS = "block.resourcefulbees.beehive.tooltip.max_combs";

        @Translate("Hive Time Modification: %s%s%%")
        public static final String HIVE_TIME ="block.resourcefulbees.beehive.tooltip.hive_time";

        @Translate(" UNIQUE \u00A76Bees")
        public static final MutableComponent UNIQUE = Component.translatable("block.resourcefulbees.beehive.tooltip.unique_bees");

        @Translate("Bees:")
        public static final MutableComponent BEES = Component.translatable("block.resourcefulbees.beehive.tooltip.bees");

        @Translate("Honeycombs:")
        public static final MutableComponent HONEYCOMBS = Component.translatable("block.resourcefulbees.beehive.tooltip.honeycombs");
    }

    public static class Guis {

        public static final MutableComponent INVENTORY = Component.translatable("container.inventory");

        @Translate("Ender Beecon")
        public static final MutableComponent BEECON = Component.translatable("gui.resourcefulbees.ender_beecon");

        @Translate("Solidification Chamber")
        public static final MutableComponent SOLIDIFICATION_CHAMBER = Component.translatable("gui.resourcefulbees.solidification_chamber");

        @Translate("Honey Generator")
        public static final MutableComponent GENERATOR = Component.translatable("gui.resourcefulbees.honey_generator");

        @Translate("Honey Pot")
        public static final MutableComponent POT = Component.translatable("gui.resourcefulbees.honey_pot");

        @Translate("Apiary")
        public static final MutableComponent APIARY = Component.translatable("gui.resourcefulbees.apiary");

        @Translate("Apiary Breeder")
        public static final MutableComponent APIARY_BREEDER = Component.translatable("gui.resourcefulbees.apiary_breeder");

        @Translate("Empty")
        public static final MutableComponent NO_FLUID = Component.translatable("gui.resourcefulbees.fluids.empty");

        public static class EnderBeecon {

            @Translate("Bee Effects")
            public static final MutableComponent PRIMARY_LABEL = Component.translatable("block.resourcefulbees.ender_beecon.primary");

            @Translate("Drain: ")
            public static final MutableComponent DRAIN_LABEL = Component.translatable("block.resourcefulbees.ender_beecon.drain");

            @Translate("Range: ")
            public static final MutableComponent RANGE_LABEL = Component.translatable("block.resourcefulbees.ender_beecon.range");

            @Translate("Is Active: ")
            public static final MutableComponent ACTIVE_LABEL = Component.translatable("block.resourcefulbees.ender_beecon.is_active");

            @Translate("Fluid: ")
            public static final MutableComponent FLUID_LABEL = Component.translatable("block.resourcefulbees.ender_beecon.fluid");

            @Translate("Amount: ")
            public static final MutableComponent FLUID_AMOUNT_LABEL = Component.translatable("block.resourcefulbees.ender_beecon.fluid_amount");

            @Translate("Empty")
            public static final MutableComponent NO_FLUID_LABEL = Component.translatable("block.resourcefulbees.ender_beecon.no_fluid");
        }
    }

    public static class Items {

        @Translate("Creative Beepedia")
        public static final MutableComponent CREATIVE_BEEPEDIA = Component.translatable("item.resourcefulbees.creative_beepedia");

        @Translate("Use on a bee to see it's information")
        public static final MutableComponent INFO_BEEPEDIA = Component.translatable("item.resourcefulbees.information.beepedia");

        @Translate("Hold SHIFT for more info")
        public static final MutableComponent MORE_INFO = Component.translatable("item.resourcefulbees.shift_info");

        @Translate("Hold CTRL for Multiblock Info")
        public static final MutableComponent MULTIBLOCK_INFO = Component.translatable("item.resourcefulbees.ctrl_info");


        @Translate("Right click the hive to smoke it.")
        public static final MutableComponent SMOKER_TOOLTIP = Component.translatable("item.resourcefulbees.smoker.tooltip");

        @Translate("Bees won't get angry for 30s when shearing honeycombs.")
        public static final MutableComponent SMOKER_TOOLTIP1 = Component.translatable("item.resourcefulbees.smoker.tooltip.1");

        @Translate("Queen Bee Banner")
        public static final MutableComponent QUEEN_BEE_BANNER = Component.translatable("block.resourcefulbees.queen_bee_banner");

        @Translate("Use on a previous tier nest to upgrade it")
        public static final MutableComponent HIVE_UPGRADE = Component.translatable("item.resourcefulbees.hive_upgrade.tooltip.info");

        @Translate("Reduces max breed time by %s ticks.")
        public static final String BREED_TIME_UPGRADE = "item.resourcefulbees.apiary_breed_time_upgrade.tooltip.info";

        @Translate("Consumes honey, grants bees safety.")
        public static final MutableComponent BEECON_TOOLTIP = Component.translatable("block.resourcefulbees.beecon.tooltip.info");

        @Translate("[Prevents bees from teleporting in effect range]")
        public static final MutableComponent BEECON_TOOLTIP_1 = Component.translatable("block.resourcefulbees.beecon.tooltip.info.1");

        @Translate("Generates RF using honey bottles.")
        public static final MutableComponent GEN_TOOLTIP = Component.translatable("block.resourcefulbees.generator.tooltip.info");

        @Translate("Max Breed Time: %s ticks")
        public static final String BREEDER_TOOLTIP_1 = "block.resourcefulbees.apiary_breeder.tooltip.info.1";

        @Translate("Can be upgraded with breed time upgrades.")
        public static final MutableComponent BREEDER_TOOLTIP_2 = Component.translatable("block.resourcefulbees.apiary_breeder.tooltip.info.2");

        @Translate("Right-click on Tiered Beehives to collect honeycombs.")
        public static final MutableComponent SCRAPER_TOOLTIP = Component.translatable("block.resourcefulbees.scraper.tooltip.info");

        @Translate("Must collect ALL honeycombs for bees to generate new ones!")
        public static final MutableComponent SCRAPER_TOOLTIP_1 = Component.translatable("block.resourcefulbees.scraper.tooltip.info.2");

        @Translate("Filled Bee Jar")
        public static final String BEE_JAR_FILLED = "item.resourcefulbees.bee_jar_filled";

        @Translate("Empty Bee Jar")
        public static final String BEE_JAR_EMPTY = "item.resourcefulbees.bee_jar_empty";

        @Translate("§8Hold [§7SHIFT§8] for %s")
        public static final String SHIFT_TOOLTIP = "item.resourcefulbees.shift_tooltip";

        @Translate("§8Hold [§fSHIFT§8] for %s")
        public static final String SHIFT_TOOLTIP_HIGHLIGHT = "item.resourcefulbees.shift_tooltip_highlight";

        @Translate("§8Hold [§7CTRL§8] for %s")
        public static final String CTRL_TOOLTIP = "item.resourcefulbees.ctrl_tooltip";

        @Translate("§8Hold [§fCTRL§8] for %s")
        public static final String CTRL_TOOLTIP_HIGHLIGHT = "item.resourcefulbees.ctrl_tooltip_highlight";

        @Translate("§8more info.")
        public static final MutableComponent FOR_MORE_INFO = Component.translatable("item.resourcefulbees.tooltip_info");

        @Translate("§8statistics.")
        public static final MutableComponent TOOLTIP_STATS = Component.translatable("item.resourcefulbees.tooltip_stats");

        @Translate(" - %s")
        public static final String BEE_BOX_ENTITY_NAME = "item.resourcefulbees.bee_box.entity_name";

        @Translate("Can hold up to %s bees.")
        public static final String BEE_BOX_TOOLTIP = "item.resourcefulbees.information.bee_box.info";

        @Translate("Contains lost bees. Consumed on use.")
        public static final MutableComponent BEE_BOX_TOOLTIP_TEMP = Component.translatable("item.resourcefulbees.information.bee_box.temp_info");

        @Translate("Bees:")
        public static final MutableComponent BEES = Component.translatable("item.resourcefulbees.information.bee_box.bees");

        @Translate("No Bees.")
        public static final MutableComponent NO_BEES = Component.translatable("item.resourcefulbees.information.bee_box.no_bees");
    }

    public static class HoneyDipper {

        @Translate("Hive position for [%s] has been set to %s")
        public static final String HIVE_SET = "items.resourcefulbees.honey_dipper.hive_set";

        @Translate("Flower position for [%s] has been set to %s")
        public static final String FLOWER_SET = "items.resourcefulbees.honey_dipper.flower_set";

        @Translate("Fake Golden Flower Position for [%s] has been set to %s")
        public static final String FAKE_FLOWER_SET = "items.resourcefulbees.honey_dipper.fake_flower_set";

        @Translate("Bee Selection Cleared!")
        public static final MutableComponent SELECTION_CLEARED = Component.translatable("items.resourcefulbees.honey_dipper.cleared");

        @Translate("[%s] has been selected!")
        public static final String BEE_SET = "items.resourcefulbees.honey_dipper.bee_set";
    }

    public static class Beepedia {

        @Translate("Bees Found: \u00A76 %s / %s")
        public static final String PROGRESS = "gui.resourcefulbees.beepedia.home.progress";

        @Translate("Beepedia v2.0")
        public static final MutableComponent NAME = Component.translatable("gui.resourcefulbees.beepedia");

        @Translate("Bees")
        public static final MutableComponent BEES = Component.translatable("gui.resourcefulbees.beepedia.tab.traits.bees_list");

        @Translate("Traits")
        public static final MutableComponent TRAITS = Component.translatable("gui.resourcefulbees.beepedia.tab.traits.effects_list");

        @Translate("Search")
        public static final MutableComponent SEARCH = Component.translatable("gui.resourcefulbees.beepedia.search");

        @Translate("Honey")
        public static final MutableComponent HONEY = Component.translatable("gui.resourcefulbees.beepedia.honey");

        @Translate("No be with that name found!")
        public static final MutableComponent COMMAND_NONE_FOUND = Component.translatable("argument.resourcefulbees.beepedia.bee_not_found");

        public static class Home {

            @Translate("Join us on Discord!")
            public static final MutableComponent DISCORD = Component.translatable("gui.resourcefulbees.beepedia.home.discord");

            @Translate("Support us on Patreon!")
            public static final MutableComponent PATREON = Component.translatable("gui.resourcefulbees.beepedia.home.patreon");

            @Translate("Found a bug/issue? Submit it here.")
            public static final MutableComponent ISSUES = Component.translatable("gui.resourcefulbees.beepedia.home.issues");

            @Translate("Browse the wiki.")
            public static final MutableComponent WIKI = Component.translatable("gui.resourcefulbees.beepedia.home.wiki");
        }

        public static class Honeycombs {

            @Translate("Honeycomb")
            public static final MutableComponent TITLE = Component.translatable("gui.resourcefulbees.beepedia.bee_subtab.honeycomb");
        }

        public static class Traits {

            @Translate("Traits")
            public static final MutableComponent TITLE = Component.translatable("gui.resourcefulbees.beepedia.tab.traits");

            @Translate("Amplifier: %d")
            public static final String AMPLIFIER = "gui.resourcefulbees.beepedia.tab.traits.amplifier";

            @Translate("Potion Damage Effects")
            public static final MutableComponent POTION_DAMAGE_EFFECTS = Component.translatable("gui.resourcefulbees.beepedia.tab.traits.potion_damage_effects");

            @Translate("Immunities")
            public static final MutableComponent IMMUNITIES = Component.translatable("gui.resourcefulbees.beepedia.tab.traits.immunities");

            @Translate("Damage Types")
            public static final MutableComponent DAMAGE_TYPES = Component.translatable("gui.resourcefulbees.beepedia.tab.traits.damage_types");

            @Translate("Abilities")
            public static final MutableComponent ABILITIES = Component.translatable("gui.resourcefulbees.beepedia.tab.traits.abilities");

            @Translate("Auras")
            public static final MutableComponent AURAS = Component.translatable("gui.resourcefulbees.beepedia.tab.traits.auras");

        }

        public static class Info {

            @Translate("Info")
            public static final MutableComponent TITLE = Component.translatable("gui.resourcefulbees.beepedia.bee_subtab.info");

            @Translate("Flower: ")
            public static final MutableComponent FLOWER = Component.translatable("gui.resourcefulbees.beepedia.bee_subtab.info.flower");

            @Translate("Health: %s")
            public static final String HEALTH = "gui.resourcefulbees.beepedia.bee_subtab.info.health";

            @Translate("Damage: %s")
            public static final String DAMAGE = "gui.resourcefulbees.beepedia.bee_subtab.info.damage";

            @Translate("Loses Stinger: %s")
            public static final String STINGER = "gui.resourcefulbees.beepedia.bee_subtab.info.stinger";

            @Translate("Passive: %s")
            public static final String PASSIVE = "gui.resourcefulbees.beepedia.bee_subtab.info.passive";

            @Translate("Poisonous: %s")
            public static final String POISON = "gui.resourcefulbees.beepedia.bee_subtab.info.poison";

            @Translate("Size: %s")
            public static final String SIZE = "gui.resourcefulbees.beepedia.bee_subtab.info.size";

            @Translate("Time In Hive: %ss")
            public static final String TIME = "gui.resourcefulbees.beepedia.bee_subtab.info.time";
        }

    }

    public static class FakeFLower {
        @Translate("Fake Golden Flower")
        public static final MutableComponent TITLE = Component.translatable("gui.resourcefulbees.fake_flower");
    }

    public static class Centrifuge {

        @Translate("Close")
        public static final MutableComponent CLOSE = Component.translatable("gui.resourcefulbees.centrifuge.close");
    }

    public static class BeeLocator {

        @Translate("Cancel")
        public static final MutableComponent CANCEL = Component.translatable("gui.resourcefulbees.bee_locator.cancel");

        @Translate("Search")
        public static final MutableComponent SEARCH = Component.translatable("gui.resourcefulbees.bee_locator.search");

        @Translate("None")
        public static final MutableComponent NONE = Component.translatable("gui.resourcefulbees.bee_locator.none");

        @Translate("Selected: %s")
        public static final String SELECTED = "gui.resourcefulbees.bee_locator.selected";

        @Translate("Loc: %s / %s")
        public static final String LOCATION = "gui.resourcefulbees.bee_locator.location";

        @Translate("Biome: %s")
        public static final String BIOME = "gui.resourcefulbees.bee_locator.biome";

        @Translate("Dist: %sm")
        public static final String DISTANCE = "gui.resourcefulbees.bee_locator.distance";
    }

    public static class MissingRegistry {

        @Translate("Quit")
        public static final MutableComponent QUIT = Component.translatable("gui.resourcefulbees.missing_registry.quit");

        @Translate("Proceed")
        public static final MutableComponent PROCEED = Component.translatable("gui.resourcefulbees.missing_registry.proceed");

        @Translate("Resourceful Bees custom registry entries missing!")
        public static final MutableComponent TITLE = Component.translatable("gui.resourcefulbees.missing_registry.title");

        @Translate("Bees, Honey, or Combs in this instance that were loaded previously are missing\nthis startup, are you sure you would you like to proceed?\nInformation on which entries are missing provided in the logs.")
        public static final MutableComponent DESCRIPTION = Component.translatable("gui.resourcefulbees.missing_registry.description");
    }

}
