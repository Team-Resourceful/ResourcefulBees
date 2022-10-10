package com.teamresourceful.resourcefulbees.client.components.beepedia;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefullib.client.components.ImageButton;
import com.teamresourceful.resourcefullib.client.screens.TooltipProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class SlotButton extends ImageButton implements TooltipProvider {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/list_button.png");

    private final Supplier<Boolean> isActive;
    private final ResourceLocation texture;
    private final Runnable onPress;

    private Supplier<List<Component>> tooltip;

    public SlotButton(int x, int y, ResourceLocation texture, Supplier<Boolean> isActive, Runnable onPress) {
        super(x, y, 20, 20);
        this.imageHeight = 60;
        this.imageWidth = 20;
        this.isActive = isActive;
        this.texture = texture;
        this.onPress = onPress;
    }

    public void setTooltipProvider(Supplier<List<Component>> tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public void renderButton(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(stack, mouseX, mouseY, partialTicks);
        ClientUtils.bindTexture(this.texture);
        blit(stack, this.x + 2, this.y + 2, 0, 0, 16, 16, 16, 16);
    }

    @Override
    public ResourceLocation getTexture(int mouseX, int mouseY) {
        return TEXTURE;
    }

    @Override
    public int getU(int mouseX, int mouseY) {
        return 0;
    }

    @Override
    public int getV(int mouseX, int mouseY) {
        return isActive.get() ? 40 : isHovered ? 20 : 0;
    }

    @Override
    public void onPress() {
        onPress.run();
    }

    @Override
    public @NotNull List<Component> getTooltip(int mouseX, int mouseY) {
        return this.tooltip == null || !this.isHovered ? List.of() : this.tooltip.get();
    }
}
