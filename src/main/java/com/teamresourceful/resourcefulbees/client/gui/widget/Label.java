package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class Label extends TooltipWidget {

    public Component text;
    private final int color;

    public Label(Component text, int x, int y, int width, int height, int color, Component message) {
        super(x, y, width, height, message);
        this.text = text;
        this.color = color;
    }

    public Label(Component text, int x, int y) {
        this(text, x, y, -1);
    }

    public Label(Component text, int x, int y, Component message) {
        this(text, x, y, -1, message);
    }

    public Label(Component text, int x, int y, int color) {
        this(text, x, y, color, Component.literal(""));
    }

    public Label(Component text, int x, int y, int color, Component message) {
        this(text, x, y, Minecraft.getInstance().font.width(text), Minecraft.getInstance().font.lineHeight, color, message);
    }

    public Label(Component text, int x, int y, int width, int height, int color) {
        this(text, x, y, width, height, color, text);
    }

    public Label(Component text, int x, int y, int width, int height) {
        this(text, x, y, width, height, -1, text);
    }

    public Label(Component text, int x, int y, int width, int height, Component message) {
        this(text, x, y, width, height, -1, message);
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        Minecraft.getInstance().font.draw(matrixStack, text, this.x, this.y, color);
    }
}
