package com.dungeonderps.resourcefulbees.client.gui.screen;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.client.gui.widget.ArrowButton;
import com.dungeonderps.resourcefulbees.container.UnvalidatedApiaryContainer;
import com.dungeonderps.resourcefulbees.network.NetPacketHandler;
import com.dungeonderps.resourcefulbees.network.packets.BuildApiaryMessage;
import com.dungeonderps.resourcefulbees.network.packets.ValidateApiaryMessage;
import com.dungeonderps.resourcefulbees.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class UnvalidatedApiaryScreen extends ContainerScreen<UnvalidatedApiaryContainer> {

    private final ResourceLocation unvalidatedTexture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/unvalidated.png");
    private final ResourceLocation arrowButtonTexture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/arrow_button.png");
    private final PlayerEntity player;
    private int verticalOffset = 0;
    private int horizontalOffset = 0;

    public UnvalidatedApiaryScreen(UnvalidatedApiaryContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.player = inv.player;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new Button(getGuiLeft() + 116, getGuiTop() + 10, 50, 20, I18n.format("gui.resourcefulbees.apiary.button.validate"), (onPress) -> this.validate()));
        Button buildStructureButton = this.addButton(new Button(getGuiLeft() + 116, getGuiTop() + 35, 50, 20, I18n.format("gui.resourcefulbees.apiary.button.build"), (onPress) -> this.build()));
        if (!this.player.isCreative()) {
            buildStructureButton.active = false;
        }
        this.addButton(new ArrowButton(getGuiLeft() + 22, getGuiTop() + 12, 12, 12, 0, 13, 0, arrowButtonTexture, (onPress) -> this.offsetPosition(Direction.UP)));
        this.addButton(new ArrowButton(getGuiLeft() + 9, getGuiTop() + 25, 12, 12, 0, 25, 0, arrowButtonTexture, (onPress) -> this.offsetPosition(Direction.LEFT)));
        this.addButton(new ArrowButton(getGuiLeft() + 22, getGuiTop() + 38, 12, 12, 13, 13, 0, arrowButtonTexture, (onPress) -> this.offsetPosition(Direction.DOWN)));
        this.addButton(new ArrowButton(getGuiLeft() + 35, getGuiTop() + 25, 12, 12, 13, 25, 0, arrowButtonTexture, (onPress) -> this.offsetPosition(Direction.RIGHT)));
    }

    private void offsetPosition(Direction direction) {
        switch (direction) {
            case UP: verticalOffset++;
                break;
            case DOWN: verticalOffset--;
                break;
            case LEFT: horizontalOffset--;
                break;
            default: horizontalOffset++;
        }
        verticalOffset = MathUtils.clamp(verticalOffset, -1, 2);
        horizontalOffset = MathUtils.clamp(horizontalOffset, -2, 2);
    }

    private void build() {
        UnvalidatedApiaryContainer container = getContainer();
        BlockPos pos = container.pos;
        NetPacketHandler.sendToServer(new BuildApiaryMessage(pos, verticalOffset, horizontalOffset));
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        super.render(p_render_1_, p_render_2_, p_render_3_);
        this.renderHoveredToolTip(p_render_1_, p_render_2_);
    }

    private void validate() {
        UnvalidatedApiaryContainer container = getContainer();
        BlockPos pos = container.pos;
        NetPacketHandler.sendToServer(new ValidateApiaryMessage(pos, verticalOffset, horizontalOffset));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft client = this.minecraft;
        if (client != null) {
            this.minecraft.getTextureManager().bindTexture(unvalidatedTexture);
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            this.blit(i, j, 0, 0, this.xSize, this.ySize);
        }
    }

    private enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}
