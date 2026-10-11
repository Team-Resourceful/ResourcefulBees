package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.component.ApiaryToggleWidget;
import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.blockentities.ApiaryBlockEntity;
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

    private final Map<Integer, Entity> previewEntities = new HashMap<>();
    private int beeIndexOffset;
    private float sliderProgress;
    private boolean clickedOnScroll;
    private final ApiaryBlockEntity apiaryBlockEntity;

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
            int beeLeft = this.leftPos + 35;
            int beeTop = this.topPos + 26;
            int beeIndexOffsetMax = this.beeIndexOffset + 7;
            this.drawRecipesBackground(beeLeft, beeTop, beeIndexOffsetMax);
            this.drawBees(graphics, beeLeft, beeTop, beeIndexOffsetMax);
            this.renderBeeToolTip(graphics, mouseX, mouseY, beeLeft, beeTop, beeIndexOffsetMax);
        }
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        if (apiaryBlockEntity == null) {
            return;
        }

        String beeCount = "(%1$s/%2$s)".formatted(apiaryBlockEntity.beeCount(), apiaryBlockEntity.getTier().maxBees());
        graphics.text(this.font, beeCount, 4, 17, 0xff404040, false);
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
                        Component.translatable(ApiaryTranslations.TICKS_LEFT, Math.max(apiaryBee.minOccupationTicks() - ticksInHive, 0)).getVisualOrderText(),
                        Component.translatable("is Locked: " + apiaryBee.locked()).getVisualOrderText() // RID: For testing purposes - TO BE REMOVED
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
            addRenderableWidget(new ApiaryToggleWidget(left + finalI * 24, top, 0, 0, 24, 28,
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

    private void drawBees(GuiGraphicsExtractor graphics, int left, int top, int beeIndexOffsetMax) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            return;
        }

        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryBlockEntity.beeCount(); ++i) {
            int index = i - this.beeIndexOffset;
            int x = left + index * 24 + 8;

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
                        x,
                        top + 4,
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
            int i = this.leftPos + 24;
            int j = this.topPos;
            int k = this.beeIndexOffset + 7;

//            for (int l = this.beeIndexOffset; l < k; ++l) {
//                int i1 = l - this.beeIndexOffset;
//                double d0 = mouseX - (i);
//                double d1 = mouseY - (j + i1 * 18);
//                if (d0 >= 24.0D && d1 >= 0.0D && d0 <= 48.0D && d1 <= 24.0D) {
//                    this.menu.lockOrUnlockBee(l);
//                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
//                    return true;
//                }
//            }

            i = this.leftPos + 44;
            j = this.topPos + 34 + ((int) (99.0F * this.sliderProgress));

            if (mouseX >= i && mouseX < (i + 6) && mouseY >= j && mouseY <= (j + 27)) {
                this.clickedOnScroll = true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }
}
