package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.container.ApiaryBreederContainer;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryBreederTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ApiaryBreederScreen extends ContainerScreen<ApiaryBreederContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_breeder_gui.png");
    private static final ResourceLocation TABS_BG = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_gui_tabs.png");

    private final ApiaryBreederTileEntity apiaryBreederTileEntity;

    public ApiaryBreederScreen(ApiaryBreederContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        apiaryBreederTileEntity = this.menu.getApiaryBreederTileEntity();
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
    }

    @Override
    public void render(@NotNull MatrixStack matrix,int mouseX, int mouseY, float partialTicks) {
        if (getApiaryBreederTileEntity() != null) {
            this.renderBackground(matrix);
            super.render(matrix, mouseX, mouseY, partialTicks);
            this.renderTooltip(matrix, mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        if (this.menu.isRebuild()) {
            preInit();
            init();
            this.menu.setRebuild(false);
        }


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
            int scaledProgress;
            for (int z = 0; z < this.menu.getNumberOfBreeders(); z++){
                blit(matrix, i+25, j+ 15 + (z*20), 25, 15, 176, 20);
            }

            for (int k = 0; k < this.menu.getNumberOfBreeders(); k++) {
                scaledProgress = MathHelper.clamp(118 * this.menu.times.get(k) / this.menu.getApiaryBreederTileEntity().getTotalTime(), 0, this.menu.getApiaryBreederTileEntity().getTotalTime());
                blit(matrix, i+54, j + 21 + (k*20), 0, 246, scaledProgress, 10);
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
