package com.teamresourceful.resourcefulbees.common.lib.constants;

import com.teamresourceful.resourcefulbees.common.lib.annotations.Translate;
import net.minecraft.util.text.TranslationTextComponent;

public class TranslationConstants {

    /**
     * NOTE: TranslationTextComponents should be USED ONLY for static contexts they should be never used if .append is happening to them.
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
        public static final TranslationTextComponent ANY = new TranslationTextComponent("gui.resourcefulbees.light.any");

        @Translate("Day")
        public static final TranslationTextComponent DAY = new TranslationTextComponent("gui.resourcefulbees.light.day");

        @Translate("Night")
        public static final TranslationTextComponent NIGHT = new TranslationTextComponent("gui.resourcefulbees.light.night");
    }

    public static class Booleans {

        @Translate("Yes")
        public static final TranslationTextComponent YES = new TranslationTextComponent("gui.resourcefulbees.yes");

        @Translate("No")
        public static final TranslationTextComponent NO = new TranslationTextComponent("gui.resourcefulbees.no");

    }

    public static class Sizes {

        @Translate("Tiny")
        public static final TranslationTextComponent TINY = new TranslationTextComponent("gui.resourcefulbees.size.tiny");

        @Translate("Small")
        public static final TranslationTextComponent SMALL = new TranslationTextComponent("gui.resourcefulbees.size.small");

        @Translate("Regular")
        public static final TranslationTextComponent REGULAR = new TranslationTextComponent("gui.resourcefulbees.size.regular");

        @Translate("Large")
        public static final TranslationTextComponent LARGE = new TranslationTextComponent("gui.resourcefulbees.size.large");

        @Translate("Giant")
        public static final TranslationTextComponent GIANT = new TranslationTextComponent("gui.resourcefulbees.size.giant");
    }

    public static class Jei {

        @Translate("Bee Breeding")
        public static final String BREEDING = "gui.resourcefulbees.jei.category.breeding";

        @Translate("Centrifuge")
        public static final String CENTRIFUGE = "gui.resourcefulbees.jei.category.centrifuge";

        @Translate("Bee Flowers")
        public static final String FLOWERS = "gui.resourcefulbees.jei.category.bee_pollination_flowers";

        @Translate("Beehive Outputs")
        public static final String HIVE = "gui.resourcefulbees.jei.category.hive";

        @Translate("Mutations")
        public static final String MUTATIONS = "gui.resourcefulbees.jei.category.mutations";

        @Translate("Solidification Chamber")
        public static final String SOLIDIFICATION = "gui.resourcefulbees.jei.category.solidification";

        @Translate("Chance that this will succeed in creating a new bee.")
        public static final TranslationTextComponent BREED_CHANCE_INFO = new TranslationTextComponent("gui.resourcefulbees.jei.category.breed_chance.info");

        @Translate("[Press Shift to show NBT]")
        public static final TranslationTextComponent NBT = new TranslationTextComponent("gui.resourcefulbees.jei.tooltip.show_nbt");

        @Translate("Bee block mutation requires there to be a valid hive for the bee to go into & the bee to have nectar.")
        public static final TranslationTextComponent MUTATION_INFO = new TranslationTextComponent("gui.resourcefulbees.jei.category.mutation.info");

        @Translate("Chance that this will succeed in mutating.")
        public static final TranslationTextComponent MUTATION_CHANCE_INFO = new TranslationTextComponent("gui.resourcefulbees.jei.category.mutation_chance.info");

        @Translate("Weight: %d")
        public static final String CENTRIFUGE_WEIGHT = "gui.resourcefulbees.jei.category.centrifuge.weight";

        @Translate("Weight: SLOT EMPTY")
        public static final TranslationTextComponent CENTRIFUGE_WEIGHT_EMPTY = new TranslationTextComponent("gui.resourcefulbees.jei.category.centrifuge.weight.empty");

        @Translate("Pool Chance: %d")
        public static final String CENTRIFUGE_CHANCE = "gui.resourcefulbees.jei.category.centrifuge.chance";

        @Translate("Pool Chance: SLOT EMPTY")
        public static final TranslationTextComponent CENTRIFUGE_CHANCE_EMPTY = new TranslationTextComponent("gui.resourcefulbees.jei.category.centrifuge.chance.empty");

        @Translate("Click or Right-Click for more info!")
        public static final TranslationTextComponent CLICK_INFO = new TranslationTextComponent("tooltip.resourcefulbees.jei.click_bee_info");
    }

    public static class Top {

        @Translate("Smoke Time:")
        public static final TranslationTextComponent SMOKE_TIME = new TranslationTextComponent("gui.resourcefulbees.top.smoke_time");

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

        @Translate("Bee import was unsuccessful!")
        public static final TranslationTextComponent IMPORT_FAILED = new TranslationTextComponent("gui.resourcefulbees.apiary.import.false");

        @Translate("Bee import was successful!")
        public static final TranslationTextComponent IMPORT_SUCCESS = new TranslationTextComponent("gui.resourcefulbees.apiary.import.true");

        @Translate("Bee export was unsuccessful!")
        public static final TranslationTextComponent EXPORT_FAILED = new TranslationTextComponent("gui.resourcefulbees.apiary.export.false");

        @Translate("Bee export was successful!")
        public static final TranslationTextComponent EXPORT_SUCCESS = new TranslationTextComponent("gui.resourcefulbees.apiary.export.true");

        @Translate("Apiary validation successful!")
        public static final TranslationTextComponent VALIDATED_SUCCESS = new TranslationTextComponent("gui.resourcefulbees.apiary.validated.true");

        @Translate("Apiary validation unsuccessful!")
        public static final TranslationTextComponent VALIDATED_FAILED = new TranslationTextComponent("gui.resourcefulbees.apiary.validated.false");

        @Translate("Multiblock is a 7x6x7 hollow box.")
        public static final TranslationTextComponent STRUCTURE_SIZE = new TranslationTextComponent("block.resourcefulbees.apiary.tooltip.structure_size");

        @Translate("Requires Apiary Storage to validate.")
        public static final TranslationTextComponent REQUISITES = new TranslationTextComponent("block.resourcefulbees.apiary.tooltip.requisites");

        @Translate("Retains contents when broken.")
        public static final TranslationTextComponent DROPS = new TranslationTextComponent("block.resourcefulbees.apiary.tooltip.drops");

        @Translate("Multiblock can be built using any block from the valid_apiary tag.")
        public static final TranslationTextComponent TAGS = new TranslationTextComponent("block.resourcefulbees.apiary.tooltip.tags");

        @Translate("Offset determines the location of the Apiary Block based on its facing direction.")
        public static final TranslationTextComponent OFFSET = new TranslationTextComponent("block.resourcefulbees.apiary.tooltip.offset");

        @Translate("Bees are locked on import.")
        public static final TranslationTextComponent LOCK = new TranslationTextComponent("block.resourcefulbees.apiary.tooltip.lock");

        @Translate("Bees must be locked and selected to export.")
        public static final TranslationTextComponent LOCK2 = new TranslationTextComponent("block.resourcefulbees.apiary.tooltip.lock_2");

        @Translate("Honeycomb")
        public static final TranslationTextComponent HONEYCOMB = new TranslationTextComponent("item.resourcefulbees.tooltip.honeycomb");

        @Translate("Honeycomb Block")
        public static final TranslationTextComponent HONEYCOMB_BLOCK = new TranslationTextComponent("item.resourcefulbees.tooltip.honeycomb_block");

        @Translate("Output Quantity: %s")
        public static final String OUTPUT_QUANTITY = "block.resourcefulbees.apiary.tooltip.output_quantity";

        @Translate("Output Type: %s")
        public static final String OUTPUT_TYPE = "block.resourcefulbees.apiary.tooltip.output_type";

        @Translate("Bee Import/Export")
        public static final TranslationTextComponent MAIN_SCREEN = new TranslationTextComponent("gui.resourcefulbees.apiary.button.main_screen");

        @Translate("Apiary Storage")
        public static final TranslationTextComponent STORAGE_SCREEN = new TranslationTextComponent("gui.resourcefulbees.apiary.button.storage_screen");

        @Translate("Validate")
        public static final TranslationTextComponent VALIDATE_BUTTON = new TranslationTextComponent("gui.resourcefulbees.apiary.button.validate");

        @Translate("Build")
        public static final TranslationTextComponent BUILD_BUTTON = new TranslationTextComponent("gui.resourcefulbees.apiary.button.build");

        @Translate("Creative Mode Only!")
        public static final TranslationTextComponent CREATIVE_BUILD_BUTTON = new TranslationTextComponent("gui.resourcefulbees.apiary.button.build.creative");

        @Translate("Disable Preview")
        public static final TranslationTextComponent PREVIEW_DISABLED = new TranslationTextComponent("gui.resourcefulbees.apiary.button.preview.disable");

        @Translate("Enable Preview")
        public static final TranslationTextComponent PREVIEW_ENABLED = new TranslationTextComponent("gui.resourcefulbees.apiary.button.preview.enable");

        @Translate("Import")
        public static final TranslationTextComponent IMPORT = new TranslationTextComponent("gui.resourcefulbees.apiary.button.import");

        @Translate("Export")
        public static final TranslationTextComponent EXPORT = new TranslationTextComponent("gui.resourcefulbees.apiary.button.export");

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
        public static final TranslationTextComponent UNIQUE = new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.unique_bees");

        @Translate("Bees:")
        public static final TranslationTextComponent BEES = new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.bees");

        @Translate("Honeycombs:")
        public static final TranslationTextComponent HONEYCOMBS = new TranslationTextComponent("block.resourcefulbees.beehive.tooltip.honeycombs");
    }

    public static class Guis {

        @Translate("Ender Beecon")
        public static final TranslationTextComponent BEECON = new TranslationTextComponent("gui.resourcefulbees.ender_beecon");

        @Translate("Solidification Chamber")
        public static final TranslationTextComponent SOLIDIFICATION_CHAMBER = new TranslationTextComponent("gui.resourcefulbees.solidfication_chamber");

        @Translate("Honey Generator")
        public static final TranslationTextComponent GENERATOR = new TranslationTextComponent("gui.resourcefulbees.honey_generator");

        @Translate("Honey Pot")
        public static final TranslationTextComponent POT = new TranslationTextComponent("gui.resourcefulbees.honey_pot");

        @Translate("Apiary")
        public static final TranslationTextComponent APIARY = new TranslationTextComponent("gui.resourcefulbees.apiary");

        @Translate("Apiary Breeder")
        public static final TranslationTextComponent APIARY_BREEDER = new TranslationTextComponent("gui.resourcefulbees.apiary_breeder");

        @Translate("Apiary Storage")
        public static final TranslationTextComponent APIARY_STORAGE = new TranslationTextComponent("gui.resourcefulbees.apiary_storage");

        @Translate("Empty")
        public static final TranslationTextComponent NO_FLUID = new TranslationTextComponent("gui.resourcefulbees.fluids.empty");

        public static class EnderBeecon {

            @Translate("Bee Effects")
            public static final TranslationTextComponent PRIMARY_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.primary");

            @Translate("Drain: ")
            public static final TranslationTextComponent DRAIN_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.drain");

            @Translate("Range: ")
            public static final TranslationTextComponent RANGE_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.range");

            @Translate("Is Active: ")
            public static final TranslationTextComponent ACTIVE_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.is_active");

            @Translate("Fluid: ")
            public static final TranslationTextComponent FLUID_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.fluid");

            @Translate("Amount: ")
            public static final TranslationTextComponent FLUID_AMOUNT_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.fluid_amount");

            @Translate("Empty")
            public static final TranslationTextComponent NO_FLUID_LABEL = new TranslationTextComponent("block.resourcefulbees.ender_beecon.no_fluid");
        }
    }

    public static class Items {

        @Translate("Creative Beepedia")
        public static final TranslationTextComponent CREATIVE_BEEPEDIA = new TranslationTextComponent("item.resourcefulbees.creative_beepedia");

        @Translate("Use on a bee to see it's information")
        public static final TranslationTextComponent INFO_BEEPEDIA = new TranslationTextComponent("item.resourcefulbees.information.beepedia");

        @Translate("Hold SHIFT for more info")
        public static final TranslationTextComponent MORE_INFO = new TranslationTextComponent("item.resourcefulbees.shift_info");

        @Translate("Hold CTRL for Multiblock Info")
        public static final TranslationTextComponent MULTIBLOCK_INFO = new TranslationTextComponent("item.resourcefulbees.ctrl_info");

        @Translate("Contains Lost bees, use while sneaking to release them. (Consumed on use)")
        public static final TranslationTextComponent TEMP_INFO = new TranslationTextComponent("item.resourcefulbees.information.bee_box.temp_info");

        @Translate("Can be used to hold up to 10 bees. Use while sneaking to release them.")
        public static final TranslationTextComponent INFO = new TranslationTextComponent("item.resourcefulbees.information.bee_box.info");

        @Translate("Bees:")
        public static final TranslationTextComponent BEES = new TranslationTextComponent("item.resourcefulbees.information.bee_box.bees");

        @Translate("Right click the hive to smoke it.")
        public static final TranslationTextComponent SMOKER_TOOLTIP = new TranslationTextComponent("item.resourcefulbees.smoker.tooltip");

        @Translate("Bees won't get angry for 30s when shearing honeycombs.")
        public static final TranslationTextComponent SMOKER_TOOLTIP1 = new TranslationTextComponent("item.resourcefulbees.smoker.tooltip.1");

        @Translate("Selected Bee: ")
        public static final TranslationTextComponent HONEY_DIPPER_TOOLTIP = new TranslationTextComponent("item.resourcefulbees.honey_dipper.tooltip");

        @Translate("Queen Bee Banner")
        public static final TranslationTextComponent QUEEN_BEE_BANNER = new TranslationTextComponent("block.resourcefulbees.queen_bee_banner");

        @Translate("Use on a previous tier nest to upgrade it")
        public static final TranslationTextComponent HIVE_UPGRADE = new TranslationTextComponent("item.resourcefulbees.hive_upgrade.tooltip.info");

        @Translate("Adds additional breeders to the Apiary Breeder")
        public static final TranslationTextComponent BREEDER_UPGRADE = new TranslationTextComponent("item.resourcefulbees.apiary_breeder_upgrade.tooltip.info");

        @Translate("Reduces max breed time by 300 ticks.")
        public static final TranslationTextComponent BREED_TIME_UPGRADE = new TranslationTextComponent("item.resourcefulbees.apiary_breed_time_upgrade.tooltip.info");

        @Translate("Fifty Shades of Bees")
        public static final TranslationTextComponent BOOK_NAME = new TranslationTextComponent("book.resourcefulbees.name");

        @Translate("Consumes honey, grants bees safety.")
        public static final TranslationTextComponent BEECON_TOOLTIP = new TranslationTextComponent("block.resourcefulbees.beecon.tooltip.info");

        @Translate("[Prevents bees from teleporting in effect range]")
        public static final TranslationTextComponent BEECON_TOOLTIP_1 = new TranslationTextComponent("block.resourcefulbees.beecon.tooltip.info.1");

        @Translate("Generates RF using honey bottles.")
        public static final TranslationTextComponent GEN_TOOLTIP = new TranslationTextComponent("block.resourcefulbees.generator.tooltip.info");

        @Translate("An OPTIONAL breeder that can be added as part of the Apiary Multiblock")
        public static final TranslationTextComponent BREEDER_TOOLTIP = new TranslationTextComponent("block.resourcefulbees.apiary_breeder.tooltip.info");

        @Translate("Max Breed Time: %s ticks")
        public static final String BREEDER_TOOLTIP_1 = "block.resourcefulbees.apiary_breeder.tooltip.info.1";

        @Translate("Can be upgraded with additional breeders or time modifiers.")
        public static final TranslationTextComponent BREEDER_TOOLTIP_2 = new TranslationTextComponent("block.resourcefulbees.apiary_breeder.tooltip.info.2");

        @Translate("Right-click on Tiered Beehives to collect honeycombs.")
        public static final TranslationTextComponent SCRAPER_TOOLTIP = new TranslationTextComponent("block.resourcefulbees.scraper.tooltip.info");

        @Translate("Must collect ALL honeycombs for bees to generate new ones!")
        public static final TranslationTextComponent SCRAPER_TOOLTIP_1 = new TranslationTextComponent("block.resourcefulbees.scraper.tooltip.info.2");
    }

    public static class HoneyDipper {

        @Translate("Hive position for [%s] has been set to %s")
        public static final String HIVE_SET = "items.resourcefulbees.honey_dipper.hive_set";

        @Translate("Flower position for [%s] has been set to %s")
        public static final String FLOWER_SET = "items.resourcefulbees.honey_dipper.flower_set";

        @Translate("Bee Selection Cleared!")
        public static final TranslationTextComponent SELECTION_CLEARED = new TranslationTextComponent("items.resourcefulbees.honey_dipper.cleared");

        @Translate("[%s] has been selected!")
        public static final String BEE_SET = "items.resourcefulbees.honey_dipper.bee_set";
    }

    public static class Beepedia {

        @Translate("Bees Found: §6 %s / %s")
        public static final String PROGRESS = "gui.resourcefulbees.beepedia.home.progress";

        @Translate("Beepedia v2.0")
        public static final TranslationTextComponent NAME = new TranslationTextComponent("gui.resourcefulbees.beepedia");

        @Translate("Bees")
        public static final TranslationTextComponent BEE_LIST = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.bees_list");

        @Translate("Trait Effects")
        public static final TranslationTextComponent EFFECTS_LIST = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.effects_list");

        @Translate("Search")
        public static final TranslationTextComponent SEARCH = new TranslationTextComponent("gui.resourcefulbees.beepedia.search");

        @Translate("Honey")
        public static final TranslationTextComponent HONEY = new TranslationTextComponent("gui.resourcefulbees.beepedia.honey");


        public static class Home {

            @Translate("Join us on Discord!")
            public static final TranslationTextComponent DISCORD = new TranslationTextComponent("gui.resourcefulbees.beepedia.home.discord");

            @Translate("Support us on Patreon!")
            public static final TranslationTextComponent PATREON = new TranslationTextComponent("gui.resourcefulbees.beepedia.home.patreon");

            @Translate("Found a bug/issue? Submit it here.")
            public static final TranslationTextComponent ISSUES = new TranslationTextComponent("gui.resourcefulbees.beepedia.home.issues");

            @Translate("Browse the wiki.")
            public static final TranslationTextComponent WIKI = new TranslationTextComponent("gui.resourcefulbees.beepedia.home.wiki");
        }

        public static class Breeding {

            @Translate("Breeding")
            public static final TranslationTextComponent TITLE = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding");

            @Translate("Parents")
            public static final TranslationTextComponent PARENTS = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.parents_title");

            @Translate("Children")
            public static final TranslationTextComponent CHILDREN = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.children_title");

            @Translate("Entity Mutations")
            public static final TranslationTextComponent ENTITY_MUTATIONS = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.entity_mutations_title");

            @Translate("Item Mutations")
            public static final TranslationTextComponent ITEM_MUTATIONS = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.item_mutations_title");

            @Translate("Error")
            public static final TranslationTextComponent ERROR = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.error_title");
        }

        public static class Spawning {

            @Translate("Spawning")
            public static final TranslationTextComponent TITLE = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning");

            @Translate("Biomes")
            public static final TranslationTextComponent BIOMES = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning.biomes");

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
            public static final TranslationTextComponent TITLE = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.honeycomb");
        }

        public static class Centrifuge {

            @Translate("Centrifuge")
            public static final TranslationTextComponent TITLE = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.centrifuge");

            @Translate("Multiblock centrifuge required for this recipe.")
            public static final TranslationTextComponent MULTIBLOCK = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.centrifuge.requires_multiblock");
        }

        public static class Mutations {

            @Translate("Mutations")
            public static final TranslationTextComponent TITLE = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations");

            @Translate("Amount of mutations able to be performed per pollination.")
            public static final TranslationTextComponent MUTATION_COUNT = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations.mutation_count.tooltip");

            @Translate("Block Mutations")
            public static final TranslationTextComponent BLOCK = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations.block");

            @Translate("Entity Mutations")
            public static final TranslationTextComponent ENTITY = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations.entity");

            @Translate("Item Mutations")
            public static final TranslationTextComponent ITEM = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations.item");

        }

        public static class Traits {

            @Translate("Traits")
            public static final TranslationTextComponent TITLE = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits");

            @Translate("Particles")
            public static final TranslationTextComponent PARTICLES = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.particle");

            @Translate("Damage Types")
            public static final TranslationTextComponent DAMAGE_TYPES = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.damageTypes");

            @Translate("Amplifier: ")
            public static final TranslationTextComponent AMPLIFIER = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.amplifier");

            @Translate("Potion Damage Effects")
            public static final TranslationTextComponent POTION_DAMAGE_EFFECTS = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.potion_damage_effects");

            @Translate("Potion Immunities")
            public static final TranslationTextComponent POTION_IMMUNITIES = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.potion_immunities");

            @Translate("Damage Immunities")
            public static final TranslationTextComponent DAMAGE_IMMUNITIES = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.damage_immunities");
        }

        public static class Info {

            @Translate("Info")
            public static final TranslationTextComponent INFO = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info");

            @Translate("Flower: ")
            public static final TranslationTextComponent FLOWER = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.flower");

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
            public static final TranslationTextComponent TITLE = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.combat");
        }

    }

    public static class Centrifuge {

        @Translate("Close")
        public static final TranslationTextComponent CLOSE = new TranslationTextComponent("gui.resourcefulbees.centrifuge.close");
    }

}
