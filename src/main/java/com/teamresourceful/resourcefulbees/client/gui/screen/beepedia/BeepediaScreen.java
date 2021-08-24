package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.HoneycombData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.*;
import com.teamresourceful.resourcefulbees.client.gui.widget.*;
import com.teamresourceful.resourcefulbees.common.config.Config;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.entity.passive.KittenBee;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.registry.custom.TraitRegistry;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BeepediaScreen extends Screen {

    private static int beesScroll = 0;
    private static int honeyScroll = 0;
    private static int traitScroll = 0;
    private static int combScroll = 0;
    private static boolean searchVisible = false;
    private static String search = null;
    private static String lastSearch = null;

    protected static final LinkedList<BeepediaScreenState> pastStates = new LinkedList<>();
    public static BeepediaScreenState currScreenState = new BeepediaScreenState();
    public final List<String> itemBees;
    public final boolean complete;
    private final CustomBeeEntity entity;
    private final boolean hasShades;
    private final ItemStack stack;

    TextFieldWidget searchBox;

    protected final int xSize;
    protected final int ySize;
    protected int guiLeft;
    protected int guiTop;
    protected int ticksOpen = 0;

    public final Map<String, BeePage> bees = new TreeMap<>();
    protected final Map<String, TraitPage> traits = new TreeMap<>();
    protected final Map<String, HoneyPage> honey = new TreeMap<>();
    protected final Map<String, CombPage> combs = new TreeMap<>();

    ButtonList beesList;
    ButtonList traitsList;
    ButtonList honeyList;
    ButtonList combsList;

    int beepediaXSize = 328;
    int beepediaYSize = 200;

    BeepediaPage home;
    BeepediaPage activePage;
    PageType activeListType = PageType.BEE;
    ButtonList activeList = null;

    final ResourceLocation background = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/screen.png");
    final ResourceLocation beeInfoImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/bee_screen.png");
    final ResourceLocation buttonImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/button.png");
    final ResourceLocation slotImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/slot.png");
    final ResourceLocation shadesBackground = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/shades_of_bees.png");
    final ResourceLocation shadesButtonImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/shades_button.png");
    private Button backButton;
    private final ResourceLocation homeButtons = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/home_buttons.png");
    private final List<ToolTip> tooltips = new LinkedList<>();
    private final List<Interaction> interactions = new LinkedList<>();
    private ModImageButton homeButton;

    @OnlyIn(Dist.CLIENT)
    public BeepediaScreen(CustomBeeEntity entity, List<String> bees, boolean complete, boolean hasShades, ItemStack itemstack) {
        super(new TranslationTextComponent("gui.resourcefulbees.beepedia"));
        if (entity != null) {
            currScreenState.setPageType(PageType.BEE);
            currScreenState.setPageID(entity.getBeeType());
            currScreenState.setBeeSubPage(BeePage.SubPageType.INFO);
        }

        this.stack = itemstack;
        this.entity = entity;
        this.itemBees = bees;
        this.complete = complete;
        this.xSize = beepediaXSize + (entity != null ? 200 : 0);
        this.ySize = beepediaYSize;
        this.hasShades = hasShades;
    }

    public static boolean listChanged() {
        return currScreenState.getLastType() == null || !currScreenState.getLastType().equals(currScreenState.getPageType());
    }

    @Override
    protected void init() {
        super.init();
        buttons.clear();
        this.guiLeft = ((this.width - this.xSize) / 2) + (entity != null ? 100 : 0);
        this.guiTop = (this.height - this.ySize) / 2;
        int x = this.guiLeft;
        int y = this.guiTop;
        int subX = x + 133;
        registerData(subX, y);
        registerButtons(subX, x, y);
        registerSearch(x, y);
        registerTabs(x, y);
        returnState(false);
    }

    /**
     * Collect all data to use for the beepedia pages and create pages for each value.
     *
     * @param subX x position of the sub window
     * @param y    top left corner y position
     */
    private void registerData(int subX, int y) {
        BeeRegistry.getRegistry().getBees().forEach((s, b) -> bees.put(s, new BeePage(this, b, s, subX, y)));
        TraitRegistry.getRegistry().getTraits().forEach((s, b) -> traits.put(s, new TraitPage(this, b, s, subX, y)));
        honey.put("honey", new HoneyPage(this, null, "honey", subX, y, true));
        honey.put("catnip", new HoneyPage(this, KittenBee.getHoneyBottleData(), "catnip", subX, y, false));
        HoneyRegistry.getRegistry().getHoneyBottles().forEach((s, h) -> honey.put(s, new HoneyPage(this, h, s, subX, y, false)));
        combs.put("catnip", new CombPage(this, HoneycombData.DEFAULT, subX, y, "catnip"));
    }

    /**
     * Registering of home row buttons. (Search button, Home button, Back button)
     *
     * @param subX x position of the sub window
     * @param x    top left corner x position
     * @param y    top left corner y position
     */
    private void registerButtons(int subX, int x, int y) {
        home = new HomePage(this, subX, y);
        homeButton = new ModImageButton(x + (beepediaXSize / 2) - 10, y + beepediaYSize - 25, 20, 20, 20, 0, 20, homeButtons, 60, 60, onPress -> selectPage(home));
        backButton = new ModImageButton(x + (beepediaXSize / 2) + 20, y + beepediaYSize - 25, 20, 20, 40, 0, 20, homeButtons, 60, 60, onPress -> {
            if (!pastStates.isEmpty()) {
                goBackState();
                returnState(true);
            }
        });
        addButton(homeButton);
        addButton(new ModImageButton(x + (beepediaXSize / 2) - 40, y + beepediaYSize - 25, 20, 20, 0, 0, 20, homeButtons, 60, 60, onPress -> {
            searchBox.visible = !searchBox.visible;
            setSearchVisible(searchBox.visible);
            updateSearch(beesList, true);
            updateSearch(traitsList, true);
            updateSearch(honeyList, true);
            updateSearch(combsList, true);
        }));
        backButton.active = false;
        addButton(backButton);
        bees.forEach((s, b) -> addButton(b.listButton));
        traits.forEach((s, b) -> addButton(b.listButton));
        honey.forEach((s, b) -> addButton(b.listButton));
        combs.forEach((s, b) -> addButton(b.listButton));
    }

    /**
     * Register the beepedia search bar and register search parameters for each page.
     *
     * @param x top left corner x position
     * @param y top left corner y position
     */
    private void registerSearch(int x, int y) {
        if (this.minecraft != null) this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        searchBox = new TextFieldWidget(Minecraft.getInstance().font, x + 10, y + 143, 98, 10, new TranslationTextComponent("gui.resourcefulbees.beepedia.search"));
        searchBox.visible = false;
        addWidget(searchBox);
        bees.forEach((s, b) -> b.addSearch());
        traits.forEach((s, b) -> b.addSearch());
        honey.forEach((s, b) -> b.addSearch());
        combs.forEach((s, b) -> b.addSearch());
    }

    /**
     * Register the page lists and the tab buttons to switch between them.
     *
     * @param x top left corner x position
     * @param y top left corner y position
     */
    public void registerTabs(int x, int y) {
        ItemStack beeItem = new ItemStack(Items.BEEHIVE);
        ItemStack traitItem = new ItemStack(ModItems.TRAIT_ICON.get());
        ItemStack honeyItem = new ItemStack(Items.HONEY_BOTTLE);
        ItemStack combItem = new ItemStack(Items.HONEYCOMB);
        TabImageButton beesButton = new TabImageButton(x + 45, y + 8, 20, 20, 0, 0, 20, buttonImage, beeItem, 2, 2, onPress ->
                setActiveList(beesList, PageType.BEE), getTooltipProvider(new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.bees")));
        TabImageButton traitsButton = new TabImageButton(x + 66, y + 8, 20, 20, 0, 0, 20, buttonImage, traitItem, 2, 2, onPress ->
                setActiveList(traitsList, PageType.TRAIT), getTooltipProvider(new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits")));
        TabImageButton honeyButton = new TabImageButton(x + 87, y + 8, 20, 20, 0, 0, 20, buttonImage, honeyItem, 2, 2, onPress ->
                setActiveList(honeyList, PageType.HONEY), getTooltipProvider(new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.honey")));
        TabImageButton combButton = new TabImageButton(x + 108, y + 8, 20, 20, 0, 0, 20, buttonImage, combItem, 2, 2, onPress ->
                setActiveList(combsList, PageType.COMB), getTooltipProvider(new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.combs")));

        addButton(beesButton);
        addButton(traitsButton);
        addButton(honeyButton);
        addButton(combButton);
        beesList = new ButtonList(x + 8, y + 31, 121, 141, 21, beesButton, bees);
        traitsList = new ButtonList(x + 8, y + 31, 121, 141, 21, traitsButton, traits);
        honeyList = new ButtonList(x + 8, y + 31, 121, 141, 21, honeyButton, honey);
        combsList = new ButtonList(x + 8, y + 31, 121, 141, 21, combButton, combs);
    }

    /**
     * Reloads the beepedia screen to the last saved state.
     *
     * @param goingBack whether the beepedia is reloading from a back button click or not.1
     */
    private void returnState(boolean goingBack) {
        setActive(currScreenState.getPageType(), currScreenState.getPageID(), goingBack);
        searchBox.visible = isSearchVisible();
        searchBox.setValue(getSearch() != null ? getSearch() : "");
        if (beesScroll != 0) beesList.setScrollPos(beesScroll);
        if (honeyScroll != 0) honeyList.setScrollPos(honeyScroll);
        if (traitScroll != 0) traitsList.setScrollPos(traitScroll);
        if (combScroll != 0) combsList.setScrollPos(combScroll);
    }

    /**
     * Removes the most recently saved state.
     */
    private static void goBackState() {
        currScreenState = pastStates.pop();
    }

    /***
     * finds the correct pageType and gets the pageID
     * @param activePage the page you want to switch to
     */
    public void selectPage(BeepediaPage activePage) {
        if (activePage instanceof HomePage) {
            setActive(null, null);
        } else if (activePage instanceof BeePage) {
            setActive(PageType.BEE, activePage.id);
        } else if (activePage instanceof TraitPage) {
            setActive(PageType.TRAIT, activePage.id);
        } else if (activePage instanceof HoneyPage) {
            setActive(PageType.HONEY, activePage.id);
        } else if (activePage instanceof CombPage) {
            setActive(PageType.COMB, activePage.id);
        }
    }

    /***
     * @see #setActive(PageType, String, boolean)
     * @param pageType the page type, this is used to select the right list
     * @param pageID the page id of the page
     */
    public void setActive(PageType pageType, String pageID) {
        setActive(pageType, pageID, false);
    }

    /***
     * activates a page
     * @param pageType the page type, this is used to select the right list
     * @param pageID the page id of the page
     */
    public void setActive(PageType pageType, String pageID, boolean goingBack) {
        if (pageType == null || pageID == null) {
            activatePage(PageType.BEE, home, beesList, goingBack);
            return;
        }
        BeepediaPage page = null;
        ButtonList list = null;
        switch (pageType) {
            case BEE:
                page = bees.get(pageID);
                list = beesList;
                break;
            case HONEY:
                page = honey.get(pageID);
                list = honeyList;
                break;
            case TRAIT:
                page = traits.get(pageID);
                list = traitsList;
                break;
            case COMB:
                page = combs.get(pageID);
                list = combsList;
        }
        // collect page if page does not match active list type.
        // this can be inaccurate as some pages may share IDs
        if (page == null) page = bees.get(pageID);
        if (page == null) page = honey.get(pageID);
        if (page == null) page = traits.get(pageID);
        if (page == null) page = combs.get(pageID);
        activatePage(pageType, page, list, goingBack);
    }

    /***
     * closes the old page and list and activates the new page and list
     *
     * @param type the type of the list that is to be activated
     * @param page the new page to be activated
     * @param list the list to be activated
     * @param goingBack whether it is loading from a previous state
     */
    private void activatePage(PageType type, BeepediaPage page, ButtonList list, boolean goingBack) {
        if (list == null) throw new IllegalStateException("IF THIS SOMEHOW HAPPENS YOU BROKE THE GAME");
        // close active page and reset screen state
        if (page == null) page = home;
        if (this.activePage != null) {
            if (!this.activePage.getClass().equals(page.getClass()) && !(this.activePage instanceof HomePage) && !goingBack) {
                saveScreenState();
            }
            this.activePage.closePage();
        }

        // set current state
        if (currScreenState.getPageType() == null) currScreenState.setPageType(type);
        currScreenState.setPageID(page.id);

        // update list
        boolean forceUpdate = this.activeList == null;
        if (currScreenState.pageChanged() || goingBack || forceUpdate) {
            if (this.activeList != null) this.activeList.setActive(false, goingBack);
            this.activeList = list;
            this.activeList.setActive(true, goingBack || forceUpdate);
            this.activeListType = type;
            if (BeepediaScreen.searchVisible && goingBack)
                this.activeList.updateReducedList(BeepediaScreen.getSearch(), true);
        }
        // open page
        this.activePage = page;
        this.activePage.openPage();
    }

    /**
     * saves the current state to the state list
     */
    public static void saveScreenState() {
        pastStates.push(currScreenState);
        currScreenState = new BeepediaScreenState();
    }

    private void setActiveList(ButtonList buttonList, PageType type) {
        currScreenState.setPageType(type);
        activeListType = type;
        if (this.activeList != null) this.activeList.setActive(false, true);
        this.activeList = buttonList;
        this.activeList.setActive(true, true);
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(matrixStack, 0);
        tooltips.clear();
        interactions.clear();
        drawBackground(matrixStack, partialTick, mouseX, mouseY);
        updateSearch();
        if (entity != null) {
            drawEntityData(matrixStack);
        }
        if (hasShades) {
            drawShadesButton(matrixStack);
        }
        super.render(matrixStack, mouseX, mouseY, partialTick);
        searchBox.render(matrixStack, mouseX, mouseY, partialTick);
        drawForeground(matrixStack, mouseX, mouseY);
        drawTooltips(matrixStack, mouseX, mouseY);

    }

    private void drawShadesButton(MatrixStack matrix) {
        int x = this.guiLeft;
        int y = this.guiTop;

        int buttonX = x + beepediaXSize + 2;
        int buttonY = y + beepediaYSize - 32;
        ItemStack book = PatchouliAPI.get().getBookStack(ModConstants.SHADES_OF_BEES);
        Minecraft.getInstance().getTextureManager().bind(shadesBackground);
        blit(matrix, buttonX, buttonY, 0, 0, 30, 30, 30, 30);

        TabImageButton shadesButton = new TabImageButton(buttonX + 6, buttonY + 6, 18, 18, 0, 0, 18,
                shadesButtonImage, book, 1, 1, onPress -> PatchouliAPI.get().openBookGUI(ModConstants.SHADES_OF_BEES),
                18, 36) {

            @Override
            public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
                TranslationTextComponent s = new TranslationTextComponent("book.resourcefulbees.name");
                BeepediaScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        };
        this.addButton(shadesButton);
    }

    private void drawEntityData(@NotNull MatrixStack matrix) {
        int x = this.guiLeft;
        int y = this.guiTop;

        int boxX = x + beepediaXSize + 12;
        int boxY = y + 10;
        String blockFormat = "[%d, %d, %d]";
        this.font.draw(matrix, entity.getDisplayName(), boxX, boxY, Color.WHITE.getRGB());
        this.font.draw(matrix, String.format("Health: %.0f/%.0f", this.entity.getHealth(), this.entity.getMaxHealth()), boxX, boxY + 10.0f, 5592405);
        BlockPos currentPos = entity.blockPosition();
        BlockPos hivePos = entity.getHivePos();
        BlockPos flowerPos = entity.getSavedFlowerPos();
        this.font.draw(matrix, String.format(blockFormat, currentPos.getX(), currentPos.getY(), currentPos.getZ()), boxX, boxY + 20.0f, 5592405);
        if (hivePos != null) {
            this.font.draw(matrix, String.format(blockFormat, hivePos.getX(), hivePos.getY(), hivePos.getZ()), boxX, boxY + 30.0f, 5592405);
        }
        if (flowerPos != null) {
            this.font.draw(matrix, String.format(blockFormat, flowerPos.getX(), flowerPos.getY(), flowerPos.getZ()), boxX, boxY + 40.0f, 5592405);
        }
        if (this.entity.hasNectar()) {
            this.font.draw(matrix, "pollenated", boxX, boxY + 50.0F, 5592405);
        }
    }

    protected void drawBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        Minecraft client = this.minecraft;
        homeButton.active = activePage != home;
        backButton.active = !pastStates.isEmpty();
        int x = this.guiLeft;
        int y = this.guiTop;
        if (client != null) {
            client.getTextureManager().bind(background);
            blit(matrix, x, y, 0, 0, this.beepediaXSize, this.beepediaYSize, this.beepediaXSize, this.beepediaYSize);
        }
        if (entity != null) {
            int boxX = x + beepediaXSize + 2;
            Minecraft.getInstance().getTextureManager().bind(beeInfoImage);
            blit(matrix, boxX, y, 0, 0, 100, 150, 100, 150);
        }
        activePage.renderBackground(matrix, partialTick, mouseX, mouseY);
        activeList.updateList();
        if (searchBox.visible) activeList.setSearchHeight();
        else activeList.resetHeight();
    }

    private void updateSearch() {
        if (searchBox.visible) {
            setFocused(searchBox);
            searchBox.setFocus(true);
        }
        if (searchBox.isFocused()) {
            String text = searchBox.getValue();
            if (text.isEmpty()) {
                setSearch(null);
            } else {
                setSearch(text);
            }
        }
        updateSearch(beesList, false);
        updateSearch(honeyList, false);
        updateSearch(traitsList, false);
    }

    private void updateSearch(ButtonList list, boolean isSearchToggled) {
        if (!searchUpdated() && !isSearchToggled) return;
        if (isSearchVisible()) {
            if (getSearch() != null) {
                list.updateReducedList(searchBox.getValue(), true);
            } else {
                list.updateReducedList(null, true);
            }
        } else {
            list.updateReducedList(null, true);
        }
    }

    public static boolean searchUpdated() {
        if (lastSearch == null) return search != null;
        return !lastSearch.equals(search);
    }

    @Override
    public void tick() {
        super.tick();
        ticksOpen++;
        activePage.tick(ticksOpen);
    }

    protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title, (float) this.guiLeft + 10, (float) this.guiTop + beepediaYSize - 20, 5592405);
        activePage.renderForeground(matrixStack, mouseX, mouseY);
        TranslationTextComponent title;
        switch (activeListType) {
            case TRAIT:
                title = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits");
                break;
            case HONEY:
                title = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.honey");
                break;
            case COMB:
                title = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.combs");
                break;
            default:
                title = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.bees");
                break;
        }
        this.font.draw(matrixStack, title.withStyle(TextFormatting.WHITE), (float) this.guiLeft + 10, (float) this.guiTop + 20, -1);
    }


    private void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {
        activePage.drawTooltips(matrixStack, mouseX, mouseY);
        beesList.button.renderToolTip(matrixStack, mouseX, mouseY);
        traitsList.button.renderToolTip(matrixStack, mouseX, mouseY);
        honeyList.button.renderToolTip(matrixStack, mouseX, mouseY);
        combsList.button.renderToolTip(matrixStack, mouseX, mouseY);
        tooltips.forEach(t -> t.draw(this, matrixStack, mouseX, mouseY));
    }

    public Map<String, TraitPage> getTraits(CustomBeeData beeData) {
        Map<String, TraitPage> pages = new HashMap<>();
        if (!beeData.getTraitData().getTraits().isEmpty()) return pages;
        for (String traitName : beeData.getTraitData().getTraits()) {
            if (traitName != null) pages.put(traitName, traits.get(traitName));
        }
        return pages;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        boolean subScrolled = activePage.mouseScrolled(mouseX, mouseY, scrollAmount);
        if (subScrolled) return true;

        if (activeList != null) {
            activeList.updatePos((int) (scrollAmount * 8));
        }
        updateScrollPos(beesList, traitsList, honeyList, combsList);
        return super.mouseScrolled(mouseX, mouseY, scrollAmount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (activePage.mouseClicked(mouseX, mouseY, mouseButton)) {
            Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        interactions.forEach(b -> {
            if (b.onMouseClick((int) mouseX, (int) mouseY)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        });
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public static void updateScrollPos(ButtonList beesList, ButtonList traitsList, ButtonList honeyList, ButtonList combsList) {
        beesScroll = beesList.getScrollPos();
        traitScroll = traitsList.getScrollPos();
        honeyScroll = honeyList.getScrollPos();
        combScroll = combsList.getScrollPos();
    }

    public static void setSearch(String search) {
        BeepediaScreen.lastSearch = BeepediaScreen.search;
        BeepediaScreen.search = search;
    }

    public static String getSearch() {
        return search;
    }

    public static void setSearchVisible(boolean visible) {
        BeepediaScreen.searchVisible = visible;
    }

    public static boolean isSearchVisible() {
        return searchVisible;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static boolean mouseHovering(float x, float y, int width, int height, int mouseX, int mouseY) {
        return mouseX > x && mouseY > y && mouseX < x + width && mouseY < y + height;
    }

    public void drawSlot(MatrixStack matrix, IItemProvider item, int amount, int xPos, int yPos) {
        if (item instanceof FlowingFluidBlock) {
            drawFluidSlot(matrix, new FluidStack(((FlowingFluidBlock) item).getFluid().getSource(), amount), xPos, yPos, true);
        } else {
            drawSlot(matrix, new ItemStack(item, amount), xPos, yPos);
        }
    }

    public void drawSlot(MatrixStack matrix, Block item, int xPos, int yPos) {
        drawSlot(matrix, item, 1, xPos, yPos);
    }

    public void drawSlot(MatrixStack matrix, IItemProvider item, int xPos, int yPos) {
        if (item instanceof FlowingFluidBlock) {
            drawFluidSlot(matrix, new FluidStack(((FlowingFluidBlock) item).getFluid().getSource(), 1000), xPos, yPos, false);
        } else {
            drawSlot(matrix, new ItemStack(item), xPos, yPos);
        }
    }

    // Unused??
    public void drawFluidSlot(MatrixStack matrix, FluidStack fluidStack, int xPos, int yPos) {
        drawFluidSlot(matrix, fluidStack, xPos, yPos, true);
    }

    public void drawFluidSlot(MatrixStack matrix, FluidStack fluidStack, int xPos, int yPos, boolean showAmount) {
        if (fluidStack.isEmpty()) return;
        drawFluidSlotNoToolTip(matrix, fluidStack, xPos, yPos);
        registerFluidTooltip(fluidStack, xPos, yPos, showAmount);
    }

    public void drawFluidSlotNoToolTip(MatrixStack matrix, FluidStack fluidStack, int xPos, int yPos) {
        if (fluidStack.isEmpty()) return;
        getMinecraft().getTextureManager().bind(slotImage);
        blit(matrix, xPos, yPos, 0, 0, 20, 20, 20, 20);
        RenderUtils.renderFluid(matrix, fluidStack, xPos + 2, yPos + 2, this.getBlitOffset());
    }

    public void drawSlot(MatrixStack matrix, ItemStack item, int xPos, int yPos) {
        drawSlotNoToolTip(matrix, item, xPos, yPos);
        registerItemTooltip(item, xPos, yPos);
    }

    public void drawSlotNoToolTip(MatrixStack matrix, IItemProvider item, int xPos, int yPos) {
        if (item instanceof FlowingFluidBlock) {
            drawFluidSlotNoToolTip(matrix, new FluidStack(((FlowingFluidBlock) item).getFluid().getSource(), 1000), xPos, yPos);
        } else {
            drawSlotNoToolTip(matrix, new ItemStack(item), xPos, yPos);
        }
    }

    public void drawSlotNoToolTip(MatrixStack matrix, ItemStack item, int xPos, int yPos) {
        if (item.isEmpty()) return;
        getMinecraft().getTextureManager().bind(slotImage);
        blit(matrix, xPos, yPos, 0, 0, 20, 20, 20, 20);
        getMinecraft().getItemRenderer().renderGuiItem(item, xPos + 2, yPos + 2);
        getMinecraft().getItemRenderer().renderGuiItemDecorations(font, item, xPos + 2, yPos + 2);
    }

    public void drawEmptySlot(MatrixStack matrix, int xPos, int yPos) {
        getMinecraft().getTextureManager().bind(slotImage);
        blit(matrix, xPos, yPos, 0, 0, 20, 20, 20, 20);
    }

    private void registerItemTooltip(ItemStack item, int xPos, int yPos) {
        tooltips.add(new ToolTip(xPos + 2, yPos + 2, 16, 16, item));
    }

    private void registerFluidTooltip(FluidStack fluid, int xPos, int yPos, boolean showAmount) {
        tooltips.add(new ToolTip(xPos + 2, yPos + 2, 16, 16, fluid, showAmount));
    }

    public void registerInteraction(int xPos, int yPos, Supplier<Boolean> supplier) {
        interactions.add(new Interaction(xPos + 2, yPos + 2, 16, 16, supplier));
    }

    @Override
    public <T extends Widget> @NotNull T addButton(@NotNull T widget) {
        return super.addButton(widget);
    }

    public Entity initEntity(EntityType<?> entityType) {
        return entityType.create(getMinecraft().level);
    }

    public Button.ITooltip getTooltipProvider(ITextComponent textComponent) {
        return (button, matrix, mouseX, mouseY) -> renderTooltip(matrix, textComponent, mouseX, mouseY);
    }

    // Unused??
    public void drawInteractiveFluidSlot(MatrixStack matrix, FluidStack fluidStack, int xPos, int yPos, int mouseX, int mouseY, Supplier<Boolean> supplier) {
        drawInteractiveFluidSlot(matrix, fluidStack, xPos, yPos, mouseX, mouseY, true, supplier);
    }

    public void drawInteractiveFluidSlot(MatrixStack matrix, FluidStack fluidStack, int xPos, int yPos, int mouseX, int mouseY, boolean showAmount, Supplier<Boolean> supplier) {
        if (fluidStack.isEmpty()) return; // TODO REMOVE DUPLICATE CODE
        registerInteraction(xPos, yPos, supplier);
        getMinecraft().getTextureManager().bind(buttonImage);
        if (mouseHovering(xPos, yPos, 20, 20, mouseX, mouseY)) {
            blit(matrix, xPos, yPos, 0, 20, 20, 20, 20, 60);
        } else {
            blit(matrix, xPos, yPos, 0, 0, 20, 20, 20, 60);
        }
        RenderUtils.renderFluid(matrix, fluidStack, xPos + 2, yPos + 2, this.getBlitOffset());
        registerFluidTooltip(fluidStack, xPos, yPos, showAmount);
    }

    // Unused??
    public void drawInteractiveSlot(MatrixStack matrix, ItemStack item, int xPos, int yPos, int mouseX, int mouseY, Supplier<Boolean> supplier) {
        if (item.isEmpty()) return; // TODO REMOVE DUPLICATE CODE
        registerInteraction(xPos, yPos, supplier);
        getMinecraft().getTextureManager().bind(buttonImage);
        if (mouseHovering(xPos, yPos, 20, 20, mouseX, mouseY)) {
            blit(matrix, xPos, yPos, 0, 20, 20, 20, 20, 60);
        } else {
            blit(matrix, xPos, yPos, 0, 0, 20, 20, 20, 60);
        }
        getMinecraft().getItemRenderer().renderGuiItem(item, xPos + 2, yPos + 2);
        getMinecraft().getItemRenderer().renderGuiItemDecorations(font, item, xPos + 2, yPos + 2);
        registerItemTooltip(item, xPos, yPos);
    }

    public Map<String, BeePage> getBees(ItemStack bottleData) {
        return bees.entrySet().stream()
                .filter(entrySet -> mapContainsBottle(entrySet.getValue(), bottleData) && (!Config.BEEPEDIA_HIDE_LOCKED.get() || entrySet.getValue().beeUnlocked))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean mapContainsBottle(BeePage page, ItemStack bottleData) {
        Item beeBottle = null;    /////BOTTLE OUTPUTS DON'T EXIST ANYMORE
        return null == bottleData.getItem();
    }


    public Map<String, BeePage> getBees(String traitName) {
        return bees.entrySet().stream()
                .filter(entrySet -> mapContainsTraitName(entrySet.getValue(), traitName) && (!Config.BEEPEDIA_HIDE_LOCKED.get() || entrySet.getValue().beeUnlocked))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean mapContainsTraitName(BeePage page, String traitName) {
        return !page.beeData.getTraitData().getTraits().isEmpty() && page.beeData.getTraitData().getTraits().contains(traitName);
    }

    public BeePage getBee(String s) {
        return bees.get(s);
    }


    public enum PageType {
        // main pages
        BEE,
        HONEY,
        COMB,
        TRAIT
    }
}
