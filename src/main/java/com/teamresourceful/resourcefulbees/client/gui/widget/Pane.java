package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class Pane extends TooltipWidget {

    protected final Set<TooltipWidget> children = new HashSet<>();
    private boolean doScissor = false;

    public Pane(int xPos, int yPos, int viewWidth, int viewHeight, Component message) {
        super(xPos, yPos, viewWidth, viewHeight, message);
        mute = true;
    }

    public Pane(int xPos, int yPos, int viewWidth, int viewHeight) {
        this(xPos, yPos, viewWidth, viewHeight, new TextComponent(""));
    }

    public Pane(ScreenArea screenArea) {
        this(screenArea, new TextComponent(""));
    }

    public Pane(ScreenArea screenArea, Component message) {
        this(screenArea.xPos(), screenArea.yPos(), screenArea.width(), screenArea.height(), message);
    }

    public void toggleScissor() {
        this.doScissor = !doScissor;
    }

    public void startScissor() {
        if (!doScissor) return;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        Minecraft minecraft = Minecraft.getInstance();
        double scale = Minecraft.getInstance().getWindow().getGuiScale();
        int bottom = (int) (minecraft.getWindow().getHeight() - (this.y + this.height) * scale);
        GL11.glScissor((int) (this.x * scale), bottom, (int) (this.width * scale), (int) (this.height * scale));
    }

    public void endScissor() {
        if (!doScissor) return;
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @OverridingMethodsMustInvokeSuper
    public void renderChild(PoseStack matrix, TooltipWidget child, int mouseX, int mouseY, float partialTicks) {
        child.render(matrix, mouseX, mouseY, partialTicks);
    }

    public void modifyPane(PoseStack matrix) {
        matrix.translate(x, y, 0);
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        startScissor();

        //move the matrix origin to the top left corner of scroll area
        matrixStack.pushPose();
        modifyPane(matrixStack);

        for (TooltipWidget child : children) {
            renderChild(matrixStack, child, mouseX, mouseY, partialTicks);
            matrixStack.pushPose();
            matrixStack.translate(0, 0, 100);
            matrixStack.scale(0.5f, 0.5f, 1.0f);
            Minecraft.getInstance().font.draw(matrixStack, "[" + child.x + ", " + child.y + "]", child.x * 2f, child.y * 2f - 9f, Color.DEFAULT.getValue());
            matrixStack.popPose();
        }

        //reset
        matrixStack.popPose();
        endScissor();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        for (AbstractWidget child : children) {
            boolean scrolled = child.mouseScrolled(mouseX - x, mouseY - y, scrollAmount);
            if (scrolled) return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollAmount);
    }


    @Override
    public void onClick(double mouseX, double mouseY) {
        children.forEach(c -> c.onClick(mouseX - x, mouseY - y));
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        children.forEach(c -> c.onRelease(mouseX - x, mouseY - y));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (AbstractWidget child : children) {
            boolean clicked = child.mouseClicked(mouseX - x, mouseY - y, mouseButton);
            if (clicked) return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        for (AbstractWidget child : children) {
            boolean released = child.mouseReleased(mouseX - x, mouseY - y, mouseButton);
            if (released) return true;
        }
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double mouseXAmount, double mouseYAmount) {
        for (TooltipWidget child : children) {
            boolean dragged = child.mouseDragged(mouseX - x, mouseY - y, button, mouseXAmount, mouseYAmount);
            if (dragged) return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, mouseXAmount, mouseYAmount);
    }

    @Override
    public void drawTooltips(@NotNull PoseStack matrix, Screen screen, int mouseX, int mouseY) {
        super.drawTooltips(matrix, screen, mouseX, mouseY);
        children.forEach(c -> c.renderToolTip(matrix, mouseX, mouseY));
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        children.forEach(c -> c.mouseMoved(mouseX - x, mouseY - y));
    }

    @Override
    public boolean keyPressed(int keycode, int scanCode, int modifiers) {
        for (AbstractWidget child : children) {
            boolean pressed = child.keyPressed(keycode, scanCode, modifiers);
            if (pressed) return true;
        }
        return super.keyPressed(keycode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keycode, int scanCode, int modifiers) {
        for (AbstractWidget child : children) {
            boolean pressed = child.keyReleased(keycode, scanCode, modifiers);
            if (pressed) return true;
        }
        return super.keyReleased(keycode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        for (AbstractWidget child : children) {
            boolean typed = child.charTyped(character, modifiers);
            if (typed) return true;
        }
        return super.charTyped(character, modifiers);
    }

    public void add(TooltipWidget widget) {
        children.add(widget);
        widget.addParent(x, y, this);
    }

    protected void addAll(List<? extends TooltipWidget> beeButtons) {
        children.addAll(beeButtons);
        beeButtons.forEach(c -> c.addParent(x, y, this));
    }

    public void reset() {
        children.clear();
    }
}
