package com.resourcefulbees.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.container.EnderBeeconContainer;
import com.resourcefulbees.resourcefulbees.effects.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.BeaconScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class EnderBeeconScreen extends ContainerScreen<EnderBeeconContainer> {

    public EnderBeeconScreen(EnderBeeconContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
        preInit();
    }

    private static final ResourceLocation BUTTON_CALMING = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/ender_beecon/calming_button.png");
    private static final ResourceLocation BUTTON_WATER = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/ender_beecon/calming_button.png");
    private static final ResourceLocation BUTTON_FIRE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/ender_beecon/calming_button.png");
    private static final ResourceLocation BUTTON_REGEN = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/ender_beecon/calming_button.png");

    private PowerButton calmingButton;
    private PowerButton waterBreathingButton;
    private PowerButton fireResistanceButton;
    private PowerButton regenerationButton;

    private void preInit() {
        this.xSize = 230;
        this.ySize = 200;
        this.playerInventoryTitleX = 36;
        this.playerInventoryTitleY = 106;
    }

    @Override
    protected void init() {
        super.init();
        buttons.clear();
        int i = this.guiLeft;
        int j = this.guiTop;

        calmingButton = this.addButton(new PowerButton(i + 18, j + 36, ModEffects.CALMING.get(), BUTTON_CALMING));

        waterBreathingButton = this.addButton(new PowerButton(i + 43, j + 36, Effects.WATER_BREATHING, BUTTON_CALMING));

        fireResistanceButton = this.addButton(new PowerButton(i + 68, j + 36, Effects.FIRE_RESISTANCE, BUTTON_CALMING));

        regenerationButton = this.addButton(new PowerButton(i + 93, j + 36, Effects.REGENERATION, BUTTON_CALMING));
    }

    @Override
    protected void drawBackground(@Nonnull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/ender_beecon/ender_beecon.png");
        Minecraft client = this.client;
        if (client != null) {
            client.getTextureManager().bindTexture(texture);
            int i = this.guiLeft;
            int j = this.guiTop;
            this.drawTexture(matrix, i, j, 0, 0, this.xSize, this.ySize);
        }
        calmingButton.active = true;
        waterBreathingButton.active = true;
        fireResistanceButton.active = true;
        regenerationButton.active = true;
    }

    @OnlyIn(Dist.CLIENT)
    abstract static class Button extends AbstractButton {
        private boolean selected;
        private ResourceLocation texture;

        protected Button(int xPos, int yPos, ResourceLocation texture) {
            super(xPos, yPos, 22, 22, StringTextComponent.EMPTY);
            this.texture = texture;
        }

        public void renderButton(MatrixStack matrixStack, int xPos, int yPos, float v) {
            Minecraft.getInstance().getTextureManager().bindTexture(texture);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int i = 0;
            int j = 0;
            if (!this.active) {
                j += this.width * 2;
            } else if (this.selected) {
                j += this.width * 1;
            } else if (this.isHovered()) {
                j += this.width * 3;
            }

            this.drawTexture(matrixStack, this.x, this.y, j, i, this.width, this.height);
            this.renderExtra(matrixStack);
        }

        protected abstract void renderExtra(MatrixStack p_230454_1_);

        public boolean isSelected() {
            return this.selected;
        }

        public void setSelected(boolean p_146140_1_) {
            this.selected = p_146140_1_;
        }
    }

    @OnlyIn(Dist.CLIENT)
    class PowerButton extends EnderBeeconScreen.Button {
        private final Effect effect;
        private final TextureAtlasSprite effectSprite;
        private final ITextComponent textComponent;

        public PowerButton(int xPos, int yPos, Effect effect, ResourceLocation texture) {
            super(xPos, yPos, texture);
            this.effect = effect;
            this.effectSprite = Minecraft.getInstance().getPotionSpriteUploader().getSprite(effect);
            this.textComponent = this.renderTooltip(effect);
        }

        private ITextComponent renderTooltip(Effect effect) {
            IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(effect.getName());
            return iformattabletextcomponent;
        }

        public void onPress() {
            if (!this.isSelected()) {
                EnderBeeconScreen.this.buttons.clear();
                EnderBeeconScreen.this.children.clear();
                EnderBeeconScreen.this.init();
                EnderBeeconScreen.this.tick();
            }
        }

        public void renderToolTip(MatrixStack matrixStack, int xPos, int yPos) {
            EnderBeeconScreen.this.renderTooltip(matrixStack, this.textComponent, xPos, yPos);
        }

        protected void renderExtra(MatrixStack matrixStack) {
            Minecraft.getInstance().getTextureManager().bindTexture(this.effectSprite.getAtlas().getId());
            drawSprite(matrixStack, this.x + 2, this.y + 2, this.getZOffset(), 18, 18, this.effectSprite);
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
    }
}
