package com.dungeonderps.resourcefulbees.client.gui.screen;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.client.gui.widget.ValidateButton;
import com.dungeonderps.resourcefulbees.container.UnvalidatedApiaryContainer;
import com.dungeonderps.resourcefulbees.network.NetPacketHandler;
import com.dungeonderps.resourcefulbees.network.packets.ValidateApiaryMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class UnvalidatedApiaryScreen extends ContainerScreen<UnvalidatedApiaryContainer> {

    private ValidateButton validateButton;

    private ResourceLocation unvalidatedTexture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/unvalidated.png");
    private ResourceLocation validatedTexture = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/validated.png");

    public UnvalidatedApiaryScreen(UnvalidatedApiaryContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init() {
        super.init();
        this.validateButton = this.addButton(new ValidateButton(this.width/2, getGuiTop() + 30, 100, 30, "validate", (onPress) -> {
            this.validate();
        }));
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
        NetPacketHandler.sendToServer(new ValidateApiaryMessage(pos));
        //onClose();
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


}
