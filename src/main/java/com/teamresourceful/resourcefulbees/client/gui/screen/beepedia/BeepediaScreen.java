package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.api.capabilities.IBeepediaData;
import com.teamresourceful.resourcefulbees.capabilities.BeepediaData;
import com.teamresourceful.resourcefulbees.client.gui.tooltip.AbstractTooltip;
import com.teamresourceful.resourcefulbees.client.gui.widget.Interaction;
import com.teamresourceful.resourcefulbees.client.gui.widget.ModImageButton;
import com.teamresourceful.resourcefulbees.client.gui.widget.TabImageButton;
import com.teamresourceful.resourcefulbees.client.gui.widget.TooltipWidget;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.network.packets.BeepediaEntityMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BeepediaScreen extends Screen {

    public final boolean isCreative;
    public final boolean hasShades;
    public final IBeepediaData data;

    public List<TooltipWidget> widgets = new LinkedList<>();

    public int guiLeft;
    public int guiTop;
    protected int ticksOpen = 0;

    public static final int SCREEN_WIDTH = 328;
    public static final int SCREEN_HEIGHT = 200;

    private final List<AbstractTooltip> tooltips = new LinkedList<>();
    private final List<Interaction> interactions = new LinkedList<>();
    private ModImageButton backButton;
    private ModImageButton homeButton;
    private ModImageButton searchButton;

    public BeepediaSearchHandler searchHandler;

    @OnlyIn(Dist.CLIENT)
    public BeepediaScreen(boolean isCreative, boolean hasShades, LazyOptional<IBeepediaData> data) {
        super(BeepediaLang.INTERFACE_NAME);
        BeepediaHandler.initBeepediaStates();
        this.hasShades = hasShades;
        this.isCreative = isCreative;
        this.data = data.orElseGet(BeepediaData::new);
        searchHandler = new BeepediaSearchHandler(this);
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
        backButton = new ModImageButton(x + (SCREEN_WIDTH / 2) + 20, y + SCREEN_HEIGHT - 25, 20, 20, 40, 0, 20, BeepediaImages.HOME_BUTTONS, 60, 60, onPress -> BeepediaState.goBackState());
        searchButton = new ModImageButton(x + (SCREEN_WIDTH / 2) - 40, y + SCREEN_HEIGHT - 25, 20, 20, 0, 0, 20, BeepediaImages.HOME_BUTTONS, 60, 60, onPress -> {
            searchHandler.toggleSearch();
        });
        TabImageButton shadesButton = new TabImageButton(shadesButtonX + 6, shadesButtonY + 6, 18, 18, 0, 0, 18,
                BeepediaImages.SHADES_BUTTON_IMAGE, book, 1, 1, onPress -> PatchouliAPI.get().openBookGUI(ModConstants.SHADES_OF_BEES),
                18, 36, BeepediaLang.FIFTY_SHADES_BUTTON) {
        };
        // add buttons to button array
        addButtons(homeButton, backButton, searchButton, shadesButton);
        backButton.active = false;
        shadesButton.visible = hasShades;

        searchHandler.registerSearch(x, y);
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
        tooltips.clear();
        interactions.clear();
        // initialises the current page's data
        BeepediaHandler.preInit();
        renderBackground(matrixStack, 0);
        drawBackground(matrixStack, partialTick, mouseX, mouseY);
        if (hasShades) {
            drawShadesButton(matrixStack);
        }
        for (Widget button : this.buttons) {
            button.render(matrixStack, mouseX, mouseY, partialTick);
        }

        searchHandler.render(matrixStack, mouseX, mouseY, partialTick);
        drawTooltips(matrixStack, mouseX, mouseY);
    }

    @Override
    public void tick() {
        super.tick();
        ticksOpen++;
        BeepediaHandler.tick(ticksOpen);
    }

    private void drawShadesButton(MatrixStack matrix) {
        int shadesButtonX = guiLeft + SCREEN_WIDTH + 2;
        int shadesButtonY = guiTop + SCREEN_HEIGHT - 32;
        Minecraft.getInstance().getTextureManager().bind(BeepediaImages.SHADES_BACKGROUND);
        blit(matrix, shadesButtonX, shadesButtonY, 0, 0, 30, 30, 30, 30);
    }

    protected void drawBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        Minecraft client = this.minecraft;
        homeButton.active = BeepediaState.isHomeState();
        backButton.active = BeepediaState.hasPastStates();
        int x = this.guiLeft;
        int y = this.guiTop;
        if (client != null) {
            client.getTextureManager().bind(BeepediaImages.BACKGROUND);
            blit(matrix, x, y, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT);
            Minecraft.getInstance().font.draw(matrix, BeepediaLang.VERSION_NUMBER, x + 12.0f, y + SCREEN_HEIGHT - 20.0f, 5592405);
        }
        BeepediaHandler.drawPage(matrix, partialTick, mouseX, mouseY);
    }

    private void drawTooltips(MatrixStack matrix, int mouseX, int mouseY) {
        widgets.forEach(t -> t.drawTooltips(matrix, this, mouseX, mouseY));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static boolean mouseHovering(float x, float y, int width, int height, int mouseX, int mouseY) {
        return mouseX > x && mouseY > y && mouseX < x + width && mouseY < y + height;
    }

    public <T extends TooltipWidget> @NotNull T addWidget(@NotNull T widget) {
        widgets.add(widget);
        return widget;
    }

    public <T extends TooltipWidget> void addButtons(List<@NotNull T> widgets) {
        widgets.forEach(this::addWidget);
    }

    public <T extends TooltipWidget> void addButtons(@NotNull T... widgets) {
        addButtons(Arrays.asList(widgets));
    }

    public static <T extends TooltipWidget> void setButtonsVisibility(boolean visible, List<@NotNull T> widgets) {
        widgets.forEach(w -> w.visible = visible);
    }

    public static <T extends TooltipWidget> void setButtonsActive(boolean active, List<@NotNull T> widgets) {
        widgets.forEach(w -> w.active = active);
    }

    public static <T extends TooltipWidget> void setButtonsVisibility(boolean b, @NotNull T... widgets) {
        setButtonsVisibility(b, Arrays.asList(widgets));
    }

    public static <T extends TooltipWidget> void setButtonsActive(boolean b, @NotNull T... widgets) {
        setButtonsActive(b, Arrays.asList(widgets));
    }

    @OnlyIn(Dist.CLIENT)
    public static Entity initEntity(EntityType<?> entityType) {
        assert Minecraft.getInstance().level != null;
        return entityType.create(Minecraft.getInstance().level);
    }
}
