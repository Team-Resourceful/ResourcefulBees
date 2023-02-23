package com.teamresourceful.resourcefulbees.common.lib.constants.translations;

import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public final class BeepediaTranslations {

    //TODO decide if the unused fields in this class will ever be used
    // and if not then remove them and reduce to this single class
    private BeepediaTranslations() {
        throw new UtilityClassError();
    }

    @Translate("Open JEI")
    public static final MutableComponent OPEN_JEI = Component.translatable("gui.resourcefulbees.beepedia.open_jei");

    @Translate("Time depends on difficulty.")
    public static final MutableComponent EFFECT_TIME = Component.translatable("gui.resourcefulbees.beepedia.effect.time");

    @Translate("Immune no matter the strength.")
    public static final MutableComponent IMMUNITY = Component.translatable("gui.resourcefulbees.beepedia.effect.immunity");

    @Translate("Hover for description")
    public static final MutableComponent HOVER_FOR_DESC = Component.translatable("gui.resourcefulbees.beepedia.hover_for_desc").setStyle(Style.EMPTY.withUnderlined(true));

    @Translate("Damage Source Immunity")
    public static final MutableComponent DAMAGE_IMMUNITY = Component.translatable("gui.resourcefulbees.beepedia.damage.immunity");

    @Translate("Potion Aura")
    public static final MutableComponent POTION_AURA = Component.translatable("gui.resourcefulbees.beepedia.aura.potion");

    @Translate("Burning Aura")
    public static final MutableComponent BURN_AURA = Component.translatable("gui.resourcefulbees.beepedia.aura.burn");

    @Translate("Healing Aura")
    public static final MutableComponent HEAL_AURA = Component.translatable("gui.resourcefulbees.beepedia.aura.heal");

    @Translate("Damage Aura")
    public static final MutableComponent DAMAGE_AURA = Component.translatable("gui.resourcefulbees.beepedia.aura.damage");

    @Translate("XP Aura")
    public static final MutableComponent XP_AURA = Component.translatable("gui.resourcefulbees.beepedia.aura.xp");

    @Translate("XP Drain Aura")
    public static final MutableComponent XP_DRAIN_AURA = Component.translatable("gui.resourcefulbees.beepedia.aura.xp_drain");

    @Translate("Burns for 10 seconds")
    public static final MutableComponent BURN_AURA_DESC = Component.translatable("gui.resourcefulbees.beepedia.aura.burn.desc");

    @Translate("Heals for %s HP")
    public static final String HEAL_AURA_DESC = "gui.resourcefulbees.beepedia.aura.heal.desc";

    @Translate("Gives %s XP")
    public static final String XP_AURA_DESC = "gui.resourcefulbees.beepedia.aura.xp.desc";

    @Translate("Drains %s XP")
    public static final String XP_DRAIN_AURA_DESC = "gui.resourcefulbees.beepedia.aura.xp_drain.desc";

    //OLD BELOW THIS LINE

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
