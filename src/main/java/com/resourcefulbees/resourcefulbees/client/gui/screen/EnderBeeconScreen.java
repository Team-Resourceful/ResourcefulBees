package com.resourcefulbees.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.EnderBeeconContainer;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.UpdateBeeconMessage;
import com.resourcefulbees.resourcefulbees.tileentity.EnderBeeconTileEntity;
import com.resourcefulbees.resourcefulbees.utils.RenderCuboid;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class EnderBeeconScreen extends ContainerScreen<EnderBeeconContainer> {

    private EnderBeeconTileEntity tileEntity;

    public EnderBeeconScreen(EnderBeeconContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn) {
        super(screenContainer, inventory, titleIn);
        this.tileEntity = screenContainer.enderBeeconTileEntity;
        preInit();
    }

    private static final ITextComponent primaryLabel = new TranslationTextComponent("block.resourcefulbees.ender_beecon.primary");
    private static final ITextComponent drainLabel = new TranslationTextComponent("block.resourcefulbees.ender_beecon.drain");
    private static final ITextComponent rangeLabel = new TranslationTextComponent("block.resourcefulbees.ender_beecon.range");
    private static final ITextComponent activeLabel = new TranslationTextComponent("block.resourcefulbees.ender_beecon.is_active");
    private static final ResourceLocation BUTTON_CALMING = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/ender_beecon/calming_button.png");

    List<PowerButton> powerButtons = new LinkedList<>();

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
        int buttonX = i + 24;
        int buttonY = j + 36;
        int buttonWidth = 22;
        int padding = 16;
        int buttonStartX = buttonX;

        for (EnderBeeconTileEntity.BeeconEffect effect : tileEntity.getEffects()) {
            PowerButton button = this.addButton(new PowerButton(buttonStartX, buttonY, effect.effect, BUTTON_CALMING));
            button.active = true;
            button.setSelected(effect.active);
            powerButtons.add(button);
            buttonStartX += buttonWidth + padding;
        }
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
        drawFluidTank(matrix, mouseX, mouseY);
    }

    private void drawFluidTank(MatrixStack matrix, int mouseX, int mouseY) {
        if (!tileEntity.fluidTank.isEmpty()) {

            //init stuff
            FluidStack stack = tileEntity.fluidTank.getFluid();
            ResourceLocation texture = stack.getFluid().getAttributes().getStillTexture();
            TextureAtlasSprite fluid = RenderUtils.getStillFluidTexture(stack);
            this.client.getTextureManager().bindTexture(texture);
            //prep color
            int color = stack.getFluid().getAttributes().getColor();
            float red = RenderCuboid.INSTANCE.getRed(color);
            float green = RenderCuboid.INSTANCE.getGreen(color);
            float blue = RenderCuboid.INSTANCE.getBlue(color);
            float alpha = RenderCuboid.INSTANCE.getAlpha(color);
            RenderSystem.color4f(red, green, blue, alpha);
            int tankPosX = this.guiLeft + 207;
            int tankPosY = this.guiTop + 92;
            int tankHeight = 62;
            int tankWidth = 14;

            int effectiveHeight = stack.getAmount() / tileEntity.fluidTank.getCapacity() * tankHeight;

            this.drawTexture(matrix, tankPosX, tankPosY - effectiveHeight, 0, 0, tankWidth, effectiveHeight, fluid.getWidth(), fluid.getHeight());
        }
    }

    protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForeground(matrixStack, mouseX, mouseY);
        int buttonX = 24 + 11;
        int buttonY = 36 + 25;
        int buttonWidth = 22;
        int padding = 16;
        int buttonStartX = buttonX;
        drawCenteredText(matrixStack, this.textRenderer, primaryLabel, 92, 24, 14737632);
        this.textRenderer.draw(matrixStack, drainLabel, 13, 90, 14737632);
        this.textRenderer.draw(matrixStack, tileEntity.getDrain() + " mb/t", 44, 90, 16751628);
        this.textRenderer.draw(matrixStack, rangeLabel, 90, 90, 14737632);
        this.textRenderer.draw(matrixStack, tileEntity.getRange() + " blocks", 125, 90, 34815);
        this.textRenderer.draw(matrixStack, activeLabel, 13, 78, 14737632);
        if (tileEntity.doEffects()) {
            this.textRenderer.draw(matrixStack, "Yes", 63, 78, 47104);
        } else {
            this.textRenderer.draw(matrixStack, "No", 63, 78, 12320768);
        }

        for (PowerButton widget : this.powerButtons) {
            EnderBeeconTileEntity.BeeconEffect effect = tileEntity.getEffect(widget.effect);
            if (Config.BEECON_DO_MULTIPLIER.get()) {
                drawCenteredText(matrixStack, this.textRenderer, new StringTextComponent("x" + effect.value), buttonStartX, buttonY, 14737632);
            } else {
                drawCenteredText(matrixStack, this.textRenderer, new StringTextComponent("+" + effect.value), buttonStartX, buttonY, 14737632);
            }
            buttonStartX += buttonWidth + padding;
            if (widget.isHovered()) widget.renderToolTip(matrixStack, mouseX - this.guiLeft, mouseY - this.guiTop);
        }
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

            this.drawTexture(matrixStack, this.x, this.y, j, i, this.width, this.height, 22 * 4, 22);
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
            NetPacketHandler.sendToServer(new UpdateBeeconMessage(effect.getRegistryName(), !this.isSelected(), tileEntity.getPos()));
            this.setSelected(!this.isSelected());
            tileEntity.toggleEffect(effect);
            EnderBeeconScreen.this.tick();
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
