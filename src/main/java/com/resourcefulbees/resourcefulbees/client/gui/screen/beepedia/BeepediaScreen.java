package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.BeePage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.HomePage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.HoneyPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.TraitPage;
import com.resourcefulbees.resourcefulbees.client.gui.widget.ButtonList;
import com.resourcefulbees.resourcefulbees.client.gui.widget.ModImageButton;
import com.resourcefulbees.resourcefulbees.client.gui.widget.TabImageButton;
import com.resourcefulbees.resourcefulbees.entity.passive.KittenBee;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.TraitRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
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
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Supplier;


public class BeepediaScreen extends Screen {

    private static int beesScroll = 0;
    private static int honeyScroll = 0;
    private static int traitScroll = 0;
    private static boolean searchVisible = false;
    private static String search = null;
    private static String lastSearch = null;

    protected static final LinkedList<BeepediaScreenState> pastStates = new LinkedList<>();
    public static BeepediaScreenState currScreenState = new BeepediaScreenState();

    TextFieldWidget searchBox;

    protected int xSize;
    protected int ySize;
    protected int guiLeft;
    protected int guiTop;
    protected int ticksOpen = 0;

    public Map<String, BeePage> bees = new TreeMap<>();
    protected Map<String, TraitPage> traits = new TreeMap<>();
    protected Map<String, HoneyPage> honey = new TreeMap<>();

    ButtonList beesList;
    ButtonList traitsList;
    ButtonList honeyList;

    BeepediaPage home;
    BeepediaPage activePage;
    PageType activeListType = PageType.BEE;
    ButtonList activeList = null;

    ResourceLocation background = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/screen.png");
    ResourceLocation buttonImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/button.png");
    ResourceLocation slotImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/slot.png");
    private Button backButton;
    private ResourceLocation homeButtons = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/home_buttons.png");
    private List<ItemTooltip> itemTooltips = new LinkedList<>();
    private List<FluidTooltip> fluidTooltips = new LinkedList<>();
    private List<Interaction> interactions = new LinkedList<>();
    private ModImageButton homeButton;

    @OnlyIn(Dist.CLIENT)
    public BeepediaScreen(String pageID) {
        super(new TranslationTextComponent("gui.resourcefulbees.beepedia"));
        if (pageID != null) {
            currScreenState.setPageType(PageType.BEE);
            currScreenState.setPageID(pageID);
            currScreenState.setBeeSubPage(BeePage.SubPageType.INFO);
        }
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
        BeeRegistry.getRegistry().getBees().forEach((s, b) -> bees.put(s, new BeePage(this, b, s, subX, y)));
        TraitRegistry.getRegistry().getTraits().forEach((s, b) -> traits.put(s, new TraitPage(this, b, s, subX, y)));
        honey.put("honey", new HoneyPage(this, BeeConstants.defaultHoney, "honey", subX, y));
        honey.put("catnip", new HoneyPage(this, KittenBee.getHoneyBottleData(), "catnip", subX, y));
        BeeRegistry.getRegistry().getHoneyBottles().forEach((s, h) -> honey.put(s, new HoneyPage(this, h, s, subX, y)));
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
        searchBox = new TextFieldWidget(Minecraft.getInstance().font, x + 10, y + 143, 98, 10, new TranslationTextComponent("gui.resourcefulbees.beepedia.search"));
        searchBox.visible = false;
        addWidget(searchBox);
        initSidebar();
        returnState(false);
    }


    private static void goBackState() {
        currScreenState = pastStates.pop();
    }

    private void returnState(boolean goingBack) {
        setActive(currScreenState.getPageType(), currScreenState.getPageID(), goingBack);
        searchBox.visible = isSearchVisible();
        if (beesScroll != 0) beesList.setScrollPos(beesScroll);
        if (honeyScroll != 0) honeyList.setScrollPos(honeyScroll);
        if (traitScroll != 0) traitsList.setScrollPos(traitScroll);
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
                this.activeList.updateReducedList(BeepediaScreen.getSearch());
        }
        // open page
        this.activePage = page;
        this.activePage.openPage();
    }

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

    public void initSidebar() {
        ItemStack beeItem = new ItemStack(Items.BEEHIVE);
        ItemStack traitItem = new ItemStack(Items.BLAZE_POWDER);
        ItemStack honeyItem = new ItemStack(Items.HONEY_BOTTLE);
        int x = this.guiLeft;
        int y = this.guiTop;
        TabImageButton beesButton = new TabImageButton(x + 45, y + 8, 20, 20, 0, 0, 20, buttonImage, beeItem, 2, 2, onPress -> {
            setActiveList(beesList, PageType.BEE);
        }, getTooltipProvider(new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.bees")));
        TabImageButton traitsButton = new TabImageButton(x + 66, y + 8, 20, 20, 0, 0, 20, buttonImage, traitItem, 2, 2, onPress -> {
            setActiveList(traitsList, PageType.TRAIT);
        }, getTooltipProvider(new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits")));
        TabImageButton honeyButton = new TabImageButton(x + 87, y + 8, 20, 20, 0, 0, 20, buttonImage, honeyItem, 2, 2, onPress -> {
            setActiveList(honeyList, PageType.HONEY);
        }, getTooltipProvider(new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.honey")));
        addButton(beesButton);
        addButton(traitsButton);
        addButton(honeyButton);
        beesList = new ButtonList(x + 8, y + 31, 100, 123, 21, beesButton, bees);
        traitsList = new ButtonList(x + 8, y + 31, 100, 123, 21, traitsButton, traits);
        honeyList = new ButtonList(x + 8, y + 31, 100, 123, 21, honeyButton, honey);
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
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

    protected void drawBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
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
            if (text == null || text.isEmpty()) {
                setSearch(null);
            } else {
                setSearch(text);
            }
        }
        updateSearch(beesList, false);
        updateSearch(honeyList, false);
        updateSearch(traitsList, false);
    }

    private void updateSearch(ButtonList list, boolean isToggled) {
        if (!searchUpdated() && !isToggled) return;
        if (isSearchVisible()) {
            if (getSearch() != null) {
                searchBox.setValue(getSearch());
                list.updateReducedList(searchBox.getValue());
            } else {
                list.updateReducedList(null);
            }
        } else {
            list.updateReducedList(null);
        }
    }

    private static boolean searchUpdated() {
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
        this.font.draw(matrixStack, this.title, (float) this.guiLeft + 10, (float) this.guiTop + ySize - 20, 5592405);
        activePage.renderForeground(matrixStack, mouseX, mouseY);
        TranslationTextComponent title;
        switch (activeListType) {
            case TRAIT:
                title = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits");
                break;
            case HONEY:
                title = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.honey");
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
        itemTooltips.forEach(t -> t.draw(matrixStack, mouseX, mouseY));
        fluidTooltips.forEach(t -> t.draw(matrixStack, mouseX, mouseY));
    }

    public Map<String, TraitPage> getTraits(CustomBeeData beeData) {
        Map<String, TraitPage> pages = new HashMap<>();
        if (beeData.getTraitNames() == null || beeData.getTraitNames().length == 0) return pages;
        for (String traitName : beeData.getTraitNames()) {
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

    public static void updateScrollPos(ButtonList beesList, ButtonList traitsList, ButtonList honeyList) {
        beesScroll = beesList.scrollPos;
        traitScroll = traitsList.scrollPos;
        honeyScroll = honeyList.scrollPos;
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
        if (item instanceof FlowingFluidBlock) {
            drawFluidSlot(matrix, new FluidStack(((FlowingFluidBlock) item).getFluid().getSource(), 1000), xPos, yPos, false);
        } else {
            drawSlot(matrix, new ItemStack(item), xPos, yPos);
        }
    }

    public void drawSlot(MatrixStack matrix, IItemProvider item, int xPos, int yPos) {
        if (item instanceof FlowingFluidBlock) {
            drawFluidSlot(matrix, new FluidStack(((FlowingFluidBlock) item).getFluid().getSource(), 1000), xPos, yPos, false);
        } else {
            drawSlot(matrix, new ItemStack(item), xPos, yPos);
        }
    }

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
        getMinecraft().getItemRenderer().renderGuiItemDecorations(minecraft.font, item, xPos + 2, yPos + 2);
    }

    public void drawEmptySlot(MatrixStack matrix, int xPos, int yPos) {
        getMinecraft().getTextureManager().bind(slotImage);
        blit(matrix, xPos, yPos, 0, 0, 20, 20, 20, 20);
    }

    private void renderFluidTooltip(MatrixStack matrix, FluidStack fluidStack, int mouseX, int mouseY, boolean showAmount) {
        List<ITextComponent> tooltip = new ArrayList<>();
        tooltip.add(fluidStack.getDisplayName());
        if (showAmount) {
            DecimalFormat decimalFormat = new DecimalFormat("##0.0");
            String amount = fluidStack.getAmount() < 500 || BeeInfoUtils.isShiftPressed() ? String.format("%,d", fluidStack.getAmount()) + " mb" : decimalFormat.format((float) fluidStack.getAmount() / 1000) + " B";
            tooltip.add(new StringTextComponent(amount));
        }
        tooltip.add(new StringTextComponent(fluidStack.getFluid().getRegistryName().toString()).withStyle(TextFormatting.DARK_GRAY));
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
    public <T extends Widget> @NotNull T addButton(@NotNull T widget) {
        return super.addButton(widget);
    }

    public Entity initEntity(ResourceLocation entityTypeRegistryID) {
        EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(entityTypeRegistryID);
        if (entityType == null) return null;
        return entityType.create(getMinecraft().level);
    }

    public Button.ITooltip getTooltipProvider(ITextComponent textComponent) {
        return (button, matrix, mouseX, mouseY) -> renderTooltip(matrix, textComponent, mouseX, mouseY);
    }


    public void drawInteractiveFluidSlot(MatrixStack matrix, FluidStack fluidStack, int xPos, int yPos, int mouseX, int mouseY, Supplier<Boolean> supplier) {
        drawInteractiveFluidSlot(matrix, fluidStack, xPos, yPos, mouseX, mouseY, true, supplier);
    }

    public void drawInteractiveFluidSlot(MatrixStack matrix, FluidStack fluidStack, int xPos, int yPos, int mouseX, int mouseY, boolean showAmount, Supplier<Boolean> supplier) {
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

    public void drawInteractiveSlot(MatrixStack matrix, ItemStack item, int xPos, int yPos, int mouseX, int mouseY, Supplier<Boolean> supplier) {
        if (item.isEmpty()) return;
        registerInteraction(xPos, yPos, supplier);
        getMinecraft().getTextureManager().bind(buttonImage);
        if (mouseHovering(xPos, yPos, 20, 20, mouseX, mouseY)) {
            blit(matrix, xPos, yPos, 0, 20, 20, 20, 20, 60);
        } else {
            blit(matrix, xPos, yPos, 0, 0, 20, 20, 20, 60);
        }
        getMinecraft().getItemRenderer().renderGuiItem(item, xPos + 2, yPos + 2);
        getMinecraft().getItemRenderer().renderGuiItemDecorations(minecraft.font, item, xPos + 2, yPos + 2);
        registerItemTooltip(item, xPos, yPos);
    }

    public Map<String, BeePage> getBees(ItemStack bottleData) {
        Map<String, BeePage> list = new HashMap<>();
        bees.forEach((s, b) -> {
            Item beeBottle = BeeInfoUtils.getItem(b.beeData.getCentrifugeData().getBottleOutput());
            if (beeBottle == bottleData.getItem()) {
                list.put(s, b);
            }
        });
        return list;
    }

    public Map<String, BeePage> getBees(String traitName) {
        Map<String, BeePage> list = new HashMap<>();
        bees.forEach((s, b) -> {
            if (b.beeData.hasTraitNames()) {
                List<String> traits = new ArrayList<>(Arrays.asList(b.beeData.getTraitNames()));
                if (traits.contains(traitName)) {
                    list.put(s, b);
                }
            }
        });
        return list;
    }


    public enum PageType {
        // main pages
        BEE,
        HONEY,
        TRAIT;
    }

    private class ItemTooltip {
        private ItemStack item;
        private int xPos;
        private int yPos;

        public ItemTooltip(ItemStack item, int xPos, int yPos) {
            this.item = item;
            this.xPos = xPos;
            this.yPos = yPos;
        }

        public void draw(MatrixStack matrix, int mouseX, int mouseY) {
            if (mouseX >= xPos && mouseY >= yPos && mouseX <= xPos + 20 && mouseY <= yPos + 20) {
                renderTooltip(matrix, item, mouseX, mouseY);
            }
        }
    }

    private class FluidTooltip {
        private FluidStack fluid;
        private int xPos;
        private int yPos;
        private boolean showAmount;

        public FluidTooltip(FluidStack fluid, int xPos, int yPos, boolean showAmount) {
            this.fluid = fluid;
            this.xPos = xPos;
            this.yPos = yPos;
            this.showAmount = showAmount;
        }

        public void draw(MatrixStack matrix, int mouseX, int mouseY) {
            if (mouseX >= xPos && mouseY >= yPos && mouseX <= xPos + 20 && mouseY <= yPos + 20) {
                renderFluidTooltip(matrix, fluid, mouseX, mouseY, showAmount);
            }
        }
    }

    private class Interaction {
        int xPos;
        int yPos;
        Supplier<Boolean> supplier;

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
