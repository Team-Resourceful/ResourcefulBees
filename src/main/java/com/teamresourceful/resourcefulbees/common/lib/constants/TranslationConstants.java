package com.teamresourceful.resourcefulbees.common.lib.constants;

import com.teamresourceful.resourcefulbees.common.lib.annotations.Translate;
import net.minecraft.network.chat.TranslatableComponent;

public class TranslationConstants {

    /**
     * NOTE: TranslatableComponents should be USED ONLY for static contexts they should be never used if .append is happening to them.
     * Use a string of the translation key if appending is needed and use string formats where possible instead of .append
     *
     * This class is used to store translation components and keys and every key and component should be annotated with @Translate
     * with their respective english translation this annotation is used in the data generator to generate the english language json.
     */

    private TranslationConstants() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static class LightLevel {

        @Translate("Any")
        public static final TranslatableComponent ANY = new TranslatableComponent("gui.resourcefulbees.light.any");

        @Translate("Day")
        public static final TranslatableComponent DAY = new TranslatableComponent("gui.resourcefulbees.light.day");

        @Translate("Night")
        public static final TranslatableComponent NIGHT = new TranslatableComponent("gui.resourcefulbees.light.night");
    }

    public static class Booleans {

        @Translate("Yes")
        public static final TranslatableComponent YES = new TranslatableComponent("gui.resourcefulbees.yes");

        @Translate("No")
        public static final TranslatableComponent NO = new TranslatableComponent("gui.resourcefulbees.no");

    }

    public static class Sizes {

        @Translate("Tiny")
        public static final TranslatableComponent TINY = new TranslatableComponent("gui.resourcefulbees.size.tiny");

        @Translate("Small")
        public static final TranslatableComponent SMALL = new TranslatableComponent("gui.resourcefulbees.size.small");

        @Translate("Regular")
        public static final TranslatableComponent REGULAR = new TranslatableComponent("gui.resourcefulbees.size.regular");

        @Translate("Large")
        public static final TranslatableComponent LARGE = new TranslatableComponent("gui.resourcefulbees.size.large");

        @Translate("Giant")
        public static final TranslatableComponent GIANT = new TranslatableComponent("gui.resourcefulbees.size.giant");
    }

    public static class Jei {

        @Translate("Bee Breeding")
        public static final TranslatableComponent BREEDING = new TranslatableComponent("gui.resourcefulbees.jei.category.breeding");

        @Translate("Centrifuge")
        public static final TranslatableComponent CENTRIFUGE = new TranslatableComponent("gui.resourcefulbees.jei.category.centrifuge");

        @Translate("Bee Flowers")
        public static final TranslatableComponent FLOWERS = new TranslatableComponent("gui.resourcefulbees.jei.category.bee_pollination_flowers");

        @Translate("Beehive Outputs")
        public static final TranslatableComponent HIVE = new TranslatableComponent("gui.resourcefulbees.jei.category.hive");

        @Translate("Mutations")
        public static final TranslatableComponent MUTATIONS = new TranslatableComponent("gui.resourcefulbees.jei.category.mutations");

        @Translate("Solidification Chamber")
        public static final TranslatableComponent SOLIDIFICATION = new TranslatableComponent("gui.resourcefulbees.jei.category.solidification");

        @Translate("Chance that this will succeed in creating a new bee.")
        public static final TranslatableComponent BREED_CHANCE_INFO = new TranslatableComponent("gui.resourcefulbees.jei.category.breed_chance.info");

        @Translate("[Press Shift to show NBT]")
        public static final TranslatableComponent NBT = new TranslatableComponent("gui.resourcefulbees.jei.tooltip.show_nbt");

        @Translate("Bee block mutation requires there to be a valid hive for the bee to go into & the bee to have nectar.")
        public static final TranslatableComponent MUTATION_INFO = new TranslatableComponent("gui.resourcefulbees.jei.category.mutation.info");

        @Translate("Chance that this will succeed in mutating.")
        public static final TranslatableComponent MUTATION_CHANCE_INFO = new TranslatableComponent("gui.resourcefulbees.jei.category.mutation_chance.info");

        @Translate("Weight: %d")
        public static final String CENTRIFUGE_WEIGHT = "gui.resourcefulbees.jei.category.centrifuge.weight";

        @Translate("Weight: SLOT EMPTY")
        public static final TranslatableComponent CENTRIFUGE_WEIGHT_EMPTY = new TranslatableComponent("gui.resourcefulbees.jei.category.centrifuge.weight.empty");

        @Translate("Pool Chance: %d")
        public static final String CENTRIFUGE_CHANCE = "gui.resourcefulbees.jei.category.centrifuge.chance";

        @Translate("Pool Chance: SLOT EMPTY")
        public static final TranslatableComponent CENTRIFUGE_CHANCE_EMPTY = new TranslatableComponent("gui.resourcefulbees.jei.category.centrifuge.chance.empty");

        @Translate("Click or Right-Click for more info!")
        public static final TranslatableComponent CLICK_INFO = new TranslatableComponent("tooltip.resourcefulbees.jei.click_bee_info");
    }

    public static class Top {

        @Translate("Smoke Time:")
        public static final TranslatableComponent SMOKE_TIME = new TranslatableComponent("gui.resourcefulbees.top.smoke_time");

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
        public static final TranslatableComponent HONEYCOMB = new TranslatableComponent("item.resourcefulbees.tooltip.honeycomb");

        @Translate("Honeycomb Block")
        public static final TranslatableComponent HONEYCOMB_BLOCK = new TranslatableComponent("item.resourcefulbees.tooltip.honeycomb_block");

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

        @Translate(" UNIQUE §6Bees")
        public static final TranslatableComponent UNIQUE = new TranslatableComponent("block.resourcefulbees.beehive.tooltip.unique_bees");

        @Translate("Bees:")
        public static final TranslatableComponent BEES = new TranslatableComponent("block.resourcefulbees.beehive.tooltip.bees");

        @Translate("Honeycombs:")
        public static final TranslatableComponent HONEYCOMBS = new TranslatableComponent("block.resourcefulbees.beehive.tooltip.honeycombs");
    }

    public static class Guis {

        public static final TranslatableComponent INVENTORY = new TranslatableComponent("container.inventory");

        @Translate("Ender Beecon")
        public static final TranslatableComponent BEECON = new TranslatableComponent("gui.resourcefulbees.ender_beecon");

        @Translate("Solidification Chamber")
        public static final TranslatableComponent SOLIDIFICATION_CHAMBER = new TranslatableComponent("gui.resourcefulbees.solidfication_chamber");

        @Translate("Honey Generator")
        public static final TranslatableComponent GENERATOR = new TranslatableComponent("gui.resourcefulbees.honey_generator");

        @Translate("Honey Pot")
        public static final TranslatableComponent POT = new TranslatableComponent("gui.resourcefulbees.honey_pot");

        @Translate("Apiary")
        public static final TranslatableComponent APIARY = new TranslatableComponent("gui.resourcefulbees.apiary");

        @Translate("Apiary Breeder")
        public static final TranslatableComponent APIARY_BREEDER = new TranslatableComponent("gui.resourcefulbees.apiary_breeder");

        @Translate("Empty")
        public static final TranslatableComponent NO_FLUID = new TranslatableComponent("gui.resourcefulbees.fluids.empty");

        public static class EnderBeecon {

            @Translate("Bee Effects")
            public static final TranslatableComponent PRIMARY_LABEL = new TranslatableComponent("block.resourcefulbees.ender_beecon.primary");

            @Translate("Drain: ")
            public static final TranslatableComponent DRAIN_LABEL = new TranslatableComponent("block.resourcefulbees.ender_beecon.drain");

            @Translate("Range: ")
            public static final TranslatableComponent RANGE_LABEL = new TranslatableComponent("block.resourcefulbees.ender_beecon.range");

            @Translate("Is Active: ")
            public static final TranslatableComponent ACTIVE_LABEL = new TranslatableComponent("block.resourcefulbees.ender_beecon.is_active");

            @Translate("Fluid: ")
            public static final TranslatableComponent FLUID_LABEL = new TranslatableComponent("block.resourcefulbees.ender_beecon.fluid");

            @Translate("Amount: ")
            public static final TranslatableComponent FLUID_AMOUNT_LABEL = new TranslatableComponent("block.resourcefulbees.ender_beecon.fluid_amount");

            @Translate("Empty")
            public static final TranslatableComponent NO_FLUID_LABEL = new TranslatableComponent("block.resourcefulbees.ender_beecon.no_fluid");
        }
    }

    public static class Items {

        @Translate("Creative Beepedia")
        public static final TranslatableComponent CREATIVE_BEEPEDIA = new TranslatableComponent("item.resourcefulbees.creative_beepedia");

        @Translate("Use on a bee to see it's information")
        public static final TranslatableComponent INFO_BEEPEDIA = new TranslatableComponent("item.resourcefulbees.information.beepedia");

        @Translate("Hold SHIFT for more info")
        public static final TranslatableComponent MORE_INFO = new TranslatableComponent("item.resourcefulbees.shift_info");

        @Translate("Hold CTRL for Multiblock Info")
        public static final TranslatableComponent MULTIBLOCK_INFO = new TranslatableComponent("item.resourcefulbees.ctrl_info");

        @Translate("Contains Lost bees, use while sneaking to release them. (Consumed on use)")
        public static final TranslatableComponent TEMP_INFO = new TranslatableComponent("item.resourcefulbees.information.bee_box.temp_info");

        @Translate("Can be used to hold up to 10 bees. Use while sneaking to release them.")
        public static final TranslatableComponent INFO = new TranslatableComponent("item.resourcefulbees.information.bee_box.info");

        @Translate("Bees:")
        public static final TranslatableComponent BEES = new TranslatableComponent("item.resourcefulbees.information.bee_box.bees");

        @Translate("Right click the hive to smoke it.")
        public static final TranslatableComponent SMOKER_TOOLTIP = new TranslatableComponent("item.resourcefulbees.smoker.tooltip");

        @Translate("Bees won't get angry for 30s when shearing honeycombs.")
        public static final TranslatableComponent SMOKER_TOOLTIP1 = new TranslatableComponent("item.resourcefulbees.smoker.tooltip.1");

        @Translate("Selected Bee: ")
        public static final TranslatableComponent HONEY_DIPPER_TOOLTIP = new TranslatableComponent("item.resourcefulbees.honey_dipper.tooltip");

        @Translate("Queen Bee Banner")
        public static final TranslatableComponent QUEEN_BEE_BANNER = new TranslatableComponent("block.resourcefulbees.queen_bee_banner");

        @Translate("Use on a previous tier nest to upgrade it")
        public static final TranslatableComponent HIVE_UPGRADE = new TranslatableComponent("item.resourcefulbees.hive_upgrade.tooltip.info");

        @Translate("Adds additional breeders to the Apiary Breeder")
        public static final TranslatableComponent BREEDER_UPGRADE = new TranslatableComponent("item.resourcefulbees.apiary_breeder_upgrade.tooltip.info");

        @Translate("Reduces max breed time by 300 ticks.")
        public static final TranslatableComponent BREED_TIME_UPGRADE = new TranslatableComponent("item.resourcefulbees.apiary_breed_time_upgrade.tooltip.info");

        @Translate("Fifty Shades of Bees")
        public static final TranslatableComponent BOOK_NAME = new TranslatableComponent("book.resourcefulbees.name");

        @Translate("Consumes honey, grants bees safety.")
        public static final TranslatableComponent BEECON_TOOLTIP = new TranslatableComponent("block.resourcefulbees.beecon.tooltip.info");

        @Translate("[Prevents bees from teleporting in effect range]")
        public static final TranslatableComponent BEECON_TOOLTIP_1 = new TranslatableComponent("block.resourcefulbees.beecon.tooltip.info.1");

        @Translate("Generates RF using honey bottles.")
        public static final TranslatableComponent GEN_TOOLTIP = new TranslatableComponent("block.resourcefulbees.generator.tooltip.info");

        @Translate("An OPTIONAL breeder that can be added as part of the Apiary Multiblock")
        public static final TranslatableComponent BREEDER_TOOLTIP = new TranslatableComponent("block.resourcefulbees.apiary_breeder.tooltip.info");

        @Translate("Max Breed Time: %s ticks")
        public static final String BREEDER_TOOLTIP_1 = "block.resourcefulbees.apiary_breeder.tooltip.info.1";

        @Translate("Can be upgraded with additional breeders or time modifiers.")
        public static final TranslatableComponent BREEDER_TOOLTIP_2 = new TranslatableComponent("block.resourcefulbees.apiary_breeder.tooltip.info.2");

        @Translate("Right-click on Tiered Beehives to collect honeycombs.")
        public static final TranslatableComponent SCRAPER_TOOLTIP = new TranslatableComponent("block.resourcefulbees.scraper.tooltip.info");

        @Translate("Must collect ALL honeycombs for bees to generate new ones!")
        public static final TranslatableComponent SCRAPER_TOOLTIP_1 = new TranslatableComponent("block.resourcefulbees.scraper.tooltip.info.2");
    }

    public static class HoneyDipper {

        @Translate("Hive position for [%s] has been set to %s")
        public static final String HIVE_SET = "items.resourcefulbees.honey_dipper.hive_set";

        @Translate("Flower position for [%s] has been set to %s")
        public static final String FLOWER_SET = "items.resourcefulbees.honey_dipper.flower_set";

        @Translate("Bee Selection Cleared!")
        public static final TranslatableComponent SELECTION_CLEARED = new TranslatableComponent("items.resourcefulbees.honey_dipper.cleared");

        @Translate("[%s] has been selected!")
        public static final String BEE_SET = "items.resourcefulbees.honey_dipper.bee_set";
    }

    public static class Beepedia {

        @Translate("Bees Found: §6 %s / %s")
        public static final String PROGRESS = "gui.resourcefulbees.beepedia.home.progress";

        @Translate("Beepedia v2.0")
        public static final TranslatableComponent NAME = new TranslatableComponent("gui.resourcefulbees.beepedia");

        @Translate("Bees")
        public static final TranslatableComponent BEE_LIST = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits.bees_list");

        @Translate("Trait Effects")
        public static final TranslatableComponent EFFECTS_LIST = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits.effects_list");

        @Translate("Search")
        public static final TranslatableComponent SEARCH = new TranslatableComponent("gui.resourcefulbees.beepedia.search");

        @Translate("Honey")
        public static final TranslatableComponent HONEY = new TranslatableComponent("gui.resourcefulbees.beepedia.honey");


        public static class Home {

            @Translate("Join us on Discord!")
            public static final TranslatableComponent DISCORD = new TranslatableComponent("gui.resourcefulbees.beepedia.home.discord");

            @Translate("Support us on Patreon!")
            public static final TranslatableComponent PATREON = new TranslatableComponent("gui.resourcefulbees.beepedia.home.patreon");

            @Translate("Found a bug/issue? Submit it here.")
            public static final TranslatableComponent ISSUES = new TranslatableComponent("gui.resourcefulbees.beepedia.home.issues");

            @Translate("Browse the wiki.")
            public static final TranslatableComponent WIKI = new TranslatableComponent("gui.resourcefulbees.beepedia.home.wiki");
        }

        public static class Breeding {

            @Translate("Breeding")
            public static final TranslatableComponent TITLE = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding");

            @Translate("Parents")
            public static final TranslatableComponent PARENTS = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.parents_title");

            @Translate("Children")
            public static final TranslatableComponent CHILDREN = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.children_title");

            @Translate("Entity Mutations")
            public static final TranslatableComponent ENTITY_MUTATIONS = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.entity_mutations_title");

            @Translate("Item Mutations")
            public static final TranslatableComponent ITEM_MUTATIONS = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.item_mutations_title");

            @Translate("Error")
            public static final TranslatableComponent ERROR = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.error_title");
        }

        public static class Spawning {

            @Translate("Spawning")
            public static final TranslatableComponent TITLE = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning");

            @Translate("Biomes")
            public static final TranslatableComponent BIOMES = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning.biomes");

            @Translate("Group Size: %s - %s")
            public static final String GROUP = "gui.resourcefulbees.beepedia.bee_subtab.spawning.group";

            @Translate("Spawn Height: %s - %s")
            public static final String HEIGHT = "gui.resourcefulbees.beepedia.bee_subtab.spawning.height";

            @Translate("Spawn Weight: %s")
            public static final String WEIGHT = "gui.resourcefulbees.beepedia.bee_subtab.spawning.weight";

            @Translate("Light Level: %s")
            public static final String LIGHT = "gui.resourcefulbees.beepedia.bee_subtab.spawning.light";
        }

        public static class Honeycombs {

            @Translate("Honeycomb")
            public static final TranslatableComponent TITLE = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.honeycomb");
        }

        public static class Centrifuge {

            @Translate("Centrifuge")
            public static final TranslatableComponent TITLE = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.centrifuge");

            @Translate("Multiblock centrifuge required for this recipe.")
            public static final TranslatableComponent MULTIBLOCK = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.centrifuge.requires_multiblock");
        }

        public static class Mutations {

            @Translate("Mutations")
            public static final TranslatableComponent TITLE = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations");

            @Translate("Amount of mutations able to be performed per pollination.")
            public static final TranslatableComponent MUTATION_COUNT = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations.mutation_count.tooltip");

            @Translate("Block Mutations")
            public static final TranslatableComponent BLOCK = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations.block");

            @Translate("Entity Mutations")
            public static final TranslatableComponent ENTITY = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations.entity");

            @Translate("Item Mutations")
            public static final TranslatableComponent ITEM = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations.item");

        }

        public static class Traits {

            @Translate("Traits")
            public static final TranslatableComponent TITLE = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits");

            @Translate("Particles")
            public static final TranslatableComponent PARTICLES = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits.particle");

            @Translate("Damage Types")
            public static final TranslatableComponent DAMAGE_TYPES = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits.damageTypes");

            @Translate("Amplifier: ")
            public static final TranslatableComponent AMPLIFIER = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits.amplifier");

            @Translate("Potion Damage Effects")
            public static final TranslatableComponent POTION_DAMAGE_EFFECTS = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits.potion_damage_effects");

            @Translate("Potion Immunities")
            public static final TranslatableComponent POTION_IMMUNITIES = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits.potion_immunities");

            @Translate("Damage Immunities")
            public static final TranslatableComponent DAMAGE_IMMUNITIES = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits.damage_immunities");
        }

        public static class Info {

            @Translate("Info")
            public static final TranslatableComponent INFO = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info");

            @Translate("Flower: ")
            public static final TranslatableComponent FLOWER = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.flower");

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

        public static class Combat {

            @Translate("Combat Data")
            public static final TranslatableComponent TITLE = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.combat");
        }

    }

    public static class Centrifuge {

        @Translate("Close")
        public static final TranslatableComponent CLOSE = new TranslatableComponent("gui.resourcefulbees.centrifuge.close");
    }

}
