package com.resourcefulbees.resourcefulbees.compat.patchouli;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
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
        IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(effect.getName());
        return iformattabletextcomponent;
    }

    @Override
    public void render(MatrixStack matrixStack, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        FontRenderer text = Minecraft.getInstance().fontRenderer;
        TextureManager manager = Minecraft.getInstance().getTextureManager();
        float width = text.getWidth(effectName);
        text.draw(matrixStack, effectName, xOffset - width / 2, yOffset, Color.parse("black").getRgb());
        manager.bindTexture(EFFECT_BACKGROUND);
        AbstractGui.drawTexture(matrixStack, xOffset - 32, yOffset + 6, 1, 99, 64, 32, 128, 256);
        manager.bindTexture(this.effectSprite.getAtlas().getId());
        AbstractGui.drawSprite(matrixStack, xOffset - 9, yOffset + 13, 1, 18, 18, this.effectSprite);
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
        effectID = lookup.apply(effectID);
        this.effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(effectID.asString()));
        this.effectSprite = Minecraft.getInstance().getPotionSpriteUploader().getSprite(effect);
        this.effectName = getEffectName(this.effect);
    }
}
