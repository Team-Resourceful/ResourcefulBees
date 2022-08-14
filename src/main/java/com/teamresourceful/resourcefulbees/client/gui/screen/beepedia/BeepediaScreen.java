package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.capabilities.IBeepediaData;
import com.teamresourceful.resourcefulbees.client.gui.widget.ModImageButton;
import com.teamresourceful.resourcefulbees.client.gui.widget.TabImageButton;
import com.teamresourceful.resourcefulbees.client.gui.widget.TooltipScreen;
import com.teamresourceful.resourcefulbees.client.gui.widget.TooltipWidget;
import com.teamresourceful.resourcefulbees.common.capabilities.BeepediaData;
import com.teamresourceful.resourcefulbees.common.network.packets.BeepediaEntityMessage;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BeepediaScreen extends TooltipScreen {

    public final boolean isCreative;
    public final boolean hasShades;
    public final IBeepediaData data;

    public List<TooltipWidget> widgets = new LinkedList<>();


    protected int ticksOpen = 0;

    public static final int SCREEN_WIDTH = 328;
    public static final int SCREEN_HEIGHT = 200;

    private ModImageButton backButton;
    private ModImageButton homeButton;
    private ModImageButton searchButton;

    public BeepediaSearchHandler searchHandler;

    @OnlyIn(Dist.CLIENT)
    public BeepediaScreen(boolean isCreative, boolean hasShades, LazyOptional<IBeepediaData> data) {
        super(BeepediaLang.INTERFACE_NAME, SCREEN_WIDTH, SCREEN_HEIGHT);
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
        registerScreen();
        add(searchHandler);
        BeepediaHandler.registerScreen(this);
    }

    private void registerScreen() {
        int x = 0;
        int y = 0;

        registerButtons(x, y);
    }

    /**
     * Define home row buttons, shadesButton and captured bee button
     */
    private void registerButtons(int x, int y) {
        int shadesButtonX = x + SCREEN_WIDTH + 2;
        int shadesButtonY = y + SCREEN_HEIGHT - 32;

        ItemStack book = ItemStack.EMPTY; //TODO  PatchouliAPI.get().getBookStack(ModConstants.SHADES_OF_BEES);

        homeButton = new ModImageButton(x + (SCREEN_WIDTH / 2) - 10, y + SCREEN_HEIGHT - 25, 20, 20, 20, 0, 20, BeepediaImages.HOME_BUTTONS, 60, 60, onPress -> BeepediaHandler.openHomeScreen());
        backButton = new ModImageButton(x + (SCREEN_WIDTH / 2) + 20, y + SCREEN_HEIGHT - 25, 20, 20, 40, 0, 20, BeepediaImages.HOME_BUTTONS, 60, 60, onPress -> BeepediaState.goBackState());
        searchButton = new ModImageButton(x + (SCREEN_WIDTH / 2) - 40, y + SCREEN_HEIGHT - 25, 20, 20, 0, 0, 20, BeepediaImages.HOME_BUTTONS, 60, 60, onPress -> searchHandler.toggleSearch());
        TabImageButton shadesButton = new TabImageButton(shadesButtonX + 6, shadesButtonY + 6, 18, 18, 0, 0, 18,
                BeepediaImages.SHADES_BUTTON_IMAGE, book, 1, 1, onPress ->{} /*TODO PatchouliAPI.get().openBookGUI(ModConstants.SHADES_OF_BEES)*/,
                18, 36, BeepediaLang.FIFTY_SHADES_BUTTON) {
        };
        // add buttons to button array
        addAll(homeButton, backButton, searchButton, shadesButton);
        backButton.active = false;
        shadesButton.visible = hasShades;
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTick) {
        BeepediaHandler.preInit();
        searchHandler.visible = BeepediaState.isSearchVisible();

        // all visibility changes must be done before this line
        super.render(matrixStack, mouseX, mouseY, partialTick);
        // initialises the current page's data
        if (hasShades) {
            drawShadesButton(matrixStack);
        }

    }

    private void drawShadesButton(PoseStack matrix) {
        int shadesButtonX = x + SCREEN_WIDTH + 2;
        int shadesButtonY = y + SCREEN_HEIGHT - 32;
        ClientUtils.bindTexture(BeepediaImages.SHADES_BACKGROUND);
        blit(matrix, shadesButtonX, shadesButtonY, 0, 0, 30, 30, 30, 30);
    }

    @Override
    public void drawBackground(PoseStack matrix, int mouseX, int mouseY, float partialTick) {
        Minecraft client = this.minecraft;
        homeButton.active = BeepediaState.isHomeState();
        backButton.active = BeepediaState.hasPastStates();
        int x = this.x;
        int y = this.y;
        if (client != null) {
            ClientUtils.bindTexture(BeepediaImages.BACKGROUND);
            blit(matrix, x, y, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT);
            client.font.draw(matrix, BeepediaLang.VERSION_NUMBER, x + 12.0f, y + SCREEN_HEIGHT - 20.0f, 5592405);
        }
        BeepediaHandler.drawPage(matrix, partialTick, mouseX, mouseY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static Entity initEntity(EntityType<?> entityType) {
        assert Minecraft.getInstance().level != null;
        return entityType.create(Minecraft.getInstance().level);
    }
}
