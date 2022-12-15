package com.teamresourceful.resourcefulbees.client.gui.screen.locator;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.item.locator.DimensionalBeeHolder;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.function.Consumer;

public class BeeListWidget extends ObjectSelectionList<BeeLocatorEntry> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/advancements/backgrounds/resourcefulbees.png");

    private final Consumer<BeeLocatorEntry> selector;

    public BeeListWidget(Consumer<BeeLocatorEntry> selector, Minecraft minecraft, int width, int height, int top, int bottom, int entryHeight) {
        super(minecraft, width, height, top, bottom, entryHeight);
        this.selector = selector;
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
    }

    @Override
    protected void renderBackground(@NotNull PoseStack stack) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(this.x0, this.y1, 0.0D).uv((float)this.x0 / 32.0F, (float)(this.y1 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        bufferbuilder.vertex(this.x1, this.y1, 0.0D).uv((float)this.x1 / 32.0F, (float)(this.y1 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        bufferbuilder.vertex(this.x1, this.y0, 0.0D).uv((float)this.x1 / 32.0F, (float)(this.y0 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        bufferbuilder.vertex(this.x0, this.y0, 0.0D).uv((float)this.x0 / 32.0F, (float)(this.y0 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
        tesselator.end();
    }

    @Override
    protected void renderDecorations(@NotNull PoseStack stack, int p_93444_, int p_93445_) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(519);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(this.x0, this.y0, -100.0D).uv(0.0F, (float)this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((this.x0 + this.width), this.y0, -100.0D).uv((float)this.width / 32.0F, (float)this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((this.x0 + this.width), 0.0D, -100.0D).uv((float)this.width / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex(this.x0, 0.0D, -100.0D).uv(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex(this.x0, this.height, -100.0D).uv(0.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((this.x0 + this.width), this.height, -100.0D).uv((float)this.width / 32.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((this.x0 + this.width), this.y1, -100.0D).uv((float)this.width / 32.0F, (float)this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex(this.x0, this.y1, -100.0D).uv(0.0F, (float)this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
        tesselator.end();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(this.x0, (this.y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
        bufferbuilder.vertex(this.x1, (this.y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
        bufferbuilder.vertex(this.x1, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex(this.x0, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex(this.x0, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex(this.x1, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex(this.x1, (this.y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
        bufferbuilder.vertex(this.x0, (this.y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
        tesselator.end();
    }

    public void updateEntries(BeeRegistry registry) {
        var level = this.minecraft.level;
        if (level == null) return;
        this.clearEntries();

        registry.getSetOfBees()
            .stream()
            .filter(bee -> DimensionalBeeHolder.DIMENSIONAL_BEES.get(level.dimension()).contains(bee.name()))
            .sorted(Comparator.comparing(CustomBeeData::name))
            .forEach(bee -> {
                Entity entity = bee.entityType().create(level);
                if (entity != null) this.addEntry(new BeeLocatorEntry(this.selector, entity, bee.displayName().copy()));
            });
    }
}
