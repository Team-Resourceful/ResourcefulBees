package com.teamresourceful.resourcefulbees.client.screen.beepedia.component.bee;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.FamilyUnit;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class BeeBreedRow extends AbstractWidget {

    private static final int HEIGHT = 48;
    private static final int PREVIEW_SIZE = 28;

    private final FamilyUnit family;

    @Nullable
    private final Entity parent1;

    @Nullable
    private final Entity parent2;

    @Nullable
    private final Entity child;

    private final CustomBeeData parent1Data;
    private final CustomBeeData parent2Data;
    private final CustomBeeData childData;

    public BeeBreedRow(FamilyUnit family, int width) {
        super(0, 0, width, HEIGHT, Component.empty());

        this.family = family;

        var registry = BeeRegistry.get();

        parent1Data = registry.getBeeData(family.getParents().getParent1());
        parent2Data = registry.getBeeData(family.getParents().getParent2());
        childData = family.getChildData();

        this.parent1 = createPreview(parent1Data);
        this.parent2 = createPreview(parent2Data);
        this.child = createPreview(childData);
    }

    @Nullable
    private static Entity createPreview(CustomBeeData bee) {
        var level = Minecraft.getInstance().level;

        if (level == null) {
            return null;
        }

        Entity entity = bee.entityType().create(level, EntitySpawnReason.COMMAND);

        if (entity != null) {
            ClientRenderUtils.preparePreviewEntity(entity);
        }

        return entity;
    }

    @Override
    protected void extractWidgetRenderState(
            @NonNull GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        int parent1Center = getX() + getWidth() / 6;
        int parent2Center = getX() + getWidth() * 3 / 6;
        int childCenter = getX() + getWidth() * 5 / 6;
        int previewY = getY();

        renderBee(graphics, parent1, parent1Center, previewY);
        renderBee(graphics, parent2, parent2Center, previewY);
        renderBee(graphics, child, childCenter, previewY);

        // + between the parents
        drawCenteredText(graphics, Component.literal("+"), (parent1Center + parent2Center) / 2, getY() + 10);

        // → between parent 2 and child
        drawCenteredText(graphics, Component.literal("→"), (parent2Center + childCenter) / 2, getY() + 10);

        drawCenteredText(graphics, Component.literal(Math.round(family.chance() * 100.0) + "%"), childCenter, getY() + PREVIEW_SIZE + 2);

        if (isOverPreview(mouseX, mouseY, parent1Center)) {
            graphics.setTooltipForNextFrame(parent1Data.displayName(), mouseX, mouseY);
        } else if (isOverPreview(mouseX, mouseY, parent2Center)) {
            graphics.setTooltipForNextFrame(parent2Data.displayName(), mouseX, mouseY);
        } else if (isOverPreview(mouseX, mouseY, childCenter)) {
            graphics.setTooltipForNextFrame(childData.displayName(), mouseX, mouseY);
        }
    }

    private static void renderBee(GuiGraphicsExtractor graphics, @Nullable Entity entity, int centerX, int y) {
        if (entity == null) {
            return;
        }

        ClientRenderUtils.renderEntity(graphics, entity, centerX - PREVIEW_SIZE / 2, y, PREVIEW_SIZE, PREVIEW_SIZE, -135.0F, 0.45F);
    }

    private boolean isOverPreview(int mouseX, int mouseY, int centerX) {
        int left = centerX - PREVIEW_SIZE / 2;

        return mouseX >= left
                && mouseX < left + PREVIEW_SIZE
                && mouseY >= getY()
                && mouseY < getY() + PREVIEW_SIZE;
    }

    private static void drawCenteredText(GuiGraphicsExtractor graphics, Component text, int centerX, int y) {
        var font = Minecraft.getInstance().font;

        graphics.text(font, text, centerX - font.width(text) / 2, y, 0xFFFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE,
                Component.empty()
                        .append(parent1Data.displayName())
                        .append(" + ")
                        .append(parent2Data.displayName())
                        .append(" → ")
                        .append(childData.displayName())
                        .append(", ")
                        .append(Math.round(family.chance() * 100.0) + "%")
        );
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        return false;
    }
}
