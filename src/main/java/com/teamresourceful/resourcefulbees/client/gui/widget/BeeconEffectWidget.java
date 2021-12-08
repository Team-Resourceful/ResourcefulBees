package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.BeeconChangeMessage;
import com.teamresourceful.resourcefulbees.common.tileentity.EnderBeeconTileEntity;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.potion.Effect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class BeeconEffectWidget extends AbstractWidget {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/ender_beecon/ender_beecon.png");

    private final EnderBeeconTileEntity tile;

    private final MobEffect effect;
    private final TextureAtlasSprite effectSprite;

    private boolean selected;

    public BeeconEffectWidget(int x, int y, MobEffect effect, EnderBeeconTileEntity tile) {
        super(x, y, 88, 22, new TextComponent("Beecon Effect Button"));
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
                    MobEffect.getId(effect),
                    tile.getBlockPos()
                )
            );
        }
    }

    @Override
    public void renderButton(@NotNull PoseStack matrix, int mouseX, int mouseY, float pPartialTicks) {
        Minecraft mc = Minecraft.getInstance();
        RenderUtils.bindTexture(this.effectSprite.atlas().location());
        blit(matrix, this.x + 2, this.y + 2, this.getBlitOffset(), 18, 18, this.effectSprite);
        drawString(matrix, mc.font, new TextComponent("x" + tile.getEffectValue(effect)), this.x+24, this.y+6, 14737632);

        boolean buttonHover = MathUtils.inRangeInclusive(mouseX, x+60, x+85) && MathUtils.inRangeInclusive(mouseY, y+4, y+19);

        RenderUtils.bindTexture(BACKGROUND);
        blit(matrix, this.x + 59, this.y + 3, isSelected() ? 0 : 26, buttonHover ? 216 : 200, 26, 16);
    }

    @Override
    public void renderToolTip(@NotNull PoseStack matrix, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.screen == null) return;


        if (MathUtils.inRangeInclusive(mouseX, x+60, x+85) && MathUtils.inRangeInclusive(mouseY, y+4, y+19)) {

            //TODO GuiUtils.drawHoveringText(matrix, Collections.singletonList(new TextComponent(this.selected ? "Active" : "Not Active")), mouseX, mouseY, mc.screen.width, mc.screen.height, -1, mc.font);
        }

        if (MathUtils.inRangeInclusive(mouseX, x+4, x+22) && MathUtils.inRangeInclusive(mouseY, y+4, y+22)) {
            //TODO GuiUtils.drawHoveringText(matrix, Collections.singletonList(effect.getDisplayName()), mouseX, mouseY, mc.screen.width, mc.screen.height, -1, mc.font);
        }

    }
}
