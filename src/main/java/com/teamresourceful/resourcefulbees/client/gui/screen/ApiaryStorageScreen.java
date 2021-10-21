package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.gui.widget.TabImageButton;
import com.teamresourceful.resourcefulbees.common.inventory.containers.ApiaryStorageContainer;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTab;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.ApiaryTabMessage;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryStorageTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.NotNull;

public class ApiaryStorageScreen extends ContainerScreen<ApiaryStorageContainer> {

    private static final ResourceLocation BACKGROUND_1X9 = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_storage_9.png");
    private static final ResourceLocation BACKGROUND_3X9 = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_storage_27.png");
    private static final ResourceLocation BACKGROUND_6X9 = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_storage_54.png");
    private static final ResourceLocation BACKGROUND_9X9 = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_storage_81.png");
    private static final ResourceLocation BACKGROUND_9X12 = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_storage_108.png");
    private static final ResourceLocation TABS_BG = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_gui_tabs.png");

    private final ApiaryStorageTileEntity apiaryStorageTileEntity;
    private ResourceLocation background;
    private TabImageButton mainTabButton;

    public ApiaryStorageScreen(ApiaryStorageContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        apiaryStorageTileEntity = this.menu.getApiaryStorageTileEntity();
        preInit();
    }

    protected void preInit(){
        this.imageWidth = 226;

        switch (this.menu.getNumberOfSlots()) {
            case 27:
                this.imageHeight = 168;
                background = BACKGROUND_3X9;
                break;
            case 54:
                this.imageHeight = 222;
                background = BACKGROUND_6X9;
                break;
            case 81:
                this.imageHeight = 276;
                background = BACKGROUND_9X9;
                break;
            case 108:
                this.imageWidth = 281;
                this.imageHeight = 276;
                background = BACKGROUND_9X12;
                break;
            default:
                this.imageHeight = 132;
                background = BACKGROUND_1X9;
        }
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();

        int i = this.leftPos;
        int j = this.topPos;
        int t = i + this.imageWidth - 23;

        mainTabButton = this.addButton(new TabImageButton(t+1, j+17, 18, 18, 110, 0, 18, TABS_BG, new ItemStack(ModItems.BEE_JAR.get()), 1, 1,
                onPress -> this.changeScreen(ApiaryTab.MAIN), 128, 128) {

            @Override
            public void renderToolTip(@NotNull MatrixStack matrix, int mouseX, int mouseY) {
                TranslationTextComponent s = new TranslationTextComponent("gui.resourcefulbees.apiary.button.main_screen");
                ApiaryStorageScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        });

        this.addButton(new TabImageButton(t + 1, j + 37, 18, 18, 110, 0, 18, TABS_BG, new ItemStack(Items.HONEYCOMB), 2, 1,
                onPress -> this.changeScreen(ApiaryTab.STORAGE), 128, 128) {

            @Override
            public void renderToolTip(@NotNull MatrixStack matrix,int mouseX, int mouseY) {
                TranslationTextComponent s = new TranslationTextComponent("gui.resourcefulbees.apiary.button.storage_screen");
                ApiaryStorageScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        }).active = false;
    }

    private void changeScreen(ApiaryTab tab) {
        switch (tab) {
            case STORAGE:
                break;
            case MAIN:
                if (mainTabButton.active)
                    NetPacketHandler.sendToServer(new ApiaryTabMessage(getApiaryStorageTileEntity().getBlockPos(), ApiaryTab.MAIN));
        }
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        if (this.menu.isRebuild()) {
            preInit();
            init();
            this.menu.setRebuild(false);
        }

        mainTabButton.active = getApiaryStorageTileEntity().getApiary() != null;

        Minecraft client = this.minecraft;
        if (client != null) {
            this.minecraft.getTextureManager().bind(getBackground());
            int i = this.leftPos;
            int j = this.topPos;
            blit(matrix, i + 26, j, 0, 0, this.imageWidth, this.imageHeight, 384, 384);
            blit(matrix, i + 1, j + 12, 359, 0, 25, 28, 384, 384);
            int t = i + this.imageWidth - 23;
            this.minecraft.getTextureManager().bind(TABS_BG);
            blit(matrix, t -1, j + 12, 0,0, 25, 68, 128, 128);
        }
    }

    @Override
    public void render(@NotNull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (getApiaryStorageTileEntity() != null) {
            this.renderBackground(matrix);
            super.render(matrix, mouseX, mouseY, partialTicks);
            this.renderTooltip(matrix, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(@NotNull MatrixStack matrix, int mouseX, int mouseY) {
        for (Widget widget : this.buttons) {
            if (widget.isHovered()) {
                widget.renderToolTip(matrix, mouseX - this.leftPos, mouseY - this.topPos);
                break;
            }
        }
    }

    public ApiaryStorageTileEntity getApiaryStorageTileEntity() {
        return apiaryStorageTileEntity;
    }

    public ResourceLocation getBackground() {
        return background;
    }
}
