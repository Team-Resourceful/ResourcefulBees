package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums.BeepediaListTypes;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums.PageTypes;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums.SubPageTypes;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.*;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.search.BeeBeepediaStats;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.search.CombBeepediaStats;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.search.HoneyBeepediaStats;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.search.TraitBeepediaStats;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.states.CollectedBeeInfoState;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.states.HelpState;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.states.ListPage;
import com.teamresourceful.resourcefulbees.client.gui.widget.BeepediaScreenArea;
import com.teamresourceful.resourcefulbees.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.network.packets.BeepediaEntityMessage;
import com.teamresourceful.resourcefulbees.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.registry.HoneyRegistry;
import com.teamresourceful.resourcefulbees.registry.ModItems;
import com.teamresourceful.resourcefulbees.registry.TraitRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.*;

public class BeepediaHandler {

    private BeepediaHandler() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }


    protected static final LinkedList<Queue<String>> pastStates = new LinkedList<>();

    private static final BeepediaScreenArea SUB_SCREEN_AREA = new BeepediaScreenArea(122, 0, BeepediaScreen.SCREEN_WIDTH - 122, BeepediaScreen.SCREEN_HEIGHT);

    /**
     * currently selected state
     * <p>
     * if state is null show loading screen
     */
    protected static BeepediaState currScreenState = null;

    /**
     * the currently selected bee
     * <p>
     * when a bee is interacted with with a beepedia store the data in this state.
     */
    protected static CollectedBeeInfoState collectedBeeState = null;

    /**
     * all of the help pages that can be selected
     */
    protected static Map<String, HelpState> helpStates = null;

    /**
     * list of bee states
     * <p>
     * generate when the beepedia is opened for the first time
     */
    protected static Map<String, BeeBeepediaStats> beeStats = new LinkedHashMap<>();

    protected static BeePage beePage = new BeePage(SUB_SCREEN_AREA);

    /**
     * list of trait states
     * <p>
     * generate when the beepedia is opened for the first time
     */
    protected static Map<String, TraitBeepediaStats> traitStats = new LinkedHashMap<>();

    protected static TraitPage traitPage = new TraitPage(SUB_SCREEN_AREA);

    /**
     * list of honey states
     * <p>
     * generate when the beepedia is opened for the first time
     */
    protected static Map<String, HoneyBeepediaStats> honeyStats = new LinkedHashMap<>();

    protected static HoneyPage honeyPage = new HoneyPage(SUB_SCREEN_AREA);

    /**
     * list of trait states
     * <p>
     * generate when the beepedia is opened for the first time
     */
    protected static Map<String, CombBeepediaStats> combStats = new LinkedHashMap<>();

    protected static CombPage combPage = new CombPage(SUB_SCREEN_AREA);

    /**
     * list of all lists that can be selected
     */
    protected static Map<BeepediaListTypes, ListPage> listStates = new LinkedHashMap<BeepediaListTypes, ListPage>();

    private static HomePage homePage = new HomePage(SUB_SCREEN_AREA);
    private static HelpPage helpPage = new HelpPage(SUB_SCREEN_AREA);
    private static CollectedBeePage collectedPage = new CollectedBeePage(SUB_SCREEN_AREA);

    private static Set<BeepediaPage> allPages = new HashSet<>();

    private static boolean init = false;


    public static void initBeepediaStates() {
        if (init) return;
        BeePage.initPages();
        HoneyPage.initPages();
        TraitPage.initPages();
        CombPage.initPages();
        allPages.add(homePage);
        allPages.add(beePage);
        allPages.add(traitPage);
        allPages.add(honeyPage);
        allPages.add(combPage);
        allPages.add(helpPage);

        BeeRegistry.getRegistry().getBees().forEach((s, b) -> beeStats.put(b.getRegistryID().toString(), new BeeBeepediaStats(b)));
        TraitRegistry.getRegistry().getTraits().forEach((s, t) -> traitStats.put(s, new TraitBeepediaStats(t)));
        HoneyRegistry.getRegistry().getHoneyBottles().forEach((s, h) -> honeyStats.put(h.getRegistryID().toString(), new HoneyBeepediaStats(h)));
//        BeeInfoUtils.getHoneycombs().forEach((s, b) -> combStats.put())

        init = true;
    }

    /**
     * generate save and select the collectedBeeInfoState
     */
    public static void collectBee(BeepediaEntityMessage message) {
        currScreenState.newState(BeepediaListTypes.BEES, PageTypes.COLLECTED, message.id.toString(), SubPageTypes.INFO, null);
    }

    public static void registerScreen(BeepediaScreen beepedia) {
        registerLists(beepedia);

        // register buttons for all pages
        for (BeepediaPage page : allPages) {
            page.registerButtons(beepedia);
            page.closePage();
        }
    }

    private static void registerLists(BeepediaScreen beepedia) {
        int x = beepedia.guiLeft;
        int y = beepedia.guiTop;

        BeepediaScreenArea screenArea = new BeepediaScreenArea(0, 0, 121, BeepediaScreen.SCREEN_HEIGHT);

        ItemStack beeItem = new ItemStack(Items.BEEHIVE);
        ItemStack traitItem = new ItemStack(ModItems.TRAIT_ICON.get());
        ItemStack honeyItem = new ItemStack(Items.HONEY_BOTTLE);
        ItemStack combItem = new ItemStack(Items.HONEYCOMB);

        ListPage beeList = new ListPage(BeepediaListTypes.BEES.name(), beeItem, screenArea, 45, BeepediaLang.TAB_BEES, beeStats);
        ListPage traitList = new ListPage(BeepediaListTypes.TRAITS.name(), traitItem, screenArea, 66, BeepediaLang.TAB_TRAITS, traitStats);
        ListPage honeyList = new ListPage(BeepediaListTypes.HONEY.name(), honeyItem, screenArea, 87, BeepediaLang.TAB_HONEY, honeyStats);
        ListPage combList = new ListPage(BeepediaListTypes.COMBS.name(), combItem, screenArea, 108, BeepediaLang.TAB_COMBS, combStats);

        listStates.put(BeepediaListTypes.BEES, beeList);
        listStates.put(BeepediaListTypes.TRAITS, traitList);
        listStates.put(BeepediaListTypes.HONEY, honeyList);
        listStates.put(BeepediaListTypes.COMBS, combList);
        allPages.addAll(listStates.values());
    }

    public static void drawList(BeepediaScreen beepedia, MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        listStates.get(BeepediaState.currentState.selectedList).preInit(beepedia);
        listStates.get(BeepediaState.currentState.selectedList).renderBackground(matrix, partialTick, mouseX, mouseY);
        listStates.get(BeepediaState.currentState.selectedList).renderForeground(matrix, mouseX, mouseY);
        listStates.get(BeepediaState.currentState.selectedList).drawTooltips(matrix, mouseX, mouseY);
    }

    public static void drawPage(BeepediaScreen beepedia, MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        BeepediaPage selectedPage = homePage;
        switch (BeepediaState.currentState.page) {
            case BEES:
                if (beeStats.containsKey(BeepediaState.currentState.listItem)) {
                    beePage.preInit(beepedia, beeStats.get(BeepediaState.currentState.listItem));
                    selectedPage = beePage;
                }
                break;
            case TRAITS:
                if (traitStats.containsKey(BeepediaState.currentState.listItem)) {
                    traitPage.preInit(beepedia, traitStats.get(BeepediaState.currentState.listItem));
                    selectedPage = traitPage;
                }
                break;
            case HONEY:
                if (honeyStats.containsKey(BeepediaState.currentState.listItem)) {
                    honeyPage.preInit(beepedia, honeyStats.get(BeepediaState.currentState.listItem));
                    selectedPage = honeyPage;
                }
                break;
            case COMBS:
                if (combStats.containsKey(BeepediaState.currentState.listItem)) {
                    combPage.preInit(beepedia, combStats.get(BeepediaState.currentState.listItem));
                    selectedPage = combPage;
                }
                break;
            case HELP:
                helpPage.preInit(beepedia);
                selectedPage = helpPage;
                break;
            case COLLECTED:
                collectedPage.preInit(beepedia);
                selectedPage = collectedPage;
                break;
            case HOME:
                selectedPage.preInit(beepedia);
                break;
            default:
        }
        selectedPage.renderBackground(matrix, partialTicks, mouseX, mouseY);
        selectedPage.renderForeground(matrix, mouseX, mouseY);
        selectedPage.drawTooltips(matrix, mouseX, mouseY);
    }

    public static void closeState() {
        switch (BeepediaState.currentState.page) {
            case HOME:
                homePage.closePage();
                break;
            case HELP:
                helpPage.closePage();
                break;
            case BEES:
                beePage.closePage();
                break;
            case TRAITS:
                traitPage.closePage();
                break;
            case HONEY:
                honeyPage.closePage();
                break;
            case COMBS:
                combPage.closePage();
                break;
            case COLLECTED:
                collectedPage.closePage();
                break;
            default:
        }
    }

    public static void openState() {
        switch (BeepediaState.currentState.page) {
            case HOME:
                homePage.openPage();
                break;
            case HELP:
                helpPage.openPage();
                break;
            case BEES:
                beePage.openPage();
                break;
            case TRAITS:
                traitPage.openPage();
                break;
            case HONEY:
                honeyPage.openPage();
                break;
            case COMBS:
                combPage.openPage();
                break;
            case COLLECTED:
                collectedPage.openPage();
                break;
            default:
        }
    }
}
