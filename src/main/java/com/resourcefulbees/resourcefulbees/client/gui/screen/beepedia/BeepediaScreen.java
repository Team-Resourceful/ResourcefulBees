package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.BeePage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.HomePage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.HoneyPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.TraitPage;
import com.resourcefulbees.resourcefulbees.client.gui.widget.ButtonList;
import com.resourcefulbees.resourcefulbees.client.gui.widget.ImageButton;
import com.resourcefulbees.resourcefulbees.client.gui.widget.TabImageButton;
import com.resourcefulbees.resourcefulbees.entity.passive.KittenBee;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.TraitRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;

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

    protected Map<String, BeePage> bees = new TreeMap<>();
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
    private ResourceLocation home_buttons = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/home_buttons.png");
    private List<ItemTooltip> itemTooltips;
    private List<FluidTooltip> fluidTooltips;

    public BeepediaScreen(ITextComponent name, String pageID) {
        super(name);
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
        honey.put("honey", new HoneyPage(this, BeeConstants.defaultHoney, "honey", subX, y));
        honey.put("catnip", new HoneyPage(this, KittenBee.getHoneyBottleData(), "catnip", subX, y));
        TraitRegistry.getRegistry().getTraits().forEach((s, b) -> traits.put(s, new TraitPage(this, b, s, subX, y)));
        BeeRegistry.getRegistry().getBees().forEach((s, b) -> bees.put(s, new BeePage(this, b, s, subX, y)));
        BeeRegistry.getRegistry().getHoneyBottles().forEach((s, h) -> honey.put(s, new HoneyPage(this, h, s, subX, y)));
        home = new HomePage(this, subX, y);
        addButton(new ImageButton(x + (xSize / 2) - 10, y + ySize - 25, 20, 20, 20, 0, 20, home_buttons, 60, 60, onPress -> selectPage(home)));
        backButton = new ImageButton(x + (xSize / 2) + 20, y + ySize - 25, 20, 20, 40, 0, 20, home_buttons, 60, 60, onPress -> {
            if (!pastStates.isEmpty()) {
                goBackState();
                returnState(true);
            }
        });
        addButton(new ImageButton(x + (xSize / 2) - 40, y + ySize - 25, 20, 20, 0, 0, 20, home_buttons, 60, 60, onPress -> {
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
        searchBox = new TextFieldWidget(Minecraft.getInstance().fontRenderer, x + 10, y + 143, 98, 10, new TranslationTextComponent("gui.resourcefulbees.beepedia.search"));
        searchBox.visible = false;
        addChild(searchBox);
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
        switch (pageType) {
            case BEE:
                activatePage(pageType, bees.get(pageID), beesList, goingBack);
                break;
            case HONEY:
                activatePage(pageType, honey.get(pageID), honeyList, goingBack);
                break;
            case TRAIT:
                activatePage(pageType, traits.get(pageID), traitsList, goingBack);
                break;
        }
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
        // close active page and reset screen state
        if (this.activePage != null) {
            if (!this.activePage.getClass().equals(activePage.getClass()) && !(this.activePage instanceof HomePage) && !goingBack) {
                resetScreenState();
            }
            this.activePage.closePage();
        }

        // set current state
        currScreenState.setPageType(type);
        currScreenState.setPageID(page.id);

        // update list
        if (currScreenState.pageChanged()) {
            if (this.activeList != null) this.activeList.setActive(false);
            this.activeList = list;
            this.activeList.setActive(true);
            this.activeListType = type;
        }
        // open page
        this.activePage = page;
        this.activePage.openPage();
    }

    public static void resetScreenState() {
        pastStates.push(currScreenState);
        currScreenState = new BeepediaScreenState();
    }

    private void setActiveList(ButtonList buttonList, PageType type) {
        currScreenState.setPageType(type);
        activeListType = type;
        if (this.activeList != null) this.activeList.setActive(false);
        this.activeList = buttonList;
        this.activeList.setActive(true);
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
        beesList = new ButtonList(x + 8, y + 31, 123, 100, 21, beesButton, bees);
        traitsList = new ButtonList(x + 8, y + 31, 123, 100, 21, traitsButton, traits);
        honeyList = new ButtonList(x + 8, y + 31, 123, 100, 21, honeyButton, honey);
        setActiveList(beesList, PageType.BEE);
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
        itemTooltips = new LinkedList<>();
        fluidTooltips = new LinkedList<>();
        drawBackground(matrixStack, partialTick, mouseX, mouseY);
        updateSearch();
        super.render(matrixStack, mouseX, mouseY, partialTick);
        searchBox.render(matrixStack, mouseX, mouseY, partialTick);
        drawForeground(matrixStack, mouseX, mouseY);
        drawTooltips(matrixStack, mouseX, mouseY);
    }

    protected void drawBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        Minecraft client = this.client;
        backButton.active = !pastStates.isEmpty();
        if (client != null) {
            client.getTextureManager().bindTexture(background);
            int x = this.guiLeft;
            int y = this.guiTop;
            drawTexture(matrix, x, y, 0, 0, this.xSize, this.ySize, 286, 182);
        }
        activePage.renderBackground(matrix, partialTick, mouseX, mouseY);
        activeList.updateList();
        if (searchBox.visible) activeList.setSearchHeight();
        else activeList.resetHeight();
    }

    private void updateSearch() {
        if (searchBox.visible) {
            setFocused(searchBox);
            searchBox.setFocused2(true);
        }
        if (searchBox.isFocused()) {
            String text = searchBox.getText();
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
                searchBox.setText(getSearch());
                list.updateReducedList(searchBox.getText());
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
        this.textRenderer.draw(matrixStack, this.title, (float) this.guiLeft + 10, (float) this.guiTop + ySize - 20, 5592405);
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
        this.textRenderer.draw(matrixStack, title, (float) this.guiLeft + 10, (float) this.guiTop + 20, 0xffffff);
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
            Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        boolean toReturn = super.mouseClicked(mouseX, mouseY, mouseButton);
        return toReturn;
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
            drawFluidSlot(matrix, new FluidStack(((FlowingFluidBlock) item).getFluid().getStillFluid(), amount), xPos, yPos, true);
        } else {
            drawSlot(matrix, new ItemStack(item, amount), xPos, yPos);
        }
    }

    public void drawSlot(MatrixStack matrix, IItemProvider item, int xPos, int yPos) {
        if (item instanceof FlowingFluidBlock) {
            drawFluidSlot(matrix, new FluidStack(((FlowingFluidBlock) item).getFluid().getStillFluid(), 1000), xPos, yPos, false);
        } else {
            drawSlot(matrix, new ItemStack(item), xPos, yPos);
        }
    }

    private void drawFluidSlot(MatrixStack matrix, FluidStack fluidStack, int xPos, int yPos, boolean showAmount) {
        getMinecraft().getTextureManager().bindTexture(slotImage);
        drawTexture(matrix, xPos, yPos, 0, 0, 20, 20, 20, 20);
        RenderUtils.renderFluid(matrix, fluidStack, xPos + 2, yPos + 2, this.getZOffset());
        registerFluidTooltip(fluidStack, xPos, yPos, showAmount);
    }

    public void drawSlot(MatrixStack matrix, ItemStack item, int xPos, int yPos) {
        getMinecraft().getTextureManager().bindTexture(slotImage);
        drawTexture(matrix, xPos, yPos, 0, 0, 20, 20, 20, 20);
        getMinecraft().getItemRenderer().renderItemIntoGUI(item, xPos + 2, yPos + 2);
        getMinecraft().getItemRenderer().renderItemOverlays(client.fontRenderer, item, xPos + 2, yPos + 2);
        registerItemTooltip(item, xPos, yPos);
    }

    private void renderFluidTooltip(MatrixStack matrix, FluidStack fluidStack, int mouseX, int mouseY, boolean showAmount) {
        List<ITextComponent> tooltip = new ArrayList<>();
        tooltip.add(fluidStack.getDisplayName());
        if (showAmount) {
            DecimalFormat decimalFormat = new DecimalFormat("##0.0");
            String amount = fluidStack.getAmount() < 500 || BeeInfoUtils.isShiftPressed() ? decimalFormat.format(fluidStack.getAmount()) + " mb" : decimalFormat.format((float) fluidStack.getAmount() / 1000) + " B";
            tooltip.add(new StringTextComponent(amount));
        }
        tooltip.add(new StringTextComponent(fluidStack.getFluid().getRegistryName().toString()).formatted(TextFormatting.DARK_GRAY));
        renderTooltip(matrix, tooltip, mouseX, mouseY);
    }

    private void registerItemTooltip(ItemStack item, int xPos, int yPos) {
        itemTooltips.add(new ItemTooltip(item, xPos, yPos));
    }

    private void registerFluidTooltip(FluidStack fluid, int xPos, int yPos, boolean drawAmount) {
        fluidTooltips.add(new FluidTooltip(fluid, xPos, yPos, drawAmount));
    }

    @Override
    public <T extends Widget> @NotNull T addButton(@NotNull T widget) {
        return super.addButton(widget);
    }

    public Entity initEntity(ResourceLocation entityTypeRegistryID) {
        EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(entityTypeRegistryID);
        if (entityType == null) return null;
        return entityType.create(getMinecraft().world);
    }

    public Button.ITooltip getTooltipProvider(ITextComponent textComponent) {
        return (button, matrix, mouseX, mouseY) -> renderTooltip(matrix, textComponent, mouseX, mouseY);
    }

    public enum PageType {
        // main pages
        BEE,
        HONEY,
        TRAIT,
        HOME;
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
}
