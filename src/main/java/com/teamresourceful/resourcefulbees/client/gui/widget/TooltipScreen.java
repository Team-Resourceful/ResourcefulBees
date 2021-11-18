package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TooltipScreen extends Screen {

    protected final Set<TooltipWidget> widgets = new HashSet<>();
    private int ticksOpen = 0;

    protected TooltipScreen(ITextComponent pTitle) {
        super(pTitle);
    }

    @Override
    protected void init() {
        super.init();
        widgets.clear();
    }

    @OverridingMethodsMustInvokeSuper
    public void renderChild(MatrixStack matrix, Widget child, int mouseX, int mouseY, float partialTicks) {
        child.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public void render(@NotNull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {

        renderBackground(matrix, 0);
        drawBackground(matrix, mouseX, mouseY, partialTicks);

        //move the matrix origin to the top left corner of scroll area

        for (Widget child : widgets) {
            renderChild(matrix, child, mouseX, mouseY, partialTicks);
        }

        drawTooltips(matrix, mouseX, mouseY);
    }

    protected abstract void drawBackground(MatrixStack matrix, int mouseX, int mouseY, float partialTicks);

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        for (Widget child : widgets) {
            boolean scrolled = child.mouseScrolled(mouseX, mouseY, scrollAmount);
            if (scrolled) return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (TooltipWidget child : widgets) {
            if (mouseButton == 0) {
                boolean flag = child.isClicked(mouseX, mouseY);
                if (flag) {
                    child.playDownSound(Minecraft.getInstance().getSoundManager());
                    child.onClick(mouseX, mouseY);
                    return true;
                }
            }
            boolean clicked = child.mouseClicked(mouseX, mouseY, mouseButton);
            if (clicked) return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        for (Widget child : widgets) {
            boolean released = child.mouseReleased(mouseX, mouseY, mouseButton);
            child.onRelease(mouseX, mouseY);
            if (released) return true;
        }
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int p_231045_5_, double p_231045_6_, double p_231045_8_) {
        for (TooltipWidget child : widgets) {
            boolean dragged = child.mouseDragged(mouseX, mouseY, p_231045_5_, p_231045_6_, p_231045_8_);
            if (dragged) return true;
        }
        return super.mouseDragged(mouseX, mouseY, p_231045_5_, p_231045_6_, p_231045_8_);
    }

    private void drawTooltips(MatrixStack matrix, int mouseX, int mouseY) {
        widgets.forEach(t -> t.drawTooltips(matrix, this, mouseX, mouseY));
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        widgets.forEach(c -> c.mouseMoved(mouseX, mouseY));
    }

    @Override
    public boolean keyPressed(int keycode, int scanCode, int modifiers) {
        for (Widget child : widgets) {
            boolean pressed = child.keyPressed(keycode, scanCode, modifiers);
            if (pressed) return true;
        }
        return super.keyPressed(keycode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keycode, int scanCode, int modifiers) {
        for (Widget child : widgets) {
            boolean pressed = child.keyReleased(keycode, scanCode, modifiers);
            if (pressed) return true;
        }
        return super.keyReleased(keycode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char character, int p_231042_2_) {
        for (Widget child : widgets) {
            boolean typed = child.charTyped(character, p_231042_2_);
            if (typed) return true;
        }
        return super.charTyped(character, p_231042_2_);
    }

    @Override
    public void tick() {
        super.tick();
        ticksOpen++;
        widgets.forEach(c -> c.tick(ticksOpen));
    }

    public void add(TooltipWidget widget) {
        widgets.add(widget);
    }

    public void reset() {
        widgets.clear();
    }

    public static boolean mouseHovering(float x, float y, int width, int height, int mouseX, int mouseY) {
        return mouseX > x && mouseY > y && mouseX < x + width && mouseY < y + height;
    }

    public <T extends TooltipWidget> @NotNull T addWidget(@NotNull T widget) {
        widgets.add(widget);
        return widget;
    }

    public <T extends TooltipWidget> void addButtons(List<@NotNull T> widgets) {
        widgets.forEach(this::addWidget);
    }

    public <T extends TooltipWidget> void addButtons(@NotNull T... widgets) {
        addButtons(Arrays.asList(widgets));
    }

    public static <T extends TooltipWidget> void setButtonsVisibility(boolean visible, List<@NotNull T> widgets) {
        widgets.forEach(w -> w.visible = visible);
    }

    public static <T extends TooltipWidget> void setButtonsActive(boolean active, List<@NotNull T> widgets) {
        widgets.forEach(w -> w.active = active);
    }

    public static <T extends TooltipWidget> void setButtonsVisibility(boolean b, @NotNull T... widgets) {
        setButtonsVisibility(b, Arrays.asList(widgets));
    }

    public static <T extends TooltipWidget> void setButtonsActive(boolean b, @NotNull T... widgets) {
        setButtonsActive(b, Arrays.asList(widgets));
    }

}
