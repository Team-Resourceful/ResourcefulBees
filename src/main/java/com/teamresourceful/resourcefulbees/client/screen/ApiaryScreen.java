package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.blockentities.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.ApiaryTranslations;
import com.teamresourceful.resourcefulbees.common.menus.ApiaryMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ApiaryScreen extends AbstractContainerScreen<ApiaryMenu> {

    private static final Identifier VALIDATED_TEXTURE = ModIdentifier.of("textures/gui/apiary/validated.png");
    private int beeIndexOffset;
    private float sliderProgress;
    private boolean clickedOnScroll;
    private final ApiaryBlockEntity apiaryBlockEntity;

    public ApiaryScreen(ApiaryMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn, 224, 168);
        this.inventoryLabelX = 58;
        apiaryBlockEntity = this.menu.getEntity();
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        if (apiaryBlockEntity != null) {
            int i = this.leftPos;
            int j = this.topPos;
            graphics.blit(RenderPipelines.GUI_TEXTURED, VALIDATED_TEXTURE, i, j, 0f, 0f, this.imageWidth, this.imageHeight, 256, 256);
            if (!this.canScroll()) {
                this.sliderProgress = 0;
            }
            int k = (int) (99.0F * this.sliderProgress);
            graphics.blit(RenderPipelines.GUI_TEXTURED, VALIDATED_TEXTURE, i + 44, j + 34 + k, 54f + (this.canScroll() ? 0 : 6), imageHeight, 6, 27, 256, 256);
            int l = this.leftPos + 5;
            int i1 = this.topPos + 34;
            int j1 = this.beeIndexOffset + 7;
            this.drawRecipesBackground(graphics, mouseX, mouseY, l, i1, j1);
            this.drawBees(graphics, l, i1, j1);
        }
    }

    /*  @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (apiaryBlockEntity != null) {
            if (canScroll()) {
                if (beeIndexOffset + 7 >= apiaryBlockEntity.beeCount()) {
                    beeIndexOffset = Math.max(0, apiaryBlockEntity.beeCount() - 7);
                }
            } else {
                beeIndexOffset = 0;
            }
            this.renderBackground(graphics, mouseX, mouseY, partialTicks);
            super.render(graphics, mouseX, mouseY, partialTicks);
            this.renderTooltip(graphics, mouseX, mouseY);
            int l = this.leftPos + 5;
            int i1 = this.topPos + 34;
            int j1 = this.beeIndexOffset + 7;
            renderBeeToolTip(mouseX, mouseY, l, i1, j1);
        }
    }



    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        Minecraft client = this.minecraft;
        if (client != null && apiaryBlockEntity != null) {
            int i = this.leftPos;
            int j = this.topPos;
            graphics.blit(VALIDATED_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
            if (!this.canScroll()) {
                this.sliderProgress = 0;
            }
            int k = (int) (99.0F * this.sliderProgress);
            graphics.blit(VALIDATED_TEXTURE, i + 44, j + 34 + k, 54 + (this.canScroll() ? 0 : 6), imageHeight, 6, 27);
            int l = this.leftPos + 5;
            int i1 = this.topPos + 34;
            int j1 = this.beeIndexOffset + 7;
            this.drawRecipesBackground(graphics, mouseX, mouseY, l, i1, j1);
            this.drawBees(graphics, l, i1, j1);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        String s = String.format("(%1$s/%2$s)", apiaryBlockEntity.beeCount(), apiaryBlockEntity.getTier().maxBees());
        graphics.drawString(this.font, s, 4, 17, 0x404040, false);
        graphics.drawString(this.font, getTitle(), 55, 7, 0x404040, false);
        graphics.drawString(this.font, GuiTranslations.INVENTORY, 55, 75, 0x404040, false);
    }*/

    private void renderBeeToolTip(int mouseX, int mouseY, int left, int top, int beeIndexOffsetMax) {
        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryBlockEntity.beeCount(); ++i) {
            int j = i - this.beeIndexOffset;
            int i1 = top + j * 18;

            if (mouseX >= left && mouseY >= i1 && mouseX < left + 18 && mouseY < i1 + 18) {
                List<Component> beeInfo = new ArrayList<>();
                var apiaryBee = this.menu.getApiaryBee(i);

                int ticksInHive = apiaryBee.ticksInHive();
                beeInfo.add(apiaryBee.displayName());
                beeInfo.add(Component.translatable(ApiaryTranslations.TICKS_HIVE, ticksInHive));
                beeInfo.add(Component.translatable(ApiaryTranslations.TICKS_LEFT, Math.max(apiaryBee.minOccupationTicks() - ticksInHive, 0)));
                //ScreenUtils.setTooltip(beeInfo);
            }
        }
    }

    private void drawRecipesBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, int left, int top, int beeIndexOffsetMax) {
        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryBlockEntity.beeCount(); ++i) {
            var bee = this.menu.getApiaryBee(i);
            int index = i - this.beeIndexOffset;
            int x = left;
            int y = top + index * 18;
            int v = this.imageHeight;
            if (mouseX >= x && mouseY >= y && mouseX < x + 18 && mouseY < y + 18) {
                v += 18;
            }
            graphics.blit(RenderPipelines.GUI_TEXTURED, VALIDATED_TEXTURE, x, y, 0, v, 18, 18, 256, 256);
            int l1 = 18;
            x = x + 18;
            v = this.imageHeight;
            if (bee.locked()) {
                l1 += 18;
            }
            if (mouseX >= x && mouseY >= y && mouseX < x + 18 && mouseY < y + 18) {
                v += 18;
            }

            graphics.blit(RenderPipelines.GUI_TEXTURED, VALIDATED_TEXTURE, x, y, l1, v, 18, 18, 256, 256);

            if (mouseX >= x && mouseY >= y && mouseX < x + 18 && mouseY < y + 18) {
                // TODO test, should be switched to actual component in a list
                graphics.setTooltipForNextFrame(bee.displayName(), mouseX, mouseY);
            }
        }

    }

    private void drawBees(GuiGraphicsExtractor graphics, int left, int top, int beeIndexOffsetMax) {
        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryBlockEntity.beeCount(); ++i) {
            int j = i - this.beeIndexOffset;
            int i1 = top + j * 18 + 2;

            var bee = this.menu.getApiaryBee(i);

            if (Minecraft.getInstance().level != null) {
                var entity = bee.createEntity(Minecraft.getInstance().level, new BlockPos(0, 0, 0));
                if (entity != null) {
                    ClientRenderUtils.preparePreviewEntity(entity);
                    ClientRenderUtils.renderEntity(graphics, entity, left, i1, 16, 16, 180f, 1.0f);
                }
            }
        }
    }

    private boolean canScroll() {
        return apiaryBlockEntity.beeCount() > 7;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.canScroll()) {
            int i = this.getHiddenRows();
            this.sliderProgress = (float) (this.sliderProgress - scrollY / i);
            this.sliderProgress = Mth.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.beeIndexOffset = (int) ((this.sliderProgress * i) + 0.5D);
        }
        return true;
    }

    private int getHiddenRows() { return apiaryBlockEntity.beeCount() - 7; }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (this.clickedOnScroll && this.canScroll()) {
            int i = this.topPos + 28;
            int j = i + 101;
            this.sliderProgress = ((float) event.y() - i - 7.5F) / ((j - i) - 15.0F);
            this.sliderProgress = Mth.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.beeIndexOffset = (int) ( (this.sliderProgress * this.getHiddenRows()) + 0.5D);
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
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
        return super.mouseClicked(event, doubleClick);
    }
}
