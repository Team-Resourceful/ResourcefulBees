package com.resourcefulbees.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.BeeconChangeMessage;
import com.resourcefulbees.resourcefulbees.tileentity.EnderBeeconTileEntity;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class BeeconEffectWidget extends Widget {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/ender_beecon/ender_beecon.png");

    private final EnderBeeconTileEntity tile;

    private final Effect effect;
    private final TextureAtlasSprite effectSprite;

    private boolean selected;

    public BeeconEffectWidget(int x, int y, Effect effect, EnderBeeconTileEntity tile) {
        super(x, y, 88, 22, new StringTextComponent("Beecon Effect Button"));
        this.effectSprite = Minecraft.getInstance().getMobEffectTextures().get(effect);
        this.effect = effect;
        this.tile = tile;
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
                            Effect.getId(effect),
                            tile.getBlockPos()
                    )
            );
            this.playDownSound(Minecraft.getInstance().getSoundManager());
        }
    }

    @Override
    public void renderButton(@NotNull MatrixStack matrix, int mouseX, int mouseY, float pPartialTicks) {
        Minecraft mc = Minecraft.getInstance();
        mc.getTextureManager().bind(this.effectSprite.atlas().location());
        blit(matrix, this.x + 2, this.y + 2, this.getBlitOffset(), 18, 18, this.effectSprite);
        drawString(matrix, mc.font, new StringTextComponent("x" + tile.getEffectValue(effect)), this.x+24, this.y+6, 14737632);

        boolean buttonHover = MathUtils.inRangeInclusive(mouseX, x+60, x+85) && MathUtils.inRangeInclusive(mouseY, y+4, y+19);

        mc.getTextureManager().bind(BACKGROUND);
        blit(matrix, this.x + 59, this.y + 3, isSelected() ? 0 : 26, buttonHover ? 216 : 200, 26, 16);
    }

    @Override
    public void renderToolTip(@NotNull MatrixStack matrix, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.screen == null) return;


        if (MathUtils.inRangeInclusive(mouseX, x+60, x+85) && MathUtils.inRangeInclusive(mouseY, y+4, y+19)) {
            GuiUtils.drawHoveringText(matrix, Collections.singletonList(new StringTextComponent(this.selected ? "Active" : "Not Active")), mouseX, mouseY, mc.screen.width, mc.screen.height, -1, mc.font);
        }

        if (MathUtils.inRangeInclusive(mouseX, x+4, x+22) && MathUtils.inRangeInclusive(mouseY, y+4, y+22)) {
            GuiUtils.drawHoveringText(matrix, Collections.singletonList(effect.getDisplayName()), mouseX, mouseY, mc.screen.width, mc.screen.height, -1, mc.font);
        }

    }
}