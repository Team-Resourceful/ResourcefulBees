package com.dungeonderps.resourcefulbees.client.gui.screen;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.container.ValidatedApiaryContainer;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.Arrays;

public class ValidatedApiaryScreen extends ContainerScreen<ValidatedApiaryContainer> {

    private final ResourceLocation VALIDATED_TEXTURE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/validated.png");
    private int recipeIndexOffset;

    public ValidatedApiaryScreen(ValidatedApiaryContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        super.render(p_render_1_, p_render_2_, p_render_3_);
        this.renderHoveredToolTip(p_render_1_, p_render_2_);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft client = this.minecraft;
        if (client != null) {
            this.minecraft.getTextureManager().bindTexture(VALIDATED_TEXTURE);
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            this.blit(i, j, 0, 0, this.xSize, this.ySize);

        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.drawRecipesBackground(mouseX, mouseY, this.guiLeft + 20, this.guiTop + 20, 1);
        this.drawRecipesItems(this.guiLeft + 20, this.guiTop + 20, 1);
    }

    private void drawRecipesBackground(int mouseX, int mouseY, int left, int top, int recipeIndexOffsetMax) {
        for(int i = this.recipeIndexOffset; i < recipeIndexOffsetMax && i < this.container.apiaryTileEntity.bees.size(); ++i) {
            int j = i - this.recipeIndexOffset;
            int k = left + j % 4 * 16;
            int l = j / 4;
            int i1 = top + l * 18 + 2;
            int j1 = this.ySize;
            if (i == this.container.selectedBeeType) {
                j1 += 18;
            } else if (mouseX >= k && mouseY >= i1 && mouseX < k + 16 && mouseY < i1 + 18) {
                j1 += 36;
            }

            this.blit(k, i1 - 1, 0, j1, 16, 18);
        }

    }

    private void drawRecipesItems(int left, int top, int recipeIndexOffsetMax) {
        String[] list = Arrays.copyOf(this.container.apiaryTileEntity.bees.keySet().toArray(), this.container.apiaryTileEntity.bees.size(), String[].class);

        for(int i = this.recipeIndexOffset; i < recipeIndexOffsetMax && i < this.container.apiaryTileEntity.bees.size(); ++i) {
            int j = i - this.recipeIndexOffset;
            int k = left + j % 4 * 16;
            int l = j / 4;
            int i1 = top + l * 18 + 2;
            ItemStack beeJar = new ItemStack(RegistryHandler.BEE_JAR.get());
            CompoundNBT data = new CompoundNBT();
            data.putString(BeeConst.NBT_ENTITY, "resourcefulbees:bee");
            data.putString(BeeConst.NBT_BEE_TYPE, list[i]);
            data.putString(BeeConst.NBT_COLOR, BeeInfo.getInfo(list[i]).getPrimaryColor());
            beeJar.setTag(data);
            if (this.minecraft != null)
            this.minecraft.getItemRenderer().renderItemAndEffectIntoGUI(beeJar, k, i1);
        }
    }
}
