package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums.BeepediaListTypes;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums.PageTypes;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums.SubPageTypes;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.*;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.stats.BeeBeepediaStats;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.stats.CombBeepediaStats;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.stats.HoneyBeepediaStats;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.stats.TraitBeepediaStats;
import com.teamresourceful.resourcefulbees.client.gui.widget.ScreenArea;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.network.packets.BeepediaEntityMessage;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.TraitRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class BeepediaHandler {

    private static BeepediaPage selectedPage;

    private BeepediaHandler() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }


    protected static final LinkedList<Queue<String>> pastStates = new LinkedList<>();

    private static final ScreenArea SUB_SCREEN_AREA = new ScreenArea(122, 0, BeepediaScreen.SCREEN_WIDTH - 122, BeepediaScreen.SCREEN_HEIGHT);

    /**
     * currently selected state
     * <p>
     * if state is null show loading screen
     */
    protected static BeepediaState currScreenState = null;


    /**
     * list of bee states
     * <p>
     * generate when the beepedia is opened for the first time
     */
    public static Map<String, BeeBeepediaStats> beeStats = new LinkedHashMap<>();

    public static BeePage beePage = new BeePage(SUB_SCREEN_AREA);

    /**
     * list of trait states
     * <p>
     * generate when the beepedia is opened for the first time
     */
    public static Map<String, TraitBeepediaStats> traitStats = new LinkedHashMap<>();

    public static TraitPage traitPage = new TraitPage(SUB_SCREEN_AREA);

    /**
     * list of honey states
     * <p>
     * generate when the beepedia is opened for the first time
     */
    public static Map<String, HoneyBeepediaStats> honeyStats = new LinkedHashMap<>();

    public static HoneyPage honeyPage = new HoneyPage(SUB_SCREEN_AREA);

    /**
     * list of trait states
     * <p>
     * generate when the beepedia is opened for the first time
     */
    public static Map<String, CombBeepediaStats> combStats = new LinkedHashMap<>();

    public static CombPage combPage = new CombPage(SUB_SCREEN_AREA);

    public static ListPage listPage = new ListPage(0, 0, 121, BeepediaScreen.SCREEN_HEIGHT);

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
        allPages.add(listPage);
        allPages.add(homePage);
        allPages.add(beePage);
        allPages.add(traitPage);
        allPages.add(honeyPage);
        allPages.add(combPage);
        allPages.add(helpPage);

        BeeRegistry.getRegistry().getBees().forEach((s, b) -> beeStats.put(b.getRegistryID().toString(), new BeeBeepediaStats(b)));
        TraitRegistry.getRegistry().getTraits().forEach((s, t) -> traitStats.put(s, new TraitBeepediaStats(t)));
        HoneyRegistry.getRegistry().getHoneyBottles().forEach((s, h) -> honeyStats.put(h.getRegistryID().toString(), new HoneyBeepediaStats(h)));
        init = true;
    }

    /**
     * generate save and select the collectedBeeInfoState
     */
    public static void collectBee(BeepediaEntityMessage message) {
        BeepediaState.newState(BeepediaListTypes.BEES, PageTypes.COLLECTED, message.id().toString(), SubPageTypes.INFO, null);
    }

    public static void registerScreen(BeepediaScreen beepedia) {
        // register buttons for all pages
        for (BeepediaPage page : allPages) {
            page.reset();
            page.registerScreen(beepedia);

        }
        openState();
        listPage.visible = true;
    }


    public static void preInit() {
        listPage.preInit();
        selectedPage = homePage;
        switch (BeepediaState.currentState.page) {
            case BEES:
                if (beeStats.containsKey(BeepediaState.currentState.listItem)) {
                    beePage.preInit(beeStats.get(BeepediaState.currentState.listItem));
                    selectedPage = beePage;
                }
                break;
            case TRAITS:
                if (traitStats.containsKey(BeepediaState.currentState.listItem)) {
                    traitPage.preInit(traitStats.get(BeepediaState.currentState.listItem));
                    selectedPage = traitPage;
                }
                break;
            case HONEY:
                if (honeyStats.containsKey(BeepediaState.currentState.listItem)) {
                    honeyPage.preInit(honeyStats.get(BeepediaState.currentState.listItem));
                    selectedPage = honeyPage;
                }
                break;
            case COMBS:
                if (combStats.containsKey(BeepediaState.currentState.listItem)) {
                    combPage.preInit(combStats.get(BeepediaState.currentState.listItem));
                    selectedPage = combPage;
                }
                break;
            case HELP:
                helpPage.preInit();
                selectedPage = helpPage;
                break;
            case COLLECTED:
                collectedPage.preInit();
                selectedPage = collectedPage;
                break;
            case HOME:
                selectedPage.preInit();
                break;
            default:
        }
    }

    public static void drawPage(PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
        listPage.renderBackground(matrix, partialTicks, mouseX, mouseY);
        if (selectedPage != null) selectedPage.renderBackground(matrix, partialTicks, mouseX, mouseY);
        listPage.renderForeground(matrix, mouseX, mouseY);
        if (selectedPage != null) selectedPage.renderForeground(matrix, mouseX, mouseY);
    }

    public static void closeState() {
        switch (BeepediaState.currentState.page) {
            case HOME -> homePage.visible = false;
            case HELP -> helpPage.visible = false;
            case BEES -> beePage.visible = false;
            case TRAITS -> traitPage.visible = false;
            case HONEY -> honeyPage.visible = false;
            case COMBS -> combPage.visible = false;
            case COLLECTED -> collectedPage.visible = false;
        }
    }

    public static void openState() {
        switch (BeepediaState.currentState.page) {
            case HOME -> homePage.visible = true;
            case HELP -> helpPage.visible = true;
            case BEES -> beePage.visible = true;
            case TRAITS -> traitPage.visible = true;
            case HONEY -> honeyPage.visible = true;
            case COMBS -> combPage.visible = true;
            case COLLECTED -> collectedPage.visible = true;
        }
    }

    public static void openHomeScreen() {

    }

    public static void tick(int ticksOpen) {
        if (selectedPage == null) return;
        listPage.tick(ticksOpen);
        selectedPage.tick(ticksOpen);
    }
}
