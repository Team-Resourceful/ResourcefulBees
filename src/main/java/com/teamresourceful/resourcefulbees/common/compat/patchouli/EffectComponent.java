package com.teamresourceful.resourcefulbees.common.compat.patchouli;

//import com.mojang.blaze3d.vertex.PoseStack;
//import com.teamresourceful.resourcefulbees.client.utils.RenderUtils;
//import net.minecraft.ChatFormatting;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.Font;
//import net.minecraft.client.gui.GuiComponent;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.network.chat.Component;
//import net.minecraft.network.chat.TranslatableComponent;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.effect.MobEffect;
//import net.minecraftforge.registries.ForgeRegistries;
//import org.jetbrains.annotations.NotNull;
//import vazkii.patchouli.api.IComponentRenderContext;
//import vazkii.patchouli.api.ICustomComponent;
//import vazkii.patchouli.api.IVariable;
//
//import java.util.function.UnaryOperator;
//
//public class EffectComponent implements ICustomComponent {
//
//    IVariable effectID;
//    private transient TextureAtlasSprite effectSprite;
//    private static final transient ResourceLocation EFFECT_BACKGROUND = new ResourceLocation("patchouli", "textures/gui/crafting.png");
//    private transient int xOffset;
//    private transient int yOffset;
//    private transient Component effectName;
//
//    @Override
//    public void build(int componentX, int componentY, int pageNum) {
//        xOffset = componentX;
//        yOffset = componentY;
//    }
//
//    private Component getEffectName(MobEffect effect) {
//        return new TranslatableComponent(effect.getDescriptionId());
//    }
//
//    @Override
//    public void render(@NotNull PoseStack matrixStack, @NotNull IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
//        Font text = Minecraft.getInstance().font;
//        float width = text.width(effectName);
//        text.draw(matrixStack, effectName.copy().withStyle(ChatFormatting.BLACK), xOffset - width / 2, yOffset, -1);
//        RenderUtils.bindTexture(EFFECT_BACKGROUND);
//        GuiComponent.blit(matrixStack, xOffset - 32, yOffset + 6, 1, 99, 64, 32, 128, 256);
//        RenderUtils.bindTexture(this.effectSprite.atlas().location());
//        GuiComponent.blit(matrixStack, xOffset - 9, yOffset + 13, 1, 18, 18, this.effectSprite);
//    }
//
//    @Override
//    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
//        effectID = lookup.apply(effectID);
//        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effectID.asString()));
//        this.effectSprite = Minecraft.getInstance().getMobEffectTextures().get(effect);
//        this.effectName = getEffectName(effect);
//    }
//}
