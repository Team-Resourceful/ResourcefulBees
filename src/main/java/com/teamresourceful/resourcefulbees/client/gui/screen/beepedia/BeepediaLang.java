package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class BeepediaLang {

    private BeepediaLang() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static final Component INTERFACE_NAME = Component.translatable("gui.resourcefulbees.beepedia");
    public static final MutableComponent TAB_BEES = Component.translatable("gui.resourcefulbees.beepedia.tab.bees");
    public static final MutableComponent TAB_TRAITS = Component.translatable("gui.resourcefulbees.beepedia.tab.traits");
    public static final MutableComponent TAB_HONEY = Component.translatable("gui.resourcefulbees.beepedia.tab.honey");
    public static final MutableComponent TAB_COMBS = Component.translatable("gui.resourcefulbees.beepedia.tab.combs");
    public static final MutableComponent TOOLTIP_DISCORD = Component.translatable("gui.resourcefulbees.beepedia.home.discord");
    public static final MutableComponent TOOLTIP_PATREON = Component.translatable("gui.resourcefulbees.beepedia.home.patreon");
    public static final MutableComponent TOOLTIP_ISSUES = Component.translatable("gui.resourcefulbees.beepedia.home.issues");
    public static final MutableComponent TOOLTIP_WIKI = Component.translatable("gui.resourcefulbees.beepedia.home.wiki");
    public static final MutableComponent COLLECTION_PROGRESS = Component.translatable("gui.resourcefulbees.beepedia.home.progress");
    public static final MutableComponent ITEM_GROUP = Component.translatable("itemGroup.resourcefulbees");
    public static final MutableComponent PARENTS_TITLE = Component.translatable("gui.resourcefulbees.beepedia.bee_subtab.breeding.parents_title");
    public static final MutableComponent CHILDREN_TITLE = Component.translatable("gui.resourcefulbees.beepedia.bee_subtab.breeding.children_title");
    public static final MutableComponent ENTITY_MUTATIONS_TITLE = Component.translatable("gui.resourcefulbees.beepedia.bee_subtab.breeding.entity_mutations_title");
    public static final MutableComponent ITEM_MUTATIONS_TITLE = Component.translatable("gui.resourcefulbees.beepedia.bee_subtab.breeding.item_mutations_title");
    public static final MutableComponent ERROR_TITLE = Component.translatable("gui.resourcefulbees.beepedia.bee_subtab.breeding.error_title");
    public static final MutableComponent FIFTY_SHADES_BUTTON = Component.translatable("book.resourcefulbees.name");
    public static final MutableComponent BEE_SEARCH_STARRED = Component.translatable("gui.resourcefulbees.beepedia.bee_search.starred");
    public static final MutableComponent BEE_SEARCH_NOT_STARRED = Component.translatable("gui.resourcefulbees.beepedia.bee_search.not_starred");
    public static final MutableComponent BEE_SEARCH_WORLD = Component.translatable("gui.resourcefulbees.beepedia.bee_search.world");
    public static final MutableComponent BEE_SEARCH_NOT_WORLD = Component.translatable("gui.resourcefulbees.beepedia.bee_search.not_world");
    public static final MutableComponent BEE_SEARCH_BREEDABLE = Component.translatable("gui.resourcefulbees.beepedia.bee_search.breedable");
    public static final MutableComponent BEE_SEARCH_NOT_BREEDABLE = Component.translatable("gui.resourcefulbees.beepedia.bee_search.not_breedable");
    public static final MutableComponent BEE_SEARCH_MUTATES = Component.translatable("gui.resourcefulbees.beepedia.bee_search.mutates");
    public static final MutableComponent BEE_SEARCH_HELP = Component.translatable("gui.resourcefulbees.beepedia.bee_search.help");
    public static final MutableComponent VERSION_NUMBER = Component.translatable("gui.resourcefulbees.beepedia");
}
