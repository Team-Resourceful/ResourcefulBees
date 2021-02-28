package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.BeePage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.HomePage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.HoneyPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.TraitPage;
import com.resourcefulbees.resourcefulbees.client.gui.widget.ButtonList;
import com.resourcefulbees.resourcefulbees.client.gui.widget.ImageButton;
import com.resourcefulbees.resourcefulbees.client.gui.widget.TabImageButton;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.entity.passive.KittenBee;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.TraitRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RenderCuboid;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BeepediaScreen extends Screen {

    private static int beesScroll = 0;
    private static int honeyScroll = 0;
    private static int traitScroll = 0;

    protected static final LinkedList<BeepediaScreenState> pastStates = new LinkedList<>();
    public static BeepediaScreenState currScreenState = new BeepediaScreenState();

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
        addButton(new ImageButton(x + (xSize / 2) - 10, y + ySize - 25, 20, 20, 20, 0, 20, home_buttons, 60, 60, onPress -> setActive(home)));
        backButton = new ImageButton(x + (xSize / 2) + 20, y + ySize - 25, 20, 20, 40, 0, 20, home_buttons, 60, 60, onPress -> {
            if (!pastStates.isEmpty()) {
                goBackState();
                returnState(true);
            }
        });
        backButton.active = false;
        addButton(backButton);
        bees.forEach((s, b) -> addButton(b.listButton));
        traits.forEach((s, b) -> addButton(b.listButton));
        honey.forEach((s, b) -> addButton(b.listButton));
        initSidebar();
        returnState(false);
    }

    private static void goBackState() {
        currScreenState = pastStates.pop();
    }

    private void returnState(boolean goingBack) {
        setActive(currScreenState.getPageType(), currScreenState.getPageID(), goingBack);
        if (beesScroll != 0) beesList.setScrollPos(beesScroll);
        if (honeyScroll != 0) honeyList.setScrollPos(honeyScroll);
        if (traitScroll != 0) traitsList.setScrollPos(traitScroll);
    }

    public void setActive(PageType pageType, String pageID) {
        setActive(pageType, pageID, false);
    }

    public void setActive(PageType pageType, String pageID, boolean goingBack) {
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
        if (page != null) setActive(page, goingBack);
        else setActive(home, goingBack);
    }

    public void setActive(BeepediaPage activePage) {
        setActive(activePage, false);
    }

    public void setActive(BeepediaPage activePage, boolean goingBack) {
        if (this.activePage != null) {
            if (!this.activePage.getClass().equals(activePage.getClass()) && !(this.activePage instanceof HomePage) && !goingBack) {
                resetScreenState();
            }
            this.activePage.closePage();
        }
        this.activePage = activePage;
        this.activePage.openPage();
        if (activePage instanceof BeePage) {
            currScreenState.setPageType(PageType.BEE);
            setActiveList(beesList);
        }
        if (activePage instanceof TraitPage) {
            currScreenState.setPageType(PageType.TRAIT);
            setActiveList(traitsList);
        }
        if (activePage instanceof HoneyPage) {
            currScreenState.setPageType(PageType.HONEY);
            setActiveList(honeyList);
        }
        currScreenState.setPageID(activePage.id);
    }

    public static void resetScreenState() {
        pastStates.push(currScreenState);
        currScreenState = new BeepediaScreenState();
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
        TabImageButton beesButton = new TabImageButton(x + 45, y + 8, 20, 20, 0, 0, 20, buttonImage, beeItem, 2, 2, onPress -> {
            setActiveList(beesList);
            activeListType = PageType.BEE;
        }, getTooltipProvider(new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.bees")));
        TabImageButton traitsButton = new TabImageButton(x + 66, y + 8, 20, 20, 0, 0, 20, buttonImage, traitItem, 2, 2, onPress -> {
            setActiveList(traitsList);
            activeListType = PageType.TRAIT;
        }, getTooltipProvider(new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits")));
        TabImageButton honeyButton = new TabImageButton(x + 87, y + 8, 20, 20, 0, 0, 20, buttonImage, honeyItem, 2, 2, onPress -> {
            setActiveList(honeyList);
            activeListType = PageType.HONEY;
        }, getTooltipProvider(new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.honey")));
        addButton(beesButton);
        addButton(traitsButton);
        addButton(honeyButton);
        beesList = new ButtonList(x + 8, y + 31, 123, 100, 21, beesButton, bees);
        traitsList = new ButtonList(x + 8, y + 31, 123, 100, 21, traitsButton, traits);
        honeyList = new ButtonList(x + 8, y + 31, 123, 100, 21, honeyButton, honey);
        setActiveList(beesList);
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
        drawBackground(matrixStack, partialTick, mouseX, mouseY);
        super.render(matrixStack, mouseX, mouseY, partialTick);
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
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private static void updateScrollPos(ButtonList beesList, ButtonList traitsList, ButtonList honeyList) {
        beesScroll = beesList.scrollPos;
        traitScroll = traitsList.scrollPos;
        honeyScroll = honeyList.scrollPos;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static boolean mouseHovering(float x, float y, int width, int height, int mouseX, int mouseY) {
        return mouseX > x && mouseY > y && mouseX < x + width && mouseY < y + height;
    }

    public void drawSlot(MatrixStack matrix, IItemProvider item, int amount, int xPos, int yPos, int mouseX, int mouseY) {
        if (item instanceof FlowingFluidBlock) {
            drawFluidSlot(matrix, new FluidStack(((FlowingFluidBlock) item).getFluid().getStillFluid(), amount), xPos, yPos, mouseX, mouseY, true);
        } else {
            drawSlot(matrix, new ItemStack(item, amount), xPos, yPos, mouseX, mouseY);
        }
    }

    public void drawSlot(MatrixStack matrix, IItemProvider item, int xPos, int yPos, int mouseX, int mouseY) {
        if (item instanceof FlowingFluidBlock) {
            drawFluidSlot(matrix, new FluidStack(((FlowingFluidBlock) item).getFluid().getStillFluid(), 1000), xPos, yPos, mouseX, mouseY, false);
        } else {
            drawSlot(matrix, new ItemStack(item), xPos, yPos, mouseX, mouseY);
        }
    }

    private void drawFluidSlot(MatrixStack matrix, FluidStack fluidStack, int xPos, int yPos, int mouseX, int mouseY, boolean showAmount) {
        getMinecraft().getTextureManager().bindTexture(slotImage);
        drawTexture(matrix, xPos, yPos, 0, 0, 20, 20, 20, 20);
        TextureAtlasSprite sprite = Minecraft.getInstance().getSpriteAtlas(PlayerContainer.BLOCK_ATLAS_TEXTURE).apply(fluidStack.getFluid().getAttributes().getStillTexture());
        int color = fluidStack.getFluid().getAttributes().getColor();
        float red = RenderCuboid.getRed(color);
        float green = RenderCuboid.getGreen(color);
        float blue = RenderCuboid.getBlue(color);
        float alpha = RenderCuboid.getAlpha(color);
        //noinspection deprecation
        RenderSystem.color4f(red, green, blue, alpha);
        this.client.getTextureManager().bindTexture(sprite.getAtlas().getId());
        drawSprite(matrix, xPos + 2, yPos + 2, this.getZOffset(), 16, 16, sprite);
        if (mouseX >= xPos && mouseY >= yPos && mouseX <= xPos + 20 && mouseY <= yPos + 20) {
            renderFluidTooltip(matrix, fluidStack, mouseX, mouseY, showAmount);
        }
    }

    private void renderFluidTooltip(MatrixStack matrix, FluidStack fluidStack, int mouseX, int mouseY, boolean showAmount) {
        List<ITextComponent> tooltip = new ArrayList<>();
        tooltip.add(fluidStack.getDisplayName());
        if (showAmount) {
            String amount = fluidStack.getAmount() < 500 || BeeInfoUtils.isShiftPressed() ? fluidStack.getAmount() + " mb" : (float) fluidStack.getAmount() / 1000 + " B";
            tooltip.add(new StringTextComponent(amount));
        }
        tooltip.add(new StringTextComponent(fluidStack.getFluid().getRegistryName().toString()).formatted(TextFormatting.DARK_GRAY));
        renderTooltip(matrix, tooltip, mouseX, mouseY);
    }

    public void drawSlot(MatrixStack matrix, ItemStack item, int xPos, int yPos, int mouseX, int mouseY) {
        getMinecraft().getTextureManager().bindTexture(slotImage);
        drawTexture(matrix, xPos, yPos, 0, 0, 20, 20, 20, 20);
        getMinecraft().getItemRenderer().renderInGui(item, xPos + 2, yPos + 2);
        if (mouseX >= xPos && mouseY >= yPos && mouseX <= xPos + 20 && mouseY <= yPos + 20) {
            renderTooltip(matrix, item, mouseX, mouseY);
        }
    }

    public static void renderEntity(MatrixStack matrixStack, Entity entity, World world, float x, float y, float rotation, float renderScale) {
        if (world == null) return;
        float scaledSize = 20;
        Minecraft mc = Minecraft.getInstance();
        if (entity instanceof LivingEntity) {
            if (mc.player != null) entity.ticksExisted = mc.player.ticksExisted;
            if (entity instanceof CustomBeeEntity) {
                scaledSize = 20 / ((CustomBeeEntity) entity).getBeeData().getSizeModifier();
            } else {
                scaledSize = 20 / (entity.getWidth() > entity.getHeight() ? entity.getWidth() : entity.getHeight());
            }
        }
        if (mc.player != null) {
            matrixStack.push();
            matrixStack.translate(10, 20 * renderScale, 0.5);
            matrixStack.translate(x, y, 1);
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
            matrixStack.translate(0, 0, 1);
            matrixStack.scale(-(scaledSize * renderScale), (scaledSize * renderScale), 30);
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotation));
            EntityRendererManager entityrenderermanager = mc.getRenderManager();
            IRenderTypeBuffer.Impl renderTypeBuffer = mc.getBufferBuilders().getEntityVertexConsumers();
            entityrenderermanager.render(entity, 0, 0, 0.0D, mc.getRenderPartialTicks(), 1, matrixStack, renderTypeBuffer, 15728880);
            renderTypeBuffer.draw();
        }
        matrixStack.pop();
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
        TRAIT
    }
}
