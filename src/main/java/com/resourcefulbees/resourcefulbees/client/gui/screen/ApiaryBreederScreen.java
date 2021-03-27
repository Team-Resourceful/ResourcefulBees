package com.resourcefulbees.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.client.gui.widget.TabImageButton;
import com.resourcefulbees.resourcefulbees.container.ApiaryBreederContainer;
import com.resourcefulbees.resourcefulbees.lib.ApiaryTabs;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.ApiaryTabMessage;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryBreederTileEntity;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;

public class ApiaryBreederScreen extends ContainerScreen<ApiaryBreederContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_breeder_gui.png");
    private static final ResourceLocation TABS_BG = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_gui_tabs.png");

    private ApiaryBreederTileEntity apiaryBreederTileEntity;

    private TabImageButton mainTabButton;
    private TabImageButton storageTabButton;


    public ApiaryBreederScreen(ApiaryBreederContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);

        preInit();
    }

    protected void preInit(){
        this.imageWidth = 226;
        this.imageHeight = 110 + this.menu.getNumberOfBreeders() * 20;
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();

        apiaryBreederTileEntity = this.menu.getApiaryBreederTileEntity();

        int i = this.leftPos;
        int j = this.topPos;
        int t = i + this.imageWidth - 24;

        mainTabButton = this.addButton(new TabImageButton(t+1, j+17, 18, 18, 110, 0, 18, TABS_BG, new ItemStack(ModItems.BEE_JAR.get()), 1, 1,
                onPress -> this.changeScreen(ApiaryTabs.MAIN), 128, 128) {

            @Override
            public void renderToolTip(@NotNull MatrixStack matrix, int mouseX, int mouseY) {
                StringTextComponent s = new StringTextComponent(I18n.get("gui.resourcefulbees.apiary.button.main_screen"));
                ApiaryBreederScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        });

        storageTabButton = this.addButton(new TabImageButton(t + 1, j + 37, 18, 18, 110, 0, 18, TABS_BG, new ItemStack(Items.HONEYCOMB), 2, 1,
                onPress -> this.changeScreen(ApiaryTabs.STORAGE), 128, 128) {

            @Override
            public void renderToolTip(@NotNull MatrixStack matrix,int mouseX, int mouseY) {
                StringTextComponent s = new StringTextComponent(I18n.get("gui.resourcefulbees.apiary.button.storage_screen"));
                ApiaryBreederScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        });

        this.addButton(new TabImageButton(t + 1, j + 57, 18, 18, 110, 0, 18, TABS_BG, new ItemStack(ModItems.GOLD_FLOWER_ITEM.get()), 1, 1,
                onPress -> this.changeScreen(ApiaryTabs.BREED), 128, 128) {

            @Override
            public void renderToolTip(@NotNull MatrixStack matrix, int mouseX, int mouseY) {
                StringTextComponent s = new StringTextComponent(I18n.get("gui.resourcefulbees.apiary.button.breed_screen"));
                ApiaryBreederScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        }).active = false;
    }

    private void changeScreen(ApiaryTabs tab) {
        switch (tab) {
            case BREED:
                break;
            case STORAGE:
                if (storageTabButton.active)
                    NetPacketHandler.sendToServer(new ApiaryTabMessage(getApiaryBreederTileEntity().getBlockPos(), ApiaryTabs.STORAGE));
                break;
            case MAIN:
                if (mainTabButton.active)
                    NetPacketHandler.sendToServer(new ApiaryTabMessage(getApiaryBreederTileEntity().getBlockPos(), ApiaryTabs.MAIN));
        }
    }

    @Override
    public void render(@NotNull MatrixStack matrix,int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrix, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        if (this.menu.isRebuild()) {
            preInit();
            init();
            this.menu.setRebuild(false);
        }

        mainTabButton.active = getApiaryBreederTileEntity().getApiary() != null;
        storageTabButton.active = getApiaryBreederTileEntity().getApiary() != null && getApiaryBreederTileEntity().getApiary().getStoragePos() != null;


        Minecraft client = this.minecraft;
        if (client != null) {
            client.getTextureManager().bind(BACKGROUND);
            int i = this.leftPos;
            int j = this.topPos;
            //upgrade slots
            blit(matrix, i, j+16, 0, 16, 25, 82);
            //Top of screen
            blit(matrix, i+25, j, 25, 0, 176, 15);
            //slots
            int scaledprogress;
            for (int z = 0; z < this.menu.getNumberOfBreeders(); z++){
                blit(matrix, i+25, j+ 15 + (z*20), 25, 15, 176, 20);
            }

            for (int k = 0; k < this.menu.getNumberOfBreeders(); k++) {
                scaledprogress = MathUtils.clamp(118 * this.menu.times.get(k) / this.menu.getApiaryBreederTileEntity().getTotalTime(), 0, this.menu.getApiaryBreederTileEntity().getTotalTime());
                blit(matrix, i+54, j + 21 + (k*20), 0, 246, scaledprogress, 10);
            }

            blit(matrix, i+25, j+15 + (20 * this.menu.getNumberOfBreeders()), 25, 95, 176, 95);

            int t = i + this.imageWidth - 24;
            this.minecraft.getTextureManager().bind(TABS_BG);
            blit(matrix, t -1, j + 12, 0,0, 25, 68, 128, 128);
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

    public ApiaryBreederTileEntity getApiaryBreederTileEntity() {
        return apiaryBreederTileEntity;
    }
}
