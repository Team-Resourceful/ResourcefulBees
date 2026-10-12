package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.component.ApiaryToggleWidget;
import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.blockentities.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.components.ApiaryOccupant;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.ApiaryTranslations;
import com.teamresourceful.resourcefulbees.common.menus.ApiaryMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

public class ApiaryScreen extends BaseScreen<ApiaryMenu> {

    private static final Identifier BACKGROUND = ModIdentifier.of("apiary/background");
    private static final Identifier SCROLLBAR = ModIdentifier.of("apiary/scrollbar");

    private int beeIndexOffset;
    private float sliderProgress;
    private boolean clickedOnScroll;
    private final ApiaryBlockEntity apiaryBlockEntity;
    private final Map<Integer, BeePreview> previewEntities = new HashMap<>();
    private record BeePreview(ApiaryOccupant occupant, Entity entity) {}

    public ApiaryScreen(ApiaryMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        apiaryBlockEntity = this.menu.getEntity();
        titleLabelX = 7;
        inventoryLabelX = 37;
        inventoryLabelY = 159;
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        if (apiaryBlockEntity != null) {
            int i = this.leftPos;
            int j = this.topPos;
            // Background
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 256, 256, 0, 0, i, j, 242, 256);
            if (!this.canScroll()) {
                this.sliderProgress = 0;
            }
            // RID: Changed from 99.0F to 105.0F to expand the scrollbar further.
            int k = (int) (105.0F * this.sliderProgress);
            // Scrollbar
            // RID: Moved the scrollbar into its own sprite and have it expand horizontally instead.
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLBAR, 60, 60, 48, 0, i + 86 + k, j + 63, 11, 8);
            int beeLeft = this.leftPos + 37;
            int beeTop = this.topPos + 26;
            int beeIndexOffsetMax = this.beeIndexOffset + 7;
            this.drawRecipesBackground(beeLeft, beeTop, beeIndexOffsetMax);
            this.renderBeeToolTip(graphics, mouseX, mouseY, beeLeft, beeTop, beeIndexOffsetMax);
        }
    }

    // RID: Moved the Bee render to extractRenderState so it renders on top of the widget.
    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        if (apiaryBlockEntity != null) {
            drawBees(graphics, this.leftPos + 37, this.topPos + 26,this.beeIndexOffset + 7, mouseX, mouseY);
        }

    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        if (apiaryBlockEntity == null) {
            return;
        }

//        String beeCount = "(%1$s/%2$s)".formatted(apiaryBlockEntity.beeCount(), apiaryBlockEntity.getTier().maxBees());
//        graphics.text(this.font, beeCount, 4, 17, 0xff404040, false);
        graphics.text(this.font, "%02d".formatted(apiaryBlockEntity.beeCount()), 36, 64, 0xFF995b35, false);
        graphics.text(this.font, "%02d".formatted(apiaryBlockEntity.beeCount()), 36, 63, 0xFF4f312b, false);
        graphics.text(this.font, "%02d".formatted(apiaryBlockEntity.getTier().maxBees()), 53, 64, 0xFF995b35, false);
        graphics.text(this.font, "%02d".formatted(apiaryBlockEntity.getTier().maxBees()), 53, 63, 0xFF4f312b, false);
    }

    private void renderBeeToolTip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int left, int top, int beeIndexOffsetMax) {
        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryBlockEntity.beeCount(); ++i) {
            int index = i - this.beeIndexOffset;
            int x = left + index * 24;

            if (mouseX >= x && mouseY >= top && mouseX < x + 24 && mouseY < top + 28) {
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

    private void drawRecipesBackground(int left, int top, int beeIndexOffsetMax) {
        clearWidgets();
        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryBlockEntity.beeCount(); ++i) {
            var bee = this.menu.getApiaryBee(i);

            int finalI = i;
            addRenderableWidget(new ApiaryToggleWidget(left + (finalI - this.beeIndexOffset) * 24, top, 0, 0, 24, 28,
                    bee.locked(),
                    SCROLLBAR) {
                @Override
                public void setLocked(boolean locked) {
                    super.setLocked(locked);
                    menu.lockOrUnlockBee(finalI);
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }
            });

        }

    }

    private void drawBees(GuiGraphicsExtractor graphics, int left, int top, int beeIndexOffsetMax, int mouseX, int mouseY) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            return;
        }

        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryBlockEntity.beeCount(); ++i) {
            int index = i - this.beeIndexOffset;
            int x = left + index * 24 + 4;
            boolean hovered = mouseX >= x && mouseX < x + 24 && mouseY >= top && mouseY < top + 28;


            var apiaryBee = this.menu.getApiaryBee(i);
            boolean locked = apiaryBee.locked();

            int beeY = top + (locked
                    ? (hovered ? 7 : 6)   // Hover Locked / Locked
                    : (hovered ? 6 : 4)); // Hovered

            // RID: Switched to using Bee Occupant to render the bee to avoid needing to close the GUI to update its occupants.
            BeePreview preview = previewEntities.get(i);
            if (preview == null || !preview.occupant().entityData().equals(apiaryBee.entityData())) {
                Entity entity = apiaryBee.createEntity(minecraft.level, BlockPos.ZERO);
                if (entity == null) {
                    previewEntities.remove(i);
                    continue;
                }

                ClientRenderUtils.preparePreviewEntity(entity);
                preview = new BeePreview(apiaryBee, entity);
                previewEntities.put(i, preview);
            }

            ClientRenderUtils.renderEntity(
                    graphics,
                    preview.entity(),
                    x,
                    beeY,
                    16,
                    16,
                    180f,
                    0.65f
            );
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
            int i = this.leftPos + 44;
            int j = this.topPos + 34 + ((int) (99.0F * this.sliderProgress));

            if (mouseX >= i && mouseX < (i + 6) && mouseY >= j && mouseY <= (j + 27)) {
                this.clickedOnScroll = true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }
}
