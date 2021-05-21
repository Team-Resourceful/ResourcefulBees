package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.BeePage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.HomePage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.HoneyPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.TraitPage;
import com.teamresourceful.resourcefulbees.client.gui.widget.ButtonList;
import com.teamresourceful.resourcefulbees.client.gui.widget.ModImageButton;
import com.teamresourceful.resourcefulbees.client.gui.widget.TabImageButton;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.config.Config;
import com.teamresourceful.resourcefulbees.entity.passive.KittenBee;
import com.teamresourceful.resourcefulbees.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.registry.HoneyRegistry;
import com.teamresourceful.resourcefulbees.registry.TraitRegistry;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.utils.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class BeepediaScreen extends Screen {

    private static int beesScroll = 0;
    private static int honeyScroll = 0;
    private static int traitScroll = 0;
    private static boolean searchVisible = false;
    private static String search = null;
    private static String lastSearch = null;

    protected static final LinkedList<BeepediaScreenState> pastStates = new LinkedList<>();
    public static BeepediaScreenState currScreenState = new BeepediaScreenState();
    public final List<String> itemBees;
    public final boolean complete;

    EditBox searchBox;

    protected final int xSize;
    protected final int ySize;
    protected int guiLeft;
    protected int guiTop;
    protected int ticksOpen = 0;

    public final Map<String, BeePage> bees = new TreeMap<>();
    protected final Map<String, TraitPage> traits = new TreeMap<>();
    protected final Map<String, HoneyPage> honey = new TreeMap<>();

    ButtonList beesList;
    ButtonList traitsList;
    ButtonList honeyList;

    BeepediaPage home;
    BeepediaPage activePage;
    PageType activeListType = PageType.BEE;
    ButtonList activeList = null;

    final ResourceLocation background = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/screen.png");
    final ResourceLocation buttonImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/button.png");
    final ResourceLocation slotImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/slot.png");
    private Button backButton;
    private final ResourceLocation homeButtons = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/home_buttons.png");
    private final List<ItemTooltip> itemTooltips = new LinkedList<>();
    private final List<FluidTooltip> fluidTooltips = new LinkedList<>();
    private final List<Interaction> interactions = new LinkedList<>();
    private ModImageButton homeButton;

    @OnlyIn(Dist.CLIENT)
    public BeepediaScreen(String pageID, List<String> bees, boolean complete) {
        super(new TranslatableComponent("gui.resourcefulbees.beepedia"));
        if (pageID != null) {
            currScreenState.setPageType(PageType.BEE);
            currScreenState.setPageID(pageID);
            currScreenState.setBeeSubPage(BeePage.SubPageType.INFO);
        }
        this.itemBees = bees;
        this.complete = complete;
        this.xSize = 286;
        this.ySize = 182;
    }

    public static boolean listChanged() {
        return currScreenState.getLastType() == null || !currScreenState.getLastType().equals(currScreenState.getPageType());
    }

    @Override
    protected void init() {
        super.init();
        buttons.clear();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        int x = this.guiLeft;
        int y = this.guiTop;
        int subX = x + 112;
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
     * @param y top right corner y position
     */
    private void registerData(int subX, int y) {
        BeeRegistry.getRegistry().getBees().forEach((s, b) -> bees.put(s, new BeePage(this, b, s, subX, y)));
        TraitRegistry.getRegistry().getTraits().forEach((s, b) -> traits.put(s, new TraitPage(this, b, s, subX, y)));
        honey.put("honey", new HoneyPage(this, null, "honey", subX, y, true));
        honey.put("catnip", new HoneyPage(this, KittenBee.getHoneyBottleData(), "catnip", subX, y, false));
        HoneyRegistry.getRegistry().getHoneyBottles().forEach((s, h) -> honey.put(s, new HoneyPage(this, h, s, subX, y, false)));
    }

    /**
     * Registering of home row buttons. (Search button, Home button, Back button)
     *
     * @param subX x position of the sub window
     * @param x top right corner x position
     * @param y top right corner y position
     */
    private void registerButtons(int subX, int x, int y) {
        home = new HomePage(this, subX, y);
        homeButton = new ModImageButton(x + (xSize / 2) - 10, y + ySize - 25, 20, 20, 20, 0, 20, homeButtons, 60, 60, onPress -> selectPage(home));
        backButton = new ModImageButton(x + (xSize / 2) + 20, y + ySize - 25, 20, 20, 40, 0, 20, homeButtons, 60, 60, onPress -> {
            if (!pastStates.isEmpty()) {
                goBackState();
                returnState(true);
            }
        });
        addButton(homeButton);
        addButton(new ModImageButton(x + (xSize / 2) - 40, y + ySize - 25, 20, 20, 0, 0, 20, homeButtons, 60, 60, onPress -> {
            searchBox.visible = !searchBox.visible;
            setSearchVisible(searchBox.visible);
            updateSearch(beesList, true);
            updateSearch(traitsList, true);
            updateSearch(honeyList, true);
        }));
        backButton.active = false;
        addButton(backButton);
        bees.forEach((s, b) -> addButton(b.listButton));
        traits.forEach((s, b) -> addButton(b.listButton));
        honey.forEach((s, b) -> addButton(b.listButton));
    }

    /**
     * Register the beepedia search bar and register search parameters for each page.
     *
     * @param x top right corner x position
     * @param y top right corner y position
     */
    private void registerSearch(int x, int y) {
        searchBox = new EditBox(Minecraft.getInstance().font, x + 10, y + 143, 98, 10, new TranslatableComponent("gui.resourcefulbees.beepedia.search"));
        searchBox.visible = false;
        addWidget(searchBox);
        bees.forEach((s, b) -> b.addSearch());
        traits.forEach((s, b) -> b.addSearch());
        honey.forEach((s, b) -> b.addSearch());
    }

    /**
     * Register the page lists and the tab buttons to switch between them.
     *
     * @param x top right corner x position
     * @param y top right corner y position
     */
    public void registerTabs(int x, int y) {
        ItemStack beeItem = new ItemStack(Items.BEEHIVE);
        ItemStack traitItem = new ItemStack(Items.BLAZE_POWDER);
        ItemStack honeyItem = new ItemStack(Items.HONEY_BOTTLE);
        TabImageButton beesButton = new TabImageButton(x + 45, y + 8, 20, 20, 0, 0, 20, buttonImage, beeItem, 2, 2, onPress ->
                setActiveList(beesList, PageType.BEE), getTooltipProvider(new TranslatableComponent("gui.resourcefulbees.beepedia.tab.bees")));
        TabImageButton traitsButton = new TabImageButton(x + 66, y + 8, 20, 20, 0, 0, 20, buttonImage, traitItem, 2, 2, onPress ->
                setActiveList(traitsList, PageType.TRAIT), getTooltipProvider(new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits")));
        TabImageButton honeyButton = new TabImageButton(x + 87, y + 8, 20, 20, 0, 0, 20, buttonImage, honeyItem, 2, 2, onPress ->
                setActiveList(honeyList, PageType.HONEY), getTooltipProvider(new TranslatableComponent("gui.resourcefulbees.beepedia.tab.honey")));

        addButton(beesButton);
        addButton(traitsButton);
        addButton(honeyButton);
        beesList = new ButtonList(x + 8, y + 31, 100, 123, 21, beesButton, bees);
        traitsList = new ButtonList(x + 8, y + 31, 100, 123, 21, traitsButton, traits);
        honeyList = new ButtonList(x + 8, y + 31, 100, 123, 21, honeyButton, honey);
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
        }
        // collect page if page does not match active list type.
        // this can be inaccurate as some pages may share IDs
        if (page == null) page = bees.get(pageID);
        if (page == null) page = honey.get(pageID);
        if (page == null) page = traits.get(pageID);
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
        if (this.activePage != null) {
            if (!this.activePage.getClass().equals(activePage.getClass()) && !(this.activePage instanceof HomePage) && !goingBack) {
                saveScreenState();
            }
            this.activePage.closePage();
        }
        if (page == null) page = home;

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
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(matrixStack, 0);
        itemTooltips.clear();
        fluidTooltips.clear();
        interactions.clear();
        drawBackground(matrixStack, partialTick, mouseX, mouseY);
        updateSearch();
        super.render(matrixStack, mouseX, mouseY, partialTick);
        searchBox.render(matrixStack, mouseX, mouseY, partialTick);
        drawForeground(matrixStack, mouseX, mouseY);
        drawTooltips(matrixStack, mouseX, mouseY);
    }

    protected void drawBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {
        Minecraft client = this.minecraft;
        homeButton.active = activePage != home;
        backButton.active = !pastStates.isEmpty();
        if (client != null) {
            client.getTextureManager().bind(background);
            int x = this.guiLeft;
            int y = this.guiTop;
            blit(matrix, x, y, 0, 0, this.xSize, this.ySize, 286, 182);
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

    protected void drawForeground(PoseStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title, (float) this.guiLeft + 10, (float) this.guiTop + ySize - 20, 5592405);
        activePage.renderForeground(matrixStack, mouseX, mouseY);
        TranslatableComponent title;
        switch (activeListType) {
            case TRAIT:
                title = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits");
                break;
            case HONEY:
                title = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.honey");
                break;
            default:
                title = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.bees");
                break;
        }
        this.font.draw(matrixStack, title.withStyle(ChatFormatting.WHITE), (float) this.guiLeft + 10, (float) this.guiTop + 20, -1);
    }


    private void drawTooltips(PoseStack matrixStack, int mouseX, int mouseY) {
        activePage.drawTooltips(matrixStack, mouseX, mouseY);
        beesList.button.renderToolTip(matrixStack, mouseX, mouseY);
        traitsList.button.renderToolTip(matrixStack, mouseX, mouseY);
        honeyList.button.renderToolTip(matrixStack, mouseX, mouseY);
        itemTooltips.forEach(t -> t.draw(matrixStack, mouseX, mouseY));
        fluidTooltips.forEach(t -> t.draw(matrixStack, mouseX, mouseY));
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
        updateScrollPos(beesList, traitsList, honeyList);
        return super.mouseScrolled(mouseX, mouseY, scrollAmount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (activePage.mouseClicked(mouseX, mouseY, mouseButton)) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        interactions.forEach(b -> {
            if (b.onMouseClick((int) mouseX, (int) mouseY)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        });
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public static void updateScrollPos(ButtonList beesList, ButtonList traitsList, ButtonList honeyList) {
        beesScroll = beesList.getScrollPos();
        traitScroll = traitsList.getScrollPos();
        honeyScroll = honeyList.getScrollPos();
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

    public void drawSlot(PoseStack matrix, ItemLike item, int amount, int xPos, int yPos) {
        if (item instanceof LiquidBlock) {
            drawFluidSlot(matrix, new FluidStack(((LiquidBlock) item).getFluid().getSource(), amount), xPos, yPos, true);
        } else {
            drawSlot(matrix, new ItemStack(item, amount), xPos, yPos);
        }
    }

    public void drawSlot(PoseStack matrix, Block item, int xPos, int yPos) {
        drawSlot(matrix, item, 1, xPos, yPos);
    }

    public void drawSlot(PoseStack matrix, ItemLike item, int xPos, int yPos) {
        if (item instanceof LiquidBlock) {
            drawFluidSlot(matrix, new FluidStack(((LiquidBlock) item).getFluid().getSource(), 1000), xPos, yPos, false);
        } else {
            drawSlot(matrix, new ItemStack(item), xPos, yPos);
        }
    }

    public void drawFluidSlot(PoseStack matrix, FluidStack fluidStack, int xPos, int yPos) {
        drawFluidSlot(matrix, fluidStack, xPos, yPos, true);
    }


    public void drawFluidSlot(PoseStack matrix, FluidStack fluidStack, int xPos, int yPos, boolean showAmount) {
        if (fluidStack.isEmpty()) return;
        drawFluidSlotNoToolTip(matrix, fluidStack, xPos, yPos);
        registerFluidTooltip(fluidStack, xPos, yPos, showAmount);
    }

    public void drawFluidSlotNoToolTip(PoseStack matrix, FluidStack fluidStack, int xPos, int yPos) {
        if (fluidStack.isEmpty()) return;
        getMinecraft().getTextureManager().bind(slotImage);
        blit(matrix, xPos, yPos, 0, 0, 20, 20, 20, 20);
        RenderUtils.renderFluid(matrix, fluidStack, xPos + 2, yPos + 2, this.getBlitOffset());
    }

    public void drawSlot(PoseStack matrix, ItemStack item, int xPos, int yPos) {
        drawSlotNoToolTip(matrix, item, xPos, yPos);
        registerItemTooltip(item, xPos, yPos);
    }

    public void drawSlotNoToolTip(PoseStack matrix, ItemLike item, int xPos, int yPos) {
        if (item instanceof LiquidBlock) {
            drawFluidSlotNoToolTip(matrix, new FluidStack(((LiquidBlock) item).getFluid().getSource(), 1000), xPos, yPos);
        } else {
            drawSlotNoToolTip(matrix, new ItemStack(item), xPos, yPos);
        }
    }

    public void drawSlotNoToolTip(PoseStack matrix, ItemStack item, int xPos, int yPos) {
        if (item.isEmpty()) return;
        getMinecraft().getTextureManager().bind(slotImage);
        blit(matrix, xPos, yPos, 0, 0, 20, 20, 20, 20);
        getMinecraft().getItemRenderer().renderGuiItem(item, xPos + 2, yPos + 2);
        getMinecraft().getItemRenderer().renderGuiItemDecorations(font, item, xPos + 2, yPos + 2);
    }

    public void drawEmptySlot(PoseStack matrix, int xPos, int yPos) {
        getMinecraft().getTextureManager().bind(slotImage);
        blit(matrix, xPos, yPos, 0, 0, 20, 20, 20, 20);
    }

    private void renderFluidTooltip(PoseStack matrix, FluidStack fluidStack, int mouseX, int mouseY, boolean showAmount) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(fluidStack.getDisplayName());
        if (showAmount) {
            DecimalFormat decimalFormat = new DecimalFormat("##0.0");
            String amount = fluidStack.getAmount() < 500 || BeeInfoUtils.isShiftPressed() ? String.format("%,d", fluidStack.getAmount()) + " mb" : decimalFormat.format((float) fluidStack.getAmount() / 1000) + " B";
            tooltip.add(new TextComponent(amount));
        }
        tooltip.add(new TextComponent(fluidStack.getFluid().getRegistryName().toString()).withStyle(ChatFormatting.DARK_GRAY));
        renderComponentTooltip(matrix, tooltip, mouseX, mouseY);
    }

    private void registerItemTooltip(ItemStack item, int xPos, int yPos) {
        itemTooltips.add(new ItemTooltip(item, xPos, yPos));
    }

    private void registerFluidTooltip(FluidStack fluid, int xPos, int yPos, boolean drawAmount) {
        fluidTooltips.add(new FluidTooltip(fluid, xPos, yPos, drawAmount));
    }

    private void registerInteraction(int xPos, int yPos, Supplier<Boolean> supplier) {
        interactions.add(new Interaction(xPos, yPos, supplier));
    }

    @Override
    public <T extends AbstractWidget> @NotNull T addButton(@NotNull T widget) {
        return super.addButton(widget);
    }

    public Entity initEntity(ResourceLocation entityTypeRegistryID) {
        EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(entityTypeRegistryID);
        if (entityType == null) return null;
        return entityType.create(getMinecraft().level);
    }

    public Button.OnTooltip getTooltipProvider(Component textComponent) {
        return (button, matrix, mouseX, mouseY) -> renderTooltip(matrix, textComponent, mouseX, mouseY);
    }


    public void drawInteractiveFluidSlot(PoseStack matrix, FluidStack fluidStack, int xPos, int yPos, int mouseX, int mouseY, Supplier<Boolean> supplier) {
        drawInteractiveFluidSlot(matrix, fluidStack, xPos, yPos, mouseX, mouseY, true, supplier);
    }

    public void drawInteractiveFluidSlot(PoseStack matrix, FluidStack fluidStack, int xPos, int yPos, int mouseX, int mouseY, boolean showAmount, Supplier<Boolean> supplier) {
        if (fluidStack.isEmpty()) return;
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

    public void drawInteractiveSlot(PoseStack matrix, ItemStack item, int xPos, int yPos, int mouseX, int mouseY, Supplier<Boolean> supplier) {
        if (item.isEmpty()) return;
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
        return beeBottle == bottleData.getItem();
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
        TRAIT
    }

    private class ItemTooltip {
        private final ItemStack item;
        private final int xPos;
        private final int yPos;

        public ItemTooltip(ItemStack item, int xPos, int yPos) {
            this.item = item;
            this.xPos = xPos;
            this.yPos = yPos;
        }

        public void draw(PoseStack matrix, int mouseX, int mouseY) {
            if (mouseX >= xPos && mouseY >= yPos && mouseX <= xPos + 20 && mouseY <= yPos + 20) {
                renderTooltip(matrix, item, mouseX, mouseY);
            }
        }
    }

    private class FluidTooltip {
        private final FluidStack fluid;
        private final int xPos;
        private final int yPos;
        private final boolean showAmount;

        public FluidTooltip(FluidStack fluid, int xPos, int yPos, boolean showAmount) {
            this.fluid = fluid;
            this.xPos = xPos;
            this.yPos = yPos;
            this.showAmount = showAmount;
        }

        public void draw(PoseStack matrix, int mouseX, int mouseY) {
            if (mouseX >= xPos && mouseY >= yPos && mouseX <= xPos + 20 && mouseY <= yPos + 20) {
                renderFluidTooltip(matrix, fluid, mouseX, mouseY, showAmount);
            }
        }
    }

    private static class Interaction {
        final int xPos;
        final int yPos;
        final Supplier<Boolean> supplier;

        public Interaction(int xPos, int yPos, Supplier<Boolean> supplier) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.supplier = supplier;
        }

        public boolean onMouseClick(int mouseX, int mouseY) {
            if (mouseX >= xPos && mouseY >= yPos && mouseX <= xPos + 20 && mouseY <= yPos + 20) {
                return supplier.get();
            }
            return false;
        }
    }
}
