package com.resourcefulbees.resourcefulbees.compat.patchouli;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.util.function.UnaryOperator;

public class EffectComponent implements ICustomComponent {

    IVariable effectID;
    private transient TextureAtlasSprite effectSprite;
    private transient Effect effect;
    private transient static final ResourceLocation EFFECT_BACKGROUND = new ResourceLocation("patchouli", "textures/gui/crafting.png");
    private transient int xOffset;
    private transient int yOffset;
    private transient ITextComponent effectName;

    @Override
    public void build(int componentX, int componentY, int pageNum) {
        xOffset = componentX;
        yOffset = componentY;
    }

    private ITextComponent getEffectName(Effect effect) {
        return new TranslationTextComponent(effect.getDescriptionId());
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, @NotNull IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        FontRenderer text = Minecraft.getInstance().font;
        TextureManager manager = Minecraft.getInstance().getTextureManager();
        float width = text.width(effectName);
        text.draw(matrixStack, effectName.copy().withStyle(TextFormatting.BLACK), xOffset - width / 2, yOffset, -1);
        manager.bind(EFFECT_BACKGROUND);
        AbstractGui.blit(matrixStack, xOffset - 32, yOffset + 6, 1, 99, 64, 32, 128, 256);
        manager.bind(this.effectSprite.atlas().location());
        AbstractGui.blit(matrixStack, xOffset - 9, yOffset + 13, 1, 18, 18, this.effectSprite);
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
        effectID = lookup.apply(effectID);
        this.effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(effectID.asString()));
        this.effectSprite = Minecraft.getInstance().getMobEffectTextures().get(effect);
        this.effectName = getEffectName(this.effect);
    }
}
