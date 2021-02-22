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
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.entity.passive.KittenBee;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.TraitRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BeepediaScreen extends Screen {

    private static PageType pageType = PageType.BEE;
    private static String pageID = null;
    private static BeePage.SubPageType beeSubPage = BeePage.SubPageType.INFO;
    private static int beesScroll = 0;
    private static int honeyScroll = 0;
    private static int traitScroll = 0;

    public int xSize;
    public int ySize;
    public int guiLeft;
    public int guiTop;
    public int ticksOpen = 0;

    public Map<String, BeePage> bees = new TreeMap<>();
    public Map<String, TraitPage> traits = new TreeMap<>();
    public Map<String, HoneyPage> honey = new TreeMap<>();

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

    public BeepediaScreen(ITextComponent name, String pageID) {
        super(name);
        if (pageID != null) {
            setPageType(PageType.BEE);
            setPageID(pageID);
            setBeeSubPage(BeePage.SubPageType.INFO);
        }
        this.xSize = 286;
        this.ySize = 182;
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
        setActive(getPageType(), getPageID());
        if (beesScroll != 0) beesList.setScrollPos(beesScroll);
        if (honeyScroll != 0) honeyList.setScrollPos(honeyScroll);
        if (traitScroll != 0) traitsList.setScrollPos(traitScroll);
    }

    private void setActive(BeepediaScreen.PageType pageType, String pageID) {
        if (pageID == null || pageType == null) {
            setActive(home);
            return;
        }
        BeepediaPage page;
        switch (pageType) {
            case BEE:
                page = bees.get(pageID);
                setActiveList(beesList);
                activeListType = PageType.BEE;
                break;
            case HONEY:
                page = honey.get(pageID);
                setActiveList(honeyList);
                activeListType = PageType.HONEY;
                break;
            case TRAIT:
                page = traits.get(pageID);
                setActiveList(traitsList);
                activeListType = PageType.TRAIT;
                break;
            default:
                throw new IllegalStateException(String.format("How did you get this: %s? please contact an developer if you encounter this error.", pageType));
        }
        if (page != null) setActive(page);
        else setActive(home);
    }

    public void setActive(BeepediaPage activePage) {
        if (this.activePage != null) this.activePage.closePage();
        this.activePage = activePage;
        this.activePage.openPage();
        if (activePage instanceof BeePage) {
            setPageType(PageType.BEE);
            setActiveList(beesList);
        }
        if (activePage instanceof TraitPage) {
            setPageType(PageType.TRAIT);
            setActiveList(traitsList);
        }
        if (activePage instanceof HoneyPage) {
            setPageType(PageType.HONEY);
            setActiveList(honeyList);
        }
        setPageID(activePage.id);
    }

    private void setActiveList(ButtonList buttonList) {
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
        TabButton beesButton = new TabButton(x + 45, y + 8, 20, 20, 0, 0, 20, buttonImage, beeItem, 2, 2, onPress -> {
            setActiveList(beesList);
            activeListType = PageType.BEE;
        });
        TabButton traitsButton = new TabButton(x + 66, y + 8, 20, 20, 0, 0, 20, buttonImage, traitItem, 2, 2, onPress -> {
            setActiveList(traitsList);
            activeListType = PageType.TRAIT;
        });
        TabButton honeyButton = new TabButton(x + 87, y + 8, 20, 20, 0, 0, 20, buttonImage, honeyItem, 2, 2, onPress -> {
            setActiveList(honeyList);
            activeListType = PageType.HONEY;
        });
        addButton(beesButton);
        addButton(traitsButton);
        addButton(honeyButton);
        beesList = new ButtonList(x + 8, y + 31, 123, 100, 21, beesButton, bees);
        traitsList = new ButtonList(x + 8, y + 31, 123, 100, 21, traitsButton, traits);
        honeyList = new ButtonList(x + 8, y + 31, 123, 100, 21, honeyButton, honey);
        setActiveList(beesList);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
        drawBackground(matrixStack, partialTick, mouseX, mouseY);
        super.render(matrixStack, mouseX, mouseY, partialTick);
        drawForeground(matrixStack, mouseX, mouseY);
    }

    protected void drawBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        Minecraft client = this.client;
        if (client != null) {
            client.getTextureManager().bindTexture(background);
            int x = this.guiLeft;
            int y = this.guiTop;
            drawTexture(matrix, x, y, 0, 0, this.xSize, this.ySize, 286, 182);
        }
        activePage.renderBackground(matrix, partialTick, mouseX, mouseY);
        activeList.updateList();
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
        this.textRenderer.draw(matrixStack, title, this.guiLeft + 10, this.guiTop + 20, Color.parse("white").getRgb());
    }

    public List<TraitPage> getTraits(CustomBeeData beeData) {
        List<TraitPage> pages = new ArrayList<>();
        if (beeData.getTraitNames() == null || beeData.getTraitNames().length == 0) return pages;
        for (String traitName : beeData.getTraitNames()) {
            pages.add(traits.get(traitName));
        }
        return pages;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (activeList != null) {
            activeList.updatePos((int) (scrollAmount * 8));
        }
        beesScroll = beesList.scrollPos;
        traitScroll = traitsList.scrollPos;
        honeyScroll = honeyList.scrollPos;
        return super.mouseScrolled(mouseX, mouseY, scrollAmount);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public Widget addButton(Widget button) {
        return super.addButton(button);
    }

    public void drawSlot(MatrixStack matrix, IItemProvider item, int xPos, int yPos, int mouseX, int mouseY) {
        getMinecraft().getTextureManager().bindTexture(slotImage);
        ItemStack itemStack = new ItemStack(item);
        drawTexture(matrix, xPos, yPos, 0, 0, 20, 20, 20, 20);
        getMinecraft().getItemRenderer().renderInGui(itemStack, xPos + 2, yPos + 2);
        if (mouseX >= xPos && mouseY >= yPos && mouseX <= xPos + 20 && mouseY <= yPos + 20) {
            renderTooltip(matrix, itemStack, mouseX, mouseY);
        }
    }

    public static void renderEntity(MatrixStack matrixStack, Entity entity, World world, float x, float y, float rotation, float renderScale) {
        if (world != null) {
            float scaledSize = 20 * renderScale;
            Minecraft mc = Minecraft.getInstance();
            if (entity instanceof BeeEntity) {
                entity.ticksExisted = mc.player.ticksExisted;
                ((BeeEntity) entity).renderYawOffset = rotation;
                if (entity instanceof CustomBeeEntity) {
                    scaledSize = 20 / ((CustomBeeEntity) entity).getBeeData().getSizeModifier() * renderScale;
                }
            }
            if (mc.player != null) {
                matrixStack.push();
                matrixStack.translate(8, 14, 0.5D);
                matrixStack.translate(x, y, 1);
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
                matrixStack.translate(0.0F, -0.2F, 1);
                matrixStack.scale(-scaledSize, scaledSize, 30);
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270.0F));
                EntityRendererManager entityrenderermanager = mc.getRenderManager();
                IRenderTypeBuffer.Impl irendertypebuffer$impl = mc.getBufferBuilders().getEntityVertexConsumers();
                entityrenderermanager.render(entity, 0, 0, 0.0D, mc.getRenderPartialTicks(), 1, matrixStack, irendertypebuffer$impl, 15728880);
                irendertypebuffer$impl.draw();
            }
            matrixStack.pop();
        }
    }

    public Entity initEntity(ResourceLocation entityTypeRegistryID) {
        return initEntity(ForgeRegistries.ENTITIES.getValue(entityTypeRegistryID), getMinecraft().world);
    }

    public Entity initEntity(EntityType<?> left, ClientWorld world) {
        return left.create(world);
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
            drawTexture(matrix, this.x, this.y, (float) this.xTexStart, (float) i, this.width, this.height, width, yDiffText * 3);
            if (this.displayItem != null)
                Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(this.displayItem, this.x + this.itemX, this.y + this.itemY);
            RenderSystem.enableDepthTest();
        }
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
        Map<String, BeepediaPage> reducedList = new TreeMap<>();

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
            button.active = !active;
            list.forEach((s, b) -> {
                if (b.listButton != null) b.listButton.visible = active;
            });
        }

        public void setScrollPos(int scrollPos) {
            this.scrollPos = scrollPos;
        }
    }

    public static PageType getPageType() {
        return pageType;
    }

    public static void setPageType(PageType pageType) {
        BeepediaScreen.pageType = pageType;
    }

    public static String getPageID() {
        return pageID;
    }

    public static void setPageID(String pageID) {
        BeepediaScreen.pageID = pageID;
    }

    public static BeePage.SubPageType getBeeSubPage() {
        return beeSubPage;
    }

    public static void setBeeSubPage(BeePage.SubPageType beeSubPage) {
        BeepediaScreen.beeSubPage = beeSubPage;
    }

    public enum PageType {
        // main pages
        BEE,
        HONEY,
        TRAIT
    }
}
