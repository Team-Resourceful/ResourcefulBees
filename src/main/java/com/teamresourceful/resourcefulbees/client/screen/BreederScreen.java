package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.screen.base.CustomContainerScreen;
import com.teamresourceful.resourcefulbees.common.blockentities.BreederBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.menus.BreederMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

public class BreederScreen extends CustomContainerScreen<BreederMenu, BreederBlockEntity> {

    private static final Identifier ACTIVE = ModIdentifier.of("breeder/active");
    private static final Identifier PROGRESS = ModIdentifier.of("breeder/progress");

    public BreederScreen(BreederMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn, 180, 256);

        this.background = ModIdentifier.of("textures/gui/containers/breeder.png");
        this.titleLabelX = 7;
        this.inventoryLabelX = 7;
        this.inventoryLabelY = 159;
    }

    @Override
    protected void extractSurface(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        var time = menu.times.get(0);
        var endTime = menu.endTimes.get(0);
        int width = (int)(((float)time / endTime) * 10);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS, 112, 61, width, 2);
        if (time > 0) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ACTIVE, 103, 60, 4, 4);
        }
    }
}
