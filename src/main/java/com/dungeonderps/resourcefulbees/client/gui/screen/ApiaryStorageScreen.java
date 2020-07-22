package com.dungeonderps.resourcefulbees.client.gui.screen;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.container.ApiaryStorageContainer;
import com.dungeonderps.resourcefulbees.tileentity.ApiaryStorageTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ApiaryStorageScreen extends ContainerScreen<ApiaryStorageContainer> {

    private static final ResourceLocation BACKGROUND_1X9 = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_storage_9.png");
    private static final ResourceLocation BACKGROUND_3X9 = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_storage_27.png");
    private static final ResourceLocation BACKGROUND_6X9 = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_storage_54.png");
    private static final ResourceLocation BACKGROUND_9X9 = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_storage_81.png");
    private static final ResourceLocation BACKGROUND_9X12 = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_storage_108.png");

    public ApiaryStorageTileEntity apiaryStorageTileEntity;

    public ResourceLocation background;

    public ApiaryStorageScreen(ApiaryStorageContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);

        preInit();
    }

    protected void preInit(){
        this.xSize = 176;

        switch (this.container.numberOfSlots) {
            case 27:
                this.ySize = 168;
                background = BACKGROUND_3X9;
                break;
            case 54:
                this.ySize = 222;
                background = BACKGROUND_6X9;
                break;
            case 81:
                this.ySize = 276;
                background = BACKGROUND_9X9;
                break;
            case 108:
                this.xSize = 230;
                this.ySize = 276;
                background = BACKGROUND_9X12;
                break;
            default:
                this.ySize = 132;
                background = BACKGROUND_1X9;
        }
    }

    @Override
    protected void init() {
        super.init();

        apiaryStorageTileEntity = this.container.apiaryStorageTileEntity;
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

        Minecraft client = this.minecraft;
        if (client != null) {
            this.minecraft.getTextureManager().bindTexture(background);
            int i = this.guiLeft;
            int j = this.guiTop;
            blit(i, j, 0, 0, this.xSize, this.ySize, 384, 384);
        }
    }
}
