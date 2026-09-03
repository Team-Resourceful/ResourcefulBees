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
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiaryScreen extends AbstractContainerScreen<ApiaryMenu> {

    private static final Identifier VALIDATED_TEXTURE = ModIdentifier.of("textures/gui/apiary/validated.png");

    private final Map<Integer, Entity> previewEntities = new HashMap<>();
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
            int beeLeft = this.leftPos + 5;
            int beeTop = this.topPos + 34;
            int beeIndexOffsetMax = this.beeIndexOffset + 7;
            this.drawRecipesBackground(graphics, mouseX, mouseY, beeLeft, beeTop, beeIndexOffsetMax);
            this.drawBees(graphics, beeLeft, beeTop, beeIndexOffsetMax);
            renderBeeToolTip(graphics, mouseX, mouseY, beeLeft, beeTop, beeIndexOffsetMax);
        }
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (apiaryBlockEntity == null) {
            return;
        }

        String beeCount = "(%1$s/%2$s)".formatted(apiaryBlockEntity.beeCount(), apiaryBlockEntity.getTier().maxBees());
        graphics.text(this.font, beeCount, 4, 17, 0xff404040, false);
        graphics.text(this.font, getTitle(), 55, 7, 0xff404040, false);
        graphics.text(this.font, this.playerInventoryTitle, 55, 75, 0xff404040, false);
    }

    private void renderBeeToolTip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int left, int top, int beeIndexOffsetMax) {
        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryBlockEntity.beeCount(); ++i) {
            int index = i - this.beeIndexOffset;
            int y = top + index * 18;

            if (mouseX >= left && mouseY >= y && mouseX < left + 18 && mouseY < y + 18) {
                var apiaryBee = this.menu.getApiaryBee(i);
                int ticksInHive = apiaryBee.ticksInHive();

                List<FormattedCharSequence> beeInfo = List.of(
                        apiaryBee.displayName().getVisualOrderText(),
                        Component.translatable(ApiaryTranslations.TICKS_HIVE, ticksInHive).getVisualOrderText(),
                        Component.translatable(ApiaryTranslations.TICKS_LEFT, Math.max(apiaryBee.minOccupationTicks() - ticksInHive, 0)).getVisualOrderText()
                );

                graphics.setTooltipForNextFrame(beeInfo, mouseX, mouseY);
                return;
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
        }

    }

    private void drawBees(GuiGraphicsExtractor graphics, int left, int top, int beeIndexOffsetMax) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            return;
        }

        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryBlockEntity.beeCount(); ++i) {
            int index = i - this.beeIndexOffset;
            int y = top + index * 18 + 2;

            var apiaryBee = this.menu.getApiaryBee(i);

            Entity entity = previewEntities.computeIfAbsent(i, ignored -> {
                Entity created = apiaryBee.createEntity(
                        minecraft.level,
                        BlockPos.ZERO
                );

                if (created != null) {
                    ClientRenderUtils.preparePreviewEntity(created);
                }

                return created;
            });

            if (entity != null) {
                ClientRenderUtils.renderEntity(
                        graphics,
                        entity,
                        left,
                        y,
                        16,
                        16,
                        180f,
                        0.85f
                );
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
