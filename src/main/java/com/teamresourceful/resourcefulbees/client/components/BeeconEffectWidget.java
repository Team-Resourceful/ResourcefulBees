package com.teamresourceful.resourcefulbees.client.components;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefulbees.common.blockentity.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.BeeconChangeMessage;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

public class BeeconEffectWidget extends AbstractWidget {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/ender_beecon/ender_beecon.png");


    private final Screen screen;
    private final EnderBeeconBlockEntity tile;
    private final MobEffect effect;
    private final TextureAtlasSprite effectSprite;
    private boolean selected;

    public BeeconEffectWidget(Screen screen, int x, int y, MobEffect effect, EnderBeeconBlockEntity tile) {
        super(x, y, 88, 22, Component.literal("Beecon Effect Button"));
        this.screen = screen;
        this.tile = tile;
        this.effect = effect;
        this.effectSprite = Minecraft.getInstance().getMobEffectTextures().get(effect);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (MathUtils.inRangeInclusive((int) mouseX, x+60, x+85) && MathUtils.inRangeInclusive((int) mouseY, y+4, y+19)) {
            NetPacketHandler.sendToServer(new BeeconChangeMessage(
                    this.isSelected() ? BeeconChangeMessage.Option.EFFECT_OFF : BeeconChangeMessage.Option.EFFECT_ON,
                    MobEffect.getId(effect),
                    tile.getBlockPos()
                )
            );
        }
    }

    @Override
    public void renderButton(@NotNull PoseStack matrix, int mouseX, int mouseY, float pPartialTicks) {
        Minecraft mc = Minecraft.getInstance();
        ClientUtils.bindTexture(this.effectSprite.atlas().location());
        blit(matrix, this.x + 2, this.y + 2, this.getBlitOffset(), 18, 18, this.effectSprite);
        drawString(matrix, mc.font, Component.literal("x" + tile.getEffectValue(effect)), this.x+24, this.y+6, 14737632);

        boolean buttonHover = MathUtils.inRangeInclusive(mouseX, x+60, x+85) && MathUtils.inRangeInclusive(mouseY, y+4, y+19);

        ClientUtils.bindTexture(BACKGROUND);
        blit(matrix, this.x + 59, this.y + 3, isSelected() ? 0 : 26, buttonHover ? 216 : 200, 26, 16);
    }

    @Override
    public void renderToolTip(@NotNull PoseStack stack, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.screen == null) return;


        if (MathUtils.inRangeInclusive(mouseX, x+60, x+85) && MathUtils.inRangeInclusive(mouseY, y+4, y+19)) {
            this.screen.renderTooltip(stack, Component.literal(this.selected ? "Active" : "Not Active"), mouseX, mouseY);
        }

        if (MathUtils.inRangeInclusive(mouseX, x+4, x+22) && MathUtils.inRangeInclusive(mouseY, y+4, y+22)) {
            this.screen.renderTooltip(stack, effect.getDisplayName(), mouseX, mouseY);
        }

    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput output) {

    }
}
