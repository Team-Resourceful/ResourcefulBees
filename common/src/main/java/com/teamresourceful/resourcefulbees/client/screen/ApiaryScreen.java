package com.teamresourceful.resourcefulbees.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.common.blockentities.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.base.BlockBee;
import com.teamresourceful.resourcefulbees.common.items.BeeJarItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.menus.ApiaryMenu;
import com.teamresourceful.resourcefulbees.mixin.client.ScreenAccessor;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ApiaryScreen extends AbstractContainerScreen<ApiaryMenu> {

    private static final ResourceLocation VALIDATED_TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/apiary/validated.png");
    private int beeIndexOffset;
    private float sliderProgress;
    private boolean clickedOnScroll;
    private final ApiaryBlockEntity apiaryBlockEntity;

    public ApiaryScreen(ApiaryMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.imageWidth = 224;
        this.imageHeight = 168;
        apiaryBlockEntity = this.menu.getEntity();
    }

    @Override
    public void render(@NotNull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (apiaryBlockEntity != null) {
            if (canScroll()) {
                if (beeIndexOffset + 7 >= apiaryBlockEntity.beeCount()) {
                    beeIndexOffset = Math.max(0, apiaryBlockEntity.beeCount() - 7);
                }
            }else {
                beeIndexOffset = 0;
            }
            this.renderBackground(matrix);
            super.render(matrix, mouseX, mouseY, partialTicks);
            this.renderTooltip(matrix, mouseX, mouseY);
            int l = this.leftPos + 5;
            int i1 = this.topPos + 34;
            int j1 = this.beeIndexOffset + 7;
            renderBeeToolTip(matrix, mouseX, mouseY, l, i1, j1);
        }
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
        Minecraft client = this.minecraft;
        if (client != null && apiaryBlockEntity != null) {
            RenderUtils.bindTexture(VALIDATED_TEXTURE);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
            if (!this.canScroll()) {
                this.sliderProgress = 0;
            }
            int k = (int) (99.0F * this.sliderProgress);
            this.blit(matrix, i + 44, j + 34 + k, 54 + (this.canScroll() ? 0 : 6), imageHeight, 6, 27);
            int l = this.leftPos + 5;
            int i1 = this.topPos + 34;
            int j1 = this.beeIndexOffset + 7;
            this.drawRecipesBackground(matrix, mouseX, mouseY, l, i1, j1);
            this.drawBees(l, i1, j1);
        }
    }

    @Override
    protected void renderLabels(@NotNull PoseStack matrix, int mouseX, int mouseY) {
        String s = String.format("(%1$s/%2$s)", apiaryBlockEntity.beeCount(), apiaryBlockEntity.getTier().maxBees());
        this.font.draw(matrix, s, 4, 17, 0x404040);
        this.font.draw(matrix, getTitle(), 55, 7, 0x404040);
        this.font.draw(matrix, TranslationConstants.Guis.INVENTORY, 55, 75, 0x404040);

        for (Widget widget : ((ScreenAccessor)this).getRenderables()) {
            if (widget instanceof AbstractWidget aWidget && aWidget.isMouseOver(mouseX, mouseY)) {
                aWidget.renderToolTip(matrix, mouseX - this.leftPos, mouseY - this.topPos);
                break;
            }
        }
    }

    private void renderBeeToolTip(@NotNull PoseStack matrix, int mouseX, int mouseY, int left, int top, int beeIndexOffsetMax) {
        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryBlockEntity.beeCount(); ++i) {
            int j = i - this.beeIndexOffset;
            int i1 = top + j * 18;

            if (mouseX >= left && mouseY >= i1 && mouseX < left + 18 && mouseY < i1 + 18) {
                List<Component> beeInfo = new ArrayList<>();
                BlockBee apiaryBee = this.menu.getApiaryBee(i);

                int ticksInHive = apiaryBee.getTicksInHive();
                beeInfo.add(apiaryBee.displayName);
                beeInfo.add(Component.translatable(TranslationConstants.Apiary.TICKS_HIVE, ticksInHive));
                beeInfo.add(Component.translatable(TranslationConstants.Apiary.TICKS_LEFT, Math.max(apiaryBee.minOccupationTicks - ticksInHive, 0)));
                this.renderComponentTooltip(matrix, beeInfo, mouseX, mouseY);
            }
        }
    }

    private void drawRecipesBackground(@NotNull PoseStack matrix, int mouseX, int mouseY, int left, int top, int beeIndexOffsetMax) {

        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryBlockEntity.beeCount(); ++i) {
            int j = i - this.beeIndexOffset;
            int k = left;
            int i1 = top + j * 18;
            int j1 = this.imageHeight;
            if (mouseX >= k && mouseY >= i1 && mouseX < k + 18 && mouseY < i1 + 18) {
                j1 += 18;
            }
            this.blit(matrix, k, i1, 0, j1, 18, 18);
            int l1 = 18;
            k = k + 18;
            j1 = this.imageHeight;
            if (this.menu.getApiaryBee(i).isLocked()) {
                l1 += 18;
            }
            if (mouseX >= k && mouseY >= i1 && mouseX < k + 18 && mouseY < i1 + 18) {
                j1 += 18;
            }

            this.blit(matrix, k, i1, l1, j1, 18, 18);
        }

    }

    private void drawBees(int left, int top, int beeIndexOffsetMax) {
        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryBlockEntity.beeCount(); ++i) {
            int j = i - this.beeIndexOffset;
            int i1 = top + j * 18 + 2;

            BlockBee bee = this.menu.getApiaryBee(i);
            CompoundTag entityTag = bee.entityData.copy();
            entityTag.putString(NBTConstants.BeeJar.COLOR, bee.color);

            ItemStack beeJar = BeeJarItem.createFilledJar(entityTag, null);
            this.itemRenderer.renderAndDecorateItem(beeJar, left, i1);
        }
    }

    private boolean canScroll() {
        return apiaryBlockEntity.beeCount() > 7;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (this.canScroll()) {
            int i = this.getHiddenRows();
            this.sliderProgress = (float) (this.sliderProgress - scrollAmount / i);
            this.sliderProgress = Mth.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.beeIndexOffset = (int) ((this.sliderProgress * i) + 0.5D);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int pMouseDragged5, double pMouseDragged6, double pMouseDragged8) {
        if (this.clickedOnScroll && this.canScroll()) {
            int i = this.topPos + 14;
            int j = i + 101;
            this.sliderProgress = ((float) mouseY - i - 7.5F) / ((j - i) - 15.0F);
            this.sliderProgress = Mth.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.beeIndexOffset = (int) ( (this.sliderProgress * this.getHiddenRows()) + 0.5D);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, pMouseDragged5, pMouseDragged6, pMouseDragged8);
    }

    private int getHiddenRows() { return apiaryBlockEntity.beeCount() - 7; }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int pMouseClicked5) {
        this.clickedOnScroll = false;
        if (apiaryBlockEntity.beeCount() > 0) {
            int i = this.leftPos + 5;
            int j = this.topPos + 34;
            int k = this.beeIndexOffset + 7;

            for (int l = this.beeIndexOffset; l < k; ++l) {
                int i1 = l - this.beeIndexOffset;
                double d0 = mouseX - (i);
                double d1 = mouseY - (j + i1 * 18);
                if (d0 >= 18.0D && d1 >= 0.0D && d0 <= 36.0D && d1 <= 18.0D) {
                    this.menu.lockOrUnlockBee(l);
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }

            i = this.leftPos + 44;
            j = this.topPos + 34 + ((int) (99.0F * this.sliderProgress));

            if (mouseX >= i && mouseX < (i + 6) && mouseY >= j && mouseY <= (j + 27)) {
                this.clickedOnScroll = true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, pMouseClicked5);
    }
}
