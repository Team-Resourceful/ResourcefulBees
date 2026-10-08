package com.teamresourceful.resourcefulbees.client.screen.beepedia.component.bee;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import earth.terrarium.olympus.client.components.base.BaseWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BeePreviewWidget extends BaseWidget {

    private static final float ROTATION = -135.0F;
    private static final float SCALE = 0.65F;

    @Nullable
    private final Entity entity;

    public BeePreviewWidget(CustomBeeData bee, int width, int height) {
        super(width, height);

        var level = Minecraft.getInstance().level;

        this.entity = level == null ? null : bee.entityType().create(level, EntitySpawnReason.COMMAND);

        if (this.entity != null) {
            ClientRenderUtils.preparePreviewEntity(this.entity);
        }
    }

    @Override
    protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (this.entity == null) {
            return;
        }

        ClientRenderUtils.renderEntity(graphics, this.entity, getX(), getY(), getWidth(), getHeight(), ROTATION, SCALE);
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubleClick) {
        return false;
    }
}