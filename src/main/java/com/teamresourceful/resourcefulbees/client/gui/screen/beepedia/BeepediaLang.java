package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeepediaLang {
    public static final Component INTERFACE_NAME = new TranslatableComponent("gui.resourcefulbees.beepedia");
    public static final TranslatableComponent TAB_BEES = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.bees");
    public static final TranslatableComponent TAB_TRAITS = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits");
    public static final TranslatableComponent TAB_HONEY = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.honey");
    public static final TranslatableComponent TAB_COMBS = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.combs");
    public static final TranslatableComponent TOOLTIP_DISCORD = new TranslatableComponent("gui.resourcefulbees.beepedia.home.discord");
    public static final TranslatableComponent TOOLTIP_PATREON = new TranslatableComponent("gui.resourcefulbees.beepedia.home.patreon");
    public static final TranslatableComponent TOOLTIP_ISSUES = new TranslatableComponent("gui.resourcefulbees.beepedia.home.issues");
    public static final TranslatableComponent TOOLTIP_WIKI = new TranslatableComponent("gui.resourcefulbees.beepedia.home.wiki");
    public static final TranslatableComponent COLLECTION_PROGRESS = new TranslatableComponent("gui.resourcefulbees.beepedia.home.progress");
    public static final TranslatableComponent ITEM_GROUP = new TranslatableComponent("itemGroup.resourcefulbees");
    public static final TranslatableComponent PARENTS_TITLE = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.parents_title");
    public static final TranslatableComponent CHILDREN_TITLE = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.children_title");
    public static final TranslatableComponent ENTITY_MUTATIONS_TITLE = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.entity_mutations_title");
    public static final TranslatableComponent ITEM_MUTATIONS_TITLE = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.item_mutations_title");
    public static final TranslatableComponent ERROR_TITLE = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.error_title");
    public static final TranslatableComponent FIFTY_SHADES_BUTTON = new TranslatableComponent("book.resourcefulbees.name");
    public static final TranslatableComponent BEE_SEARCH_STARRED = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_search.starred");
    public static final TranslatableComponent BEE_SEARCH_NOT_STARRED = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_search.not_starred");;
    public static final TranslatableComponent BEE_SEARCH_WORLD = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_search.world");
    public static final TranslatableComponent BEE_SEARCH_NOT_WORLD = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_search.not_world");
    public static final TranslatableComponent BEE_SEARCH_BREEDABLE = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_search.breedable");
    public static final TranslatableComponent BEE_SEARCH_NOT_BREEDABLE = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_search.not_breedable");
    public static final TranslatableComponent BEE_SEARCH_MUTATES = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_search.mutates");
    public static final TranslatableComponent BEE_SEARCH_HELP = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_search.help");
    public static final TranslatableComponent VERSION_NUMBER = new TranslatableComponent("gui.resourcefulbees.beepedia");
}
