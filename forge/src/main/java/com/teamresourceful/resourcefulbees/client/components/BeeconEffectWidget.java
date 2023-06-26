package com.teamresourceful.resourcefulbees.client.components;

import com.teamresourceful.resourcefulbees.common.blockentity.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeeconTranslations;
import com.teamresourceful.resourcefulbees.common.network.packets.client.BeeconChangePacket;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

public class BeeconEffectWidget extends AbstractWidget {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/ender_beecon/ender_beecon.png");


    private final EnderBeeconBlockEntity tile;
    private final MobEffect effect;
    private final TextureAtlasSprite effectSprite;
    private boolean selected;

    public BeeconEffectWidget(int x, int y, MobEffect effect, EnderBeeconBlockEntity tile) {
        super(x, y, 88, 22, BeeconTranslations.BEECON_EFFECT_BUTTON);
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
        if (MathUtils.inRangeInclusive((int) mouseX, getX() + 60, getX() + 85) && MathUtils.inRangeInclusive((int) mouseY, getY() + 4, getY() + 19)) {
            NetworkHandler.CHANNEL.sendToServer(new BeeconChangePacket(
                    this.isSelected() ? BeeconChangePacket.Option.EFFECT_OFF : BeeconChangePacket.Option.EFFECT_ON,
                    MobEffect.getId(effect),
                    tile.getBlockPos()
                )
            );
        }
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float pPartialTicks) {
        Minecraft mc = Minecraft.getInstance();
        graphics.blit(this.getX() + 2, this.getY() + 2, 0, 18, 18, this.effectSprite);
        graphics.drawString(mc.font, Component.literal("x" + tile.getEffectValue(effect)), this.getX() + 24, this.getY() + 6, 14737632);

        boolean buttonHover = MathUtils.inRangeInclusive(mouseX, getX() + 60, getX() + 85) && MathUtils.inRangeInclusive(mouseY, getY() + 4, getY() + 19);

        graphics.blit(BACKGROUND, getX() + 59, getY() + 3, isSelected() ? 0 : 26, buttonHover ? 216 : 200, 26, 16);

        if (buttonHover) {
            ScreenUtils.setTooltip(this.selected ? BeeconTranslations.EFFECT_ACTIVE : BeeconTranslations.EFFECT_INACTIVE);
        }

        if (MathUtils.inRangeInclusive(mouseX, getX() + 4, getX() + 22) && MathUtils.inRangeInclusive(mouseY, getY() + 4, getY() + 22)) {
            ScreenUtils.setTooltip(effect.getDisplayName());
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {

    }
}
