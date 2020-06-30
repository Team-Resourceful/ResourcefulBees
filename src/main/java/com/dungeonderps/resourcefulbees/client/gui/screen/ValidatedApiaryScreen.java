package com.dungeonderps.resourcefulbees.client.gui.screen;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.container.ValidatedApiaryContainer;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.ApiaryTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import java.util.Arrays;

public class ValidatedApiaryScreen extends ContainerScreen<ValidatedApiaryContainer> {

    private static final ResourceLocation VALIDATED_TEXTURE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/validated.png");
    private int beeIndexOffset;

    private float sliderProgress;
    private boolean clickedOnScroll;

    private ApiaryTileEntity apiaryTileEntity;

    public ValidatedApiaryScreen(ValidatedApiaryContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.xSize = 224;
        this.ySize = 152;
    }

    @Override
    protected void init() {


        super.init();

        apiaryTileEntity = this.container.apiaryTileEntity;

        ResourcefulBees.LOGGER.debug(this.guiLeft);
        ResourcefulBees.LOGGER.debug(this.guiTop);
        ResourcefulBees.LOGGER.debug(this.xSize);
        ResourcefulBees.LOGGER.debug(this.ySize);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft client = this.minecraft;
        if (client != null) {
            this.minecraft.getTextureManager().bindTexture(VALIDATED_TEXTURE);
            int i = this.guiLeft;
            int j = this.guiTop;
            this.blit(i, j, 0, 0, this.xSize, this.ySize);
            if (!this.canScroll()) {
                this.sliderProgress = 0;
            }
            int k = (int)(101.0F * this.sliderProgress);
            this.blit(i + 44, j + 18 + k, 36 + (this.canScroll() ? 0 : 6), 152, 6, 27);
            int l = this.guiLeft + 5;
            int i1 = this.guiTop + 18;
            int j1 = this.beeIndexOffset + 7;
            this.drawRecipesBackground(mouseX, mouseY, l, i1, j1);
            this.drawRecipesItems(l, i1, j1);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.drawString(font, String.valueOf(apiaryTileEntity.getBeeCount()), 10, 5, 0xffffff);

    }

    private void drawRecipesBackground(int mouseX, int mouseY, int left, int top, int beeIndexOffsetMax) {

        for(int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryTileEntity.getBeeCount(); ++i) {
            int j = i - this.beeIndexOffset;
            int k = left;
            int l = j;
            int i1 = top + l * 18;
            int j1 = this.ySize;
            if (i == this.container.selectedBeeType) {
                j1 += 18;
            } else if (mouseX >= k && mouseY >= i1 && mouseX < k + 16 && mouseY < i1 + 18) {
                j1 += 36;
            }

            this.blit(k, i1, 0, j1, 18, 18);
        }

    }

    private void drawRecipesItems(int left, int top, int beeIndexOffsetMax) {
        String[] list = Arrays.copyOf(this.container.apiaryTileEntity.bees.keySet().toArray(), this.container.apiaryTileEntity.bees.size(), String[].class);

        for(int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < this.container.apiaryTileEntity.bees.size(); ++i) {
            int j = i - this.beeIndexOffset;
            int k = left;
            int l = j;
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

    private boolean canScroll() {
        return apiaryTileEntity.getBeeCount() > 7;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double p_mouseScrolled_5_) {
        if (this.canScroll()) {
            int i = this.getHiddenRows();
            this.sliderProgress = (float)((double)this.sliderProgress - p_mouseScrolled_5_ / (double)i);
            this.sliderProgress = MathHelper.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.beeIndexOffset = (int)((double)(this.sliderProgress * (float)i) + 0.5D) * 4;
        }

        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        if (this.clickedOnScroll && this.canScroll()) {
            int i = this.guiTop + 14;
            int j = i + 54;
            this.sliderProgress = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.sliderProgress = MathHelper.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.beeIndexOffset = (int)((double)(this.sliderProgress * (float)this.getHiddenRows()) + 0.5D);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
        }
    }

    private int getHiddenRows() {
        return apiaryTileEntity.getBeeCount() - 7;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int p_mouseClicked_5_) {
        this.clickedOnScroll = false;
        if (apiaryTileEntity.getBeeCount() > 0) {
            int i = this.guiLeft + 5;
            int j = this.guiTop + 18;
            int k = this.beeIndexOffset + 7;

            for(int l = this.beeIndexOffset; l < k; ++l) {
                int i1 = l - this.beeIndexOffset;
                double d0 = mouseX - (double)(i + i1);
                double d1 = mouseY - (double)(j + i1 * 18);
                if (d0 >= 0.0D && d1 >= 0.0D && d0 < 18.0D && d1 < 18.0D && this.container.enchantItem(this.minecraft.player, l)) {
                    Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    this.minecraft.playerController.sendEnchantPacket((this.container).windowId, l);
                    return true;
                }
            }

            i = this.guiLeft + 44;
            j = this.guiTop + 18;
            if (mouseX >= (double)i && mouseX < (double)(i + 12) && mouseY >= (double)j && mouseY < (double)(j + 54)) {
                this.clickedOnScroll = true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, p_mouseClicked_5_);
    }
}
