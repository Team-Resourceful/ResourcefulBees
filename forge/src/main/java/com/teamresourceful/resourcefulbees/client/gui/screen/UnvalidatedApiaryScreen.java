package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.gui.widget.ArrowButton;
import com.teamresourceful.resourcefulbees.container.UnvalidatedApiaryContainer;
import com.teamresourceful.resourcefulbees.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.network.packets.BuildApiaryMessage;
import com.teamresourceful.resourcefulbees.network.packets.ValidateApiaryMessage;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import com.teamresourceful.resourcefulbees.utils.MathUtils;
import com.teamresourceful.resourcefulbees.utils.PreviewHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class UnvalidatedApiaryScreen extends AbstractContainerScreen<UnvalidatedApiaryContainer> {

    private static final ResourceLocation unvalidatedTexture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/unvalidated.png");
    private static final ResourceLocation arrowButtonTexture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/arrow_button.png");
    private final ApiaryTileEntity apiaryTileEntity;
    private final Player player;
    private int verticalOffset;
    private int horizontalOffset;
    private ArrowButton upButton;
    private ArrowButton downButton;
    private ArrowButton leftButton;
    private ArrowButton rightButton;
    private PreviewButton previewButton;


    public UnvalidatedApiaryScreen(UnvalidatedApiaryContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.player = inv.player;
        this.verticalOffset = screenContainer.getApiaryTileEntity().getVerticalOffset();
        this.horizontalOffset = screenContainer.getApiaryTileEntity().getHorizontalOffset();
        this.apiaryTileEntity = this.menu.getApiaryTileEntity();
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new Button(getGuiLeft() + 116, getGuiTop() + 10, 50, 20, new TranslatableComponent("gui.resourcefulbees.apiary.button.validate"), onPress -> this.validate()));
        BuildButton buildStructureButton = this.addButton(new BuildButton(getGuiLeft() + 116, getGuiTop() + 35, 50, 20, new TranslatableComponent("gui.resourcefulbees.apiary.button.build"), onPress -> this.build()));
        if (!this.player.isCreative()) {
            buildStructureButton.active = false;
        }
        this.previewButton = this.addButton(new PreviewButton(getGuiLeft() + 22, getGuiTop() + 25, 12, 12, 0, 24, 12, arrowButtonTexture, this.menu.getApiaryTileEntity().isPreviewed(), onPress -> {
            setPreviewToggle();
            previewSetToggle(this.previewButton.isTriggered());
        }));
        previewSetToggle(this.previewButton.isTriggered());
        this.upButton = this.addButton(new ArrowButton(getGuiLeft() + 22, getGuiTop() + 12, ArrowButton.Direction.UP, onPress -> this.offsetPosition(Direction.UP)));
        this.downButton = this.addButton(new ArrowButton(getGuiLeft() + 22, getGuiTop() + 38, ArrowButton.Direction.DOWN, onPress -> this.offsetPosition(Direction.DOWN)));
        this.leftButton = this.addButton(new ArrowButton(getGuiLeft() + 9, getGuiTop() + 25, ArrowButton.Direction.LEFT, onPress -> this.offsetPosition(Direction.LEFT)));
        this.rightButton = this.addButton(new ArrowButton(getGuiLeft() + 35, getGuiTop() + 25, ArrowButton.Direction.RIGHT, onPress -> this.offsetPosition(Direction.RIGHT)));
    }

    private void previewSetToggle(boolean toggled) {
        if (!toggled)
            this.previewButton.setTrigger(false);

        PreviewHandler.setPreview(getMenu().getPos(), this.menu.getApiaryTileEntity().buildStructureBounds(this.horizontalOffset, this.verticalOffset), toggled);
    }

    private void setPreviewToggle() {
        if (this.previewButton.active)
            this.previewButton.setTrigger(!this.previewButton.isTriggered());
    }

    private void offsetPosition(Direction direction) {
        previewSetToggle(false);
        switch (direction) {
            case UP:
                verticalOffset++;
                break;
            case DOWN:
                verticalOffset--;
                break;
            case LEFT:
                horizontalOffset--;
                break;
            default:
                horizontalOffset++;
        }
        verticalOffset = MathUtils.clamp(verticalOffset, -1, 2);
        horizontalOffset = MathUtils.clamp(horizontalOffset, -2, 2);

        apiaryTileEntity.setVerticalOffset(verticalOffset);
        apiaryTileEntity.setHorizontalOffset(horizontalOffset);
    }

    private void build() {
        previewSetToggle(false);
        UnvalidatedApiaryContainer container = getMenu();
        BlockPos pos = container.getPos();
        NetPacketHandler.sendToServer(new BuildApiaryMessage(pos, verticalOffset, horizontalOffset));
    }

    @Override
    public void render(@NotNull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (apiaryTileEntity != null) {
            this.upButton.active = verticalOffset != 2;
            this.downButton.active = verticalOffset != -1;
            this.leftButton.active = horizontalOffset != -2;
            this.rightButton.active = horizontalOffset != 2;
            this.renderBackground(matrix);
            super.render(matrix, mouseX, mouseY, partialTicks);
            this.renderTooltip(matrix, mouseX, mouseY);
        }
    }

    private void validate() {
        previewSetToggle(false);
        UnvalidatedApiaryContainer container = getMenu();
        BlockPos pos = container.getPos();
        NetPacketHandler.sendToServer(new ValidateApiaryMessage(pos, verticalOffset, horizontalOffset));
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
        Minecraft client = this.minecraft;
        if (client != null) {
            this.minecraft.getTextureManager().bind(unvalidatedTexture);
            int i = (this.width - this.imageWidth) / 2;
            int j = (this.height - this.imageHeight) / 2;
            this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
        }
    }

    @Override
    protected void renderLabels(@NotNull PoseStack matrix, int mouseX, int mouseY) {
        this.font.draw(matrix,  "Offset", 65, 13, 0x404040);
        this.font.draw(matrix, "Vert.", 75, 26, 0x404040);
        this.font.draw(matrix, "Horiz.", 75, 39, 0x404040);
        this.drawRightAlignedString(matrix, font, String.valueOf(verticalOffset), 70, 26, 0x404040);
        this.drawRightAlignedString(matrix, font, String.valueOf(horizontalOffset), 70, 39, 0x404040);

        for (AbstractWidget widget : this.buttons) {
            if (widget.isHovered()) {
                widget.renderToolTip(matrix, mouseX - this.leftPos, mouseY - this.topPos);
                break;
            }
        }
    }


    public void drawRightAlignedString(@NotNull PoseStack matrix, Font fontRenderer, @NotNull String s, int posX, int posY, int color) {
        fontRenderer.draw(matrix, s, (float) (posX - fontRenderer.width(s)), (float) posY, color);
    }

    private enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    @OnlyIn(Dist.CLIENT)
    public class BuildButton extends Button {
        public BuildButton(int widthIn, int heightIn, int width, int height, TranslatableComponent text, OnPress onPress) {
            super(widthIn, heightIn, width, height, text, onPress);
        }

        @Override
        public void renderToolTip(@NotNull PoseStack matrix, int mouseX, int mouseY) {
            if (!this.active) {
                TranslatableComponent s = new TranslatableComponent("gui.resourcefulbees.apiary.button.build.creative");
                UnvalidatedApiaryScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class PreviewButton extends ImageButton {
        private final ResourceLocation resourceLocation;
        private final int xTexStart;
        private final int yTexStart;
        private final int yDiffText;
        private boolean triggered;

        public PreviewButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, boolean triggered, OnPress onPressIn) {
            super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, onPressIn);
            this.triggered = triggered;
            this.xTexStart = xTexStartIn;
            this.yTexStart = yTexStartIn;
            this.yDiffText = yDiffTextIn;
            this.resourceLocation = resourceLocationIn;
        }

        @Override
        public void renderButton(@NotNull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getTextureManager().bind(this.resourceLocation);
            RenderSystem.disableDepthTest();
            int i = this.yTexStart;
            int j = this.xTexStart;
            if (!this.active) {
                j += 24;
            } else if (this.isTriggered()) {
                j += 12;
                if (this.isHovered()) {
                    i += this.yDiffText;
                }
            } else {
                if (this.isHovered()) {
                    i += this.yDiffText;
                }
            }
            blit(matrix, this.x, this.y, (float) j, (float) i, this.width, this.height, 64, 64);
            RenderSystem.enableDepthTest();
        }

        @Override
        public void renderToolTip(@NotNull PoseStack matrix, int mouseX, int mouseY) {
            TranslatableComponent s;
            if (!isTriggered()) {
                s = new TranslatableComponent("gui.resourcefulbees.apiary.button.preview.enable");
            }
            else {
                s = new TranslatableComponent("gui.resourcefulbees.apiary.button.preview.disable");
            }
            UnvalidatedApiaryScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
        }

        public void setTrigger(boolean triggered) {
            menu.getApiaryTileEntity().setPreviewed(triggered);
            this.triggered = triggered;
        }

        public boolean isTriggered() {
            return this.triggered;
        }
    }
}
