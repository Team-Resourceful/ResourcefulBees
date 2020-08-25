package com.dungeonderps.resourcefulbees.client.gui.screen;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.client.gui.widget.TabImageButton;
import com.dungeonderps.resourcefulbees.container.ApiaryBreederContainer;
import com.dungeonderps.resourcefulbees.lib.ApiaryTabs;
import com.dungeonderps.resourcefulbees.network.NetPacketHandler;
import com.dungeonderps.resourcefulbees.network.packets.ApiaryTabMessage;
import com.dungeonderps.resourcefulbees.tileentity.ApiaryBreederTileEntity;
import com.dungeonderps.resourcefulbees.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ApiaryBreederScreen extends ContainerScreen<ApiaryBreederContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_breeder_gui.png");
    private static final ResourceLocation TABS_BG = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_gui_tabs.png");

    public ApiaryBreederTileEntity apiaryBreederTileEntity;

    private TabImageButton mainTabButton;
    private TabImageButton storageTabButton;


    public ApiaryBreederScreen(ApiaryBreederContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);

        preInit();
    }

    protected void preInit(){
        this.xSize = 226;
        this.ySize = 110 + this.container.numberOfBreeders * 20;
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();

        apiaryBreederTileEntity = this.container.apiaryBreederTileEntity;

        int i = this.guiLeft;
        int j = this.guiTop;
        int t = i + this.xSize - 24;

        mainTabButton = this.addButton(new TabImageButton(t+1, j+17, 18, 18, 74, 0, 18, TABS_BG,
                (onPress) -> this.changeScreen(ApiaryTabs.MAIN)) {
            public void renderToolTip(int mouseX, int mouseY) {
                String s = I18n.format("gui.resourcefulbees.apiary.button.main_screen");
                ApiaryBreederScreen.this.renderTooltip(s, mouseX, mouseY);
            }
        });

        storageTabButton = this.addButton(new TabImageButton(t + 1, j + 37, 18, 18, 110, 0, 18, TABS_BG,
                (onPress) -> this.changeScreen(ApiaryTabs.STORAGE)) {
            public void renderToolTip(int mouseX, int mouseY) {
                String s = I18n.format("gui.resourcefulbees.apiary.button.storage_screen");
                ApiaryBreederScreen.this.renderTooltip(s, mouseX, mouseY);
            }
        });

        this.addButton(new TabImageButton(t + 1, j + 57, 18, 18, 92, 0, 18, TABS_BG,
                (onPress) -> this.changeScreen(ApiaryTabs.BREED)) {
            public void renderToolTip(int mouseX, int mouseY) {
                String s = I18n.format("gui.resourcefulbees.apiary.button.breed_screen");
                ApiaryBreederScreen.this.renderTooltip(s, mouseX, mouseY);
            }
        }).active = false;
    }

    private void changeScreen(ApiaryTabs tab) {
        switch (tab) {
            case BREED:
                break;
            case STORAGE:
                if (storageTabButton.active)
                    NetPacketHandler.sendToServer(new ApiaryTabMessage(apiaryBreederTileEntity.getPos(), ApiaryTabs.STORAGE));
                break;
            case MAIN:
                if (mainTabButton.active)
                    NetPacketHandler.sendToServer(new ApiaryTabMessage(apiaryBreederTileEntity.getPos(), ApiaryTabs.MAIN));
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (this.container.rebuild) {
            preInit();
            init();
            this.container.rebuild = false;
        }

        mainTabButton.active = apiaryBreederTileEntity.getApiary() != null;
        storageTabButton.active = apiaryBreederTileEntity.getApiary() != null && apiaryBreederTileEntity.getApiary().storagePos != null;


        Minecraft client = this.minecraft;
        if (client != null) {
            client.getTextureManager().bindTexture(BACKGROUND);
            int i = this.guiLeft;
            int j = this.guiTop;
            //upgrade slots
            blit(i, j+16, 0, 16, 25, 82);
            //Top of screen
            blit(i+25, j, 25, 0, 176, 15);
            //slots
            int scaledprogress;
            for (int z=0; z < this.container.numberOfBreeders; z++){
                blit(i+25, j+ 15 + (z*20), 25, 15, 176, 20);
            }

            for (int k = 0; k < this.container.numberOfBreeders; k++) {
                scaledprogress = MathUtils.clamp(118 * this.container.times.get(k) / this.container.apiaryBreederTileEntity.totalTime, 0, this.container.apiaryBreederTileEntity.totalTime);
                blit(i+54, j + 21 + (k*20), 0, 246, scaledprogress, 10);
            }

            blit(i+25, j+15 + (20 * this.container.numberOfBreeders), 25, 95, 176, 95);

            int t = i + this.xSize - 24;
            this.minecraft.getTextureManager().bindTexture(TABS_BG);
            blit(t -1, j + 12, 0,0, 25, 68, 128, 128);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        for (Widget widget : this.buttons) {
            if (widget.isHovered()) {
                widget.renderToolTip(mouseX - this.guiLeft, mouseY - this.guiTop);
                break;
            }
        }
    }
}
