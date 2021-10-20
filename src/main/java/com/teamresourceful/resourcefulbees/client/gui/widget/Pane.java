package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.HashSet;
import java.util.Set;

public class Pane extends TooltipWidget {

    protected final Set<TooltipWidget> children = new HashSet<>();
    private boolean doScissor = false;

    public Pane(int xPos, int yPos, int viewWidth, int viewHeight, ITextComponent message) {
        super(xPos, yPos, viewWidth, viewHeight, message);
    }

    public Pane(int xPos, int yPos, int viewWidth, int viewHeight) {
        this(xPos, yPos, viewWidth, viewHeight, new StringTextComponent(""));
    }

    public Pane(ScreenArea screenArea) {
        this(screenArea, new StringTextComponent(""));
    }

    public Pane(ScreenArea screenArea, ITextComponent message) {
        this(screenArea.xPos, screenArea.yPos, screenArea.width, screenArea.height, message);
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
    public void renderChild(MatrixStack matrix, Widget child, int mouseX, int mouseY, float partialTicks) {
        child.render(matrix, mouseX, mouseY, partialTicks);
    }

    public void modifyPane(MatrixStack matrix) {
        matrix.translate(x, y, 0);
    }

    @Override
    public void render(@NotNull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;
        startScissor();

        super.render(matrix, mouseX, mouseY, partialTicks);

        //move the matrix origin to the top left corner of scroll area
        matrix.pushPose();
        modifyPane(matrix);

        for (Widget child : children) {
            renderChild(matrix, child, mouseX, mouseY, partialTicks);
        }

        //reset
        matrix.popPose();
        endScissor();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        for (Widget child : children) {
            boolean scrolled = child.mouseScrolled(mouseX + x, mouseY + y, scrollAmount);
            if (scrolled) return true;
        }
        return false;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        children.forEach(c -> c.onClick(mouseX, mouseY));
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        children.forEach(c -> c.onRelease(mouseX, mouseY));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (Widget child : children) {
            boolean clicked = child.mouseClicked(mouseX + x, mouseY + y, mouseButton);
            if (clicked) return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        for (Widget child : children) {
            boolean released = child.mouseReleased(mouseX + x, mouseY + y, mouseButton);
            if (released) return true;
        }
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int p_231045_5_, double p_231045_6_, double p_231045_8_) {
        for (TooltipWidget child : children) {
            boolean dragged = child.mouseDragged(mouseX + x, mouseY + y, p_231045_5_, p_231045_6_, p_231045_8_);
            if (dragged) return true;
        }
        return super.mouseDragged(mouseX, mouseY, p_231045_5_, p_231045_6_, p_231045_8_);
    }

    @Override
    public void drawTooltips(@NotNull MatrixStack matrix, Screen screen, int mouseX, int mouseY) {
        super.drawTooltips(matrix, screen, mouseX, mouseY);
        children.forEach(c -> c.renderToolTip(matrix, mouseX + x, mouseY + y));
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        children.forEach(c -> mouseMoved(mouseX, mouseY));
    }

    @Override
    public boolean keyPressed(int keycode, int scanCode, int modifiers) {
        for (Widget child : children) {
            boolean pressed = child.keyPressed(keycode, scanCode, modifiers);
            if (pressed) return true;
        }
        return super.keyPressed(keycode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keycode, int scanCode, int modifiers) {
        for (Widget child : children) {
            boolean pressed = child.keyReleased(keycode, scanCode, modifiers);
            if (pressed) return true;
        }
        return super.keyReleased(keycode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char character, int p_231042_2_) {
        for (Widget child : children) {
            boolean typed = child.charTyped(character, p_231042_2_);
            if (typed) return true;
        }
        return super.charTyped(character, p_231042_2_);
    }

    public void add(TooltipWidget widget) {
        children.add(widget);
    }

    public void reset() {
        children.clear();
    }
}
