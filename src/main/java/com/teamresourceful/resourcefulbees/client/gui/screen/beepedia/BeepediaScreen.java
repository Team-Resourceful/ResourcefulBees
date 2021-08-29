package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.capabilities.BeepediaData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.*;
import com.teamresourceful.resourcefulbees.client.gui.widget.*;
import com.teamresourceful.resourcefulbees.config.Config;
import com.teamresourceful.resourcefulbees.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.network.packets.BeepediaEntityMessage;
import com.teamresourceful.resourcefulbees.utils.RenderUtils;
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
import net.minecraft.util.IItemProvider;
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

    private boolean searchVisible = false;
    private String search = null;
    private String lastSearch = null;

    public final boolean isCreative;
    public final boolean hasShades;
    public final BeepediaData data;

    TextFieldWidget searchBox;
    public int guiLeft;
    public int guiTop;
    protected int ticksOpen = 0;

    public static final int SCREEN_WIDTH = 328;
    public static final int SCREEN_HEIGHT = 200;

    private final List<ToolTip> tooltips = new LinkedList<>();
    private final List<Interaction> interactions = new LinkedList<>();
    private Button backButton;
    private ModImageButton homeButton;
    private ModImageButton searchButton;

    @OnlyIn(Dist.CLIENT)
    public BeepediaScreen(boolean isCreative, boolean hasShades, BeepediaData data) {
        super(BeepediaLang.INTERFACE_NAME);
        BeepediaHandler.initBeepediaStates();
        this.hasShades = hasShades;
        this.isCreative = isCreative;
        this.data = data;
    }

    public static void receiveBeeMessage(BeepediaEntityMessage message) {
        BeepediaHandler.collectBee(message);
    }

    @Override
    protected void init() {
        super.init();
        buttons.clear();
        this.guiLeft = (this.width - SCREEN_WIDTH) / 2;
        this.guiTop = (this.height - SCREEN_HEIGHT) / 2;
        registerScreen();
        BeepediaHandler.registerScreen(this);
    }


    private void registerScreen() {
        int x = this.guiLeft;
        int y = this.guiTop;

        registerButtons(x, y);
    }

    /**
     * Define home row buttons, shadesButton and captured bee button
     */
    private void registerButtons(int x, int y) {
        int shadesButtonX = x + SCREEN_WIDTH + 2;
        int shadesButtonY = y + SCREEN_HEIGHT - 32;

        ItemStack book = PatchouliAPI.get().getBookStack(ModConstants.SHADES_OF_BEES);

        homeButton = new ModImageButton(x + (SCREEN_WIDTH / 2) - 10, y + SCREEN_HEIGHT - 25, 20, 20, 20, 0, 20, BeepediaImages.HOME_BUTTONS, 60, 60, onPress -> BeepediaHandler.openHomeScreen());
        backButton = new ModImageButton(x + (SCREEN_WIDTH / 2) + 20, y + SCREEN_HEIGHT - 25, 20, 20, 40, 0, 20, BeepediaImages.HOME_BUTTONS, 60, 60, onPress -> BeepediaHandler.goBackState());
        searchButton = new ModImageButton(x + (SCREEN_WIDTH / 2) - 40, y + SCREEN_HEIGHT - 25, 20, 20, 0, 0, 20, BeepediaImages.HOME_BUTTONS, 60, 60, onPress -> {
            searchBox.visible = !searchBox.visible;
            setSearchVisible(searchBox.visible);
            updateSearch(beesList, true);
            updateSearch(traitsList, true);
            updateSearch(honeyList, true);
            updateSearch(combsList, true);
        });
        TabImageButton shadesButton = new TabImageButton(shadesButtonX + 6, shadesButtonY + 6, 18, 18, 0, 0, 18,
                BeepediaImages.SHADES_BUTTON_IMAGE, book, 1, 1, onPress -> PatchouliAPI.get().openBookGUI(ModConstants.SHADES_OF_BEES),
                18, 36) {

            @Override
            public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
                TranslationTextComponent s = new TranslationTextComponent("book.resourcefulbees.name");
                BeepediaScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        };
        // add buttons to button array
        addButtons(homeButton, backButton, searchButton, shadesButton);
        backButton.active = false;
        shadesButton.visible = hasShades;
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
        int shadesButtonX = guiLeft + SCREEN_WIDTH + 2;
        int shadesButtonY = guiTop + SCREEN_HEIGHT - 32;
        Minecraft.getInstance().getTextureManager().bind(BeepediaImages.SHADES_BACKGROUND);
        blit(matrix, shadesButtonX, shadesButtonY, 0, 0, 30, 30, 30, 30);
    }

    private void drawEntityData(@NotNull MatrixStack matrix) {
        int x = this.guiLeft;
        int y = this.guiTop;

        int boxX = x + SCREEN_WIDTH + 12;
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
            client.getTextureManager().bind(BeepediaImages.BACKGROUND);
            blit(matrix, x, y, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT);
        }
        if (entity != null) {
            int boxX = x + SCREEN_WIDTH + 2;
            Minecraft.getInstance().getTextureManager().bind(BeepediaImages.BEE_INFO_IMAGE);
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
        this.font.draw(matrixStack, this.title, (float) this.guiLeft + 10, (float) this.guiTop + SCREEN_HEIGHT - 20, 5592405);
        activePage.renderForeground(matrixStack, mouseX, mouseY);
        TranslationTextComponent title;
        switch (activeListType) {
            case TRAIT:
                title = BeepediaLang.TAB_BEES;
                break;
            case HONEY:
                title = BeepediaLang.TAB_TRAITS;
                break;
            case COMB:
                title = BeepediaLang.TAB_HONEY;
                break;
            default:
                title = BeepediaLang.TAB_COMBS;
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
        getMinecraft().getTextureManager().bind(BeepediaImages.SLOT_IMAGE);
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
        getMinecraft().getTextureManager().bind(BeepediaImages.SLOT_IMAGE);
        blit(matrix, xPos, yPos, 0, 0, 20, 20, 20, 20);
        getMinecraft().getItemRenderer().renderGuiItem(item, xPos + 2, yPos + 2);
        getMinecraft().getItemRenderer().renderGuiItemDecorations(font, item, xPos + 2, yPos + 2);
    }

    public void drawEmptySlot(MatrixStack matrix, int xPos, int yPos) {
        getMinecraft().getTextureManager().bind(BeepediaImages.SLOT_IMAGE);
        blit(matrix, xPos, yPos, 0, 0, 20, 20, 20, 20);
    }

    public void registerItemTooltip(ItemStack item, int xPos, int yPos) {
        tooltips.add(new ToolTip(xPos + 2, yPos + 2, 16, 16, item));
    }

    public void registerFluidTooltip(FluidStack fluid, int xPos, int yPos, boolean showAmount) {
        tooltips.add(new ToolTip(xPos + 2, yPos + 2, 16, 16, fluid, showAmount));
    }

    public void registerTooltip(ITextComponent textComponent, int xPos, int yPos, int width, int height) {
        tooltips.add(new ToolTip(xPos, yPos, width, height, () -> textComponent));
    }

    public void registerTooltip(Supplier<ITextComponent> textComponent, int xPos, int yPos, int width, int height) {
        tooltips.add(new ToolTip(xPos, yPos, width, height, textComponent));
    }

    public void registerInteraction(int xPos, int yPos, Supplier<Boolean> supplier) {
        interactions.add(new Interaction(xPos + 2, yPos + 2, 16, 16, supplier));
    }

    @Override
    public <T extends Widget> @NotNull T addButton(@NotNull T widget) {
        return super.addButton(widget);
    }

    public <T extends Widget> void addButtons(List<@NotNull T> widgets) {
        widgets.forEach(super::addButton);
    }

    public <T extends Widget> void addButtons(@NotNull T... widgets) {
        addButtons(Arrays.asList(widgets));
    }

    public static <T extends Widget> void setButtonsVisibility(boolean visible, List<@NotNull T> widgets) {
        widgets.forEach(w -> w.visible = visible);
    }

    public static <T extends Widget> void setButtonsActive(boolean active, List<@NotNull T> widgets) {
        widgets.forEach(w -> w.active = active);
    }

    public static <T extends Widget> void setButtonsVisibility(boolean b, @NotNull T... widgets) {
        setButtonsVisibility(b, Arrays.asList(widgets));
    }

    public static <T extends Widget> void setButtonsActive(boolean b, @NotNull T... widgets) {
        setButtonsActive(b, Arrays.asList(widgets));
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

        getMinecraft().getTextureManager().bind(BeepediaImages.BUTTON_IMAGE);
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
        getMinecraft().getTextureManager().bind(BeepediaImages.BUTTON_IMAGE);
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
}
