package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.BeePage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.HomePage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.HoneyPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.TraitPage;
import com.resourcefulbees.resourcefulbees.client.gui.widget.TabImageButton;
import com.resourcefulbees.resourcefulbees.container.BeepediaContainer;
import com.resourcefulbees.resourcefulbees.entity.passive.KittenBee;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.TraitRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BeepediaScreen extends ContainerScreen<BeepediaContainer> {

    public Map<String, BeePage> bees = new HashMap<>();
    public Map<String, TraitPage> traits = new HashMap<>();
    public Map<String, HoneyPage> honey = new HashMap<>();

    ButtonList beesList;
    ButtonList traitsList;
    ButtonList honeyList;

    BeepediaPage home;
    BeepediaPage activePage;
    ButtonList activeList = null;

    ResourceLocation background = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/screen.png");
    ResourceLocation buttonImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/button.png");

    protected void preInit() {
        this.xSize = 286;
        this.ySize = 182;
    }

    public void setActive(BeepediaPage activePage) {
        if (this.activePage != null) this.activePage.closePage();
        this.activePage = activePage;
        this.activePage.openPage();
        if (activePage instanceof BeePage) {
            container.setPageType(PageType.BEE);
            setActiveList(beesList);
        }
        if (activePage instanceof TraitPage) {
            container.setPageType(PageType.TRAIT);
            setActiveList(traitsList);
        }
        if (activePage instanceof HoneyPage) {
            container.setPageType(PageType.HONEY);
            setActiveList(honeyList);
        }
        container.setPageID(activePage.id);
    }

    @Override
    protected void init() {
        super.init();
        buttons.clear();
        int x = this.guiLeft;
        int y = this.guiTop;
        int subX = x + 130;
        int subY = y + 0;
        honey.put("honey", new HoneyPage(this, BeeConstants.defaultHoney, "honey", subX, subY));
        honey.put("catnip", new HoneyPage(this, KittenBee.getHoneyBottleData(), "catnip", subX, subY));
        TraitRegistry.getRegistry().getTraits().forEach((s, b) -> traits.put(s, new TraitPage(this, b, s, subX, subY)));
        BeeRegistry.getRegistry().getBees().forEach((s, b) -> bees.put(s, new BeePage(this, b, s, subX, subY)));
        BeeRegistry.getRegistry().getHoneyBottles().forEach((s, h) -> honey.put(s, new HoneyPage(this, h, s, subX, subY)));
        home = new HomePage(this, subX, subY);
        addButton(new Button(x + (xSize / 2) - 20, y + ySize - 25, 40, 20, new TranslationTextComponent("gui.resourcefulbees.beepedia.home_button"), onPress -> {
            setActive(home);
        }));
        bees.forEach((s, b) -> addButton(b.listButton));
        traits.forEach((s, b) -> addButton(b.listButton));
        honey.forEach((s, b) -> addButton(b.listButton));
        initSidebar();
        returnState();
    }

    private void returnState() {
        setActive(container.getPageType(), container.getPageID());
        if (container.beesScroll != 0) beesList.setScrollPos(container.beesScroll);
        if (container.honeyScroll != 0) honeyList.setScrollPos(container.honeyScroll);
        if (container.traitScroll != 0) traitsList.setScrollPos(container.traitScroll);
    }

    private void setActive(BeepediaScreen.PageType pageType, String pageID) {
        BeepediaPage page;
        if (pageType == null || pageID == null) {
            setActive(home);
            return;
        }
        switch (pageType) {
            case BEE:
                page = bees.get(pageID);
                setActiveList(beesList);
                if (page instanceof BeePage) {
                    ((BeePage) page).setSubPage(container.getBeeSubPageType());
                }
                break;
            case HONEY:
                page = honey.get(pageID);
                setActiveList(honeyList);
                break;
            case TRAIT:
                page = traits.get(pageID);
                setActiveList(traitsList);
                break;
            default:
                throw new IllegalStateException(String.format("How did you get this: %s? please contact an developer if you encounter this error.", pageType));
        }
        if (page != null) setActive(page);
    }

    public void initSidebar() {
        ItemStack beeItem = new ItemStack(Items.BEEHIVE);
        ItemStack traitItem = new ItemStack(Items.ENDER_PEARL);
        ItemStack honeyItem = new ItemStack(Items.HONEY_BOTTLE);
        int x = this.guiLeft;
        int y = this.guiTop;
        TabButton beesButton = new TabButton(x + 0, y + 0, 20, 20, 0, 0, 20, buttonImage, beeItem, 2, 2, onPress -> {
            setActiveList(beesList);
        });
        TabButton traitsButton = new TabButton(x + 20, y + 0, 20, 20, 0, 0, 20, buttonImage, traitItem, 2, 2, onPress -> {
            setActiveList(traitsList);
        });
        TabButton honeyButton = new TabButton(x + 40, y + 0, 20, 20, 0, 0, 20, buttonImage, honeyItem, 2, 2, onPress -> {
            setActiveList(honeyList);
        });
        addButton(beesButton);
        addButton(traitsButton);
        addButton(honeyButton);
        beesList = new ButtonList(x + 8, y + 30, 124, 100, 21, beesButton, bees);
        traitsList = new ButtonList(x + 8, y + 30, 124, 100, 21, traitsButton, traits);
        honeyList = new ButtonList(x + 8, y + 30, 124, 100, 21, honeyButton, honey);
        setActiveList(beesList);
    }

    private void setActiveList(ButtonList buttonList) {
        if (this.activeList != null) this.activeList.setActive(false);
        this.activeList = buttonList;
        this.activeList.setActive(true);
    }

    public BeepediaScreen(BeepediaContainer beepediaContainer, PlayerInventory inventory, ITextComponent name) {
        super(beepediaContainer, inventory, name);
        preInit();
    }

    @Override
    protected void drawBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        Minecraft client = this.client;
        if (client != null) {
            client.getTextureManager().bindTexture(background);
            int x = this.guiLeft;
            int y = this.guiTop;
            drawTexture(matrix, x, y, 0, 0, this.getXSize(), this.getYSize(), 286, 182);
        }
        activePage.renderBackground(matrix, partialTick, mouseX, mouseY);
        activeList.updateList();
    }

    @Override
    protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.textRenderer.draw(matrixStack, this.title, (float) this.titleX, (float) ySize - 15, Color.parse("gray").getRgb());
        activePage.renderForeground(matrixStack, mouseX, mouseY);
    }

    public List<TraitPage> getTraits(CustomBeeData beeData) {
        List<TraitPage> pages = new ArrayList<>();
        if (beeData.getTraitNames() == null || beeData.getTraitNames().length == 0) return pages;
        for (String traitName : beeData.getTraitNames()) {
            pages.add(traits.get(traitName));
        }
        return pages;
    }

    public Widget addButton(Widget button) {
        return super.addButton(button);
    }

    public static class TabButton extends TabImageButton {

        public TabButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, @NotNull ItemStack displayItem, int itemX, int itemY, IPressable onPressIn) {
            super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, displayItem, itemX, itemY, onPressIn);
        }

        @Override
        public void renderButton(@Nonnull MatrixStack matrix, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getTextureManager().bindTexture(this.resourceLocation);
            RenderSystem.disableDepthTest();
            int i = this.yTexStart;
            if (!this.active) {
                i += yDiffText * 2;
            } else if (this.isHovered()) {
                i += this.yDiffText;
            }
            drawTexture(matrix, this.x, this.y, (float) this.xTexStart, (float) i, this.width, this.height, 128, 128);
            if (this.displayItem != null)
                Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(this.displayItem, this.x + this.itemX, this.y + this.itemY);
            RenderSystem.enableDepthTest();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (activeList != null) {
            activeList.updatePos((int) (scrollAmount * 8));
        }
        container.beesScroll = beesList.scrollPos;
        container.traitScroll = traitsList.scrollPos;
        container.honeyScroll = honeyList.scrollPos;
        return super.mouseScrolled(mouseX, mouseY, scrollAmount);
    }

    public class ButtonList {
        public int xPos;
        public int yPos;
        public int height;
        public int width;
        public int itemHeight;
        public int scrollPos = 0;
        public TabButton button;
        public boolean active = false;
        Map<String, ? extends BeepediaPage> list;
        Map<String, BeepediaPage> reducedList = new HashMap<>();

        public ButtonList(int xPos, int yPos, int height, int width, int itemHeight, TabButton button, Map<String, ? extends BeepediaPage> list) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.height = height;
            this.width = width;
            this.itemHeight = itemHeight;
            this.list = list;
            this.button = button;
            updateReducedList(null);
            list.forEach((s, b) -> b.listButton.setParent(this));
        }

        public void updateReducedList(String search) {
            reducedList.clear();
            if (search != null && !search.isEmpty() && !search.equals("")) {
                list.forEach((s, b) -> {
                    if (s.contains(search.toLowerCase()) || b.getSearch().toLowerCase().contains(search.toLowerCase())) {
                        reducedList.put(s, b);
                    }
                });
            } else {
                reducedList = new HashMap<>(list);
            }
        }

        public void updatePos(int newPos) {
            // update position of list
            if (height > reducedList.size() * 20) return;
            scrollPos += newPos;
            if (scrollPos > 0) scrollPos = 0;
            else if (scrollPos < -(reducedList.size() * itemHeight - height))
                scrollPos = -(reducedList.size() * itemHeight - height);
        }

        public void updateList() {
            // update each button
            AtomicInteger counter = new AtomicInteger();
            reducedList.forEach((s, b) -> {
                b.updateListPosition(xPos, (yPos + scrollPos + counter.get() * itemHeight));
                counter.getAndIncrement();
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            list.forEach((s, b) -> {
                if (b.listButton != null) b.listButton.visible = active;
            });
        }

        public void setScrollPos(int scrollPos) {
            this.scrollPos = scrollPos;
        }
    }

    public enum PageType {

        // main pages
        BEE,
        HONEY,
        TRAIT,

        // bee sub pages
        INFO,
        SPAWNING,
        BREEDING,
        MUTATIONS,
        CENTRIFUGE,
        TRAIT_LIST

    }
}
