package com.teamresourceful.resourcefulbees.common.lib.constants.translations;

import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class ItemTranslations {

    private ItemTranslations() {
        throw new UtilityClassError();
    }

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

    @Translate("Generates RF using honey")
    public static final MutableComponent GEN_TOOLTIP = Component.translatable("block.resourcefulbees.generator.tooltip.info");

    @Translate("Max Breed Time: %s ticks")
    public static final String BREEDER_TOOLTIP_1 = "block.resourcefulbees.apiary_breeder.tooltip.info.1";

    @Translate("Can be upgraded with breed time upgrades.")
    public static final MutableComponent BREEDER_TOOLTIP_2 = Component.translatable("block.resourcefulbees.apiary_breeder.tooltip.info.2");

    @Translate("Use on Tiered Beehives to harvest combs one at a time")
    public static final MutableComponent SCRAPER_TOOLTIP = Component.translatable("block.resourcefulbees.scraper.tooltip.info");

    @Translate("Must harvest ALL honeycombs for bees to generate new ones!")
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

    @Translate("§8contents.")
    public static final MutableComponent TOOLTIP_CONTENTS = Component.translatable("item.resourcefulbees.tooltip_contents");

    @Translate(" - %s")
    public static final String BEE_BOX_ENTITY_NAME = "item.resourcefulbees.bee_box.entity_name";

    @Translate("Holds up to %s bees.")
    public static final String BEE_BOX_TOOLTIP = "item.resourcefulbees.information.bee_box.info";

    @Translate("Contains lost bees. Consumed on use.")
    public static final MutableComponent BEE_BOX_TOOLTIP_TEMP = Component.translatable("item.resourcefulbees.information.bee_box.temp_info");

    @Translate("Bees:")
    public static final MutableComponent BEES = Component.translatable("item.resourcefulbees.information.bee_box.bees");

    @Translate("No Bees.")
    public static final MutableComponent NO_BEES = Component.translatable("item.resourcefulbees.information.bee_box.no_bees");
}
