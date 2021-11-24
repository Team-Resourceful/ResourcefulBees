package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
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

    private final int screenWidth;
    private final int screenHeight;
    public int x;
    public int y;

    protected final Set<TooltipWidget> widgets = new HashSet<>();
    private int ticksOpen = 0;

    protected TooltipScreen(ITextComponent pTitle, int screenWidth, int screenHeight) {
        super(pTitle);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - screenWidth) / 2;
        this.y = (this.height - screenHeight) / 2;
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
        matrix.pushPose();
        matrix.translate(x, y, 0);
        for (Widget child : widgets) {
            renderChild(matrix, child, mouseX, mouseY, partialTicks);
            matrix.pushPose();
            matrix.translate(0, 0, 100);
            matrix.scale(0.5f, 0.5f, 1.0f);
            Minecraft.getInstance().font.draw(matrix,"[" + child.x + ", " + child.y + "]", child.x * 2, child.y * 2 - 9, Color.DEFAULT.getValue());
            matrix.popPose();
        }
        matrix.popPose();

        drawTooltips(matrix, mouseX, mouseY);

        font.draw(matrix,"[" + mouseX + ", " + mouseY + "]", mouseX, mouseY, Color.DEFAULT.getValue());
    }

    protected abstract void drawBackground(MatrixStack matrix, int mouseX, int mouseY, float partialTicks);

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        for (Widget child : widgets) {
            boolean scrolled = child.mouseScrolled(mouseX - x, mouseY - y, scrollAmount);
            if (scrolled) return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (TooltipWidget child : widgets) {
            if (mouseButton == 0) {
                boolean flag = child.isClicked(mouseX - x, mouseY - y);
                if (flag) {
                    child.playDownSound(Minecraft.getInstance().getSoundManager());
                    child.onClick(mouseX, mouseY);
                    return true;
                }
            }
            boolean clicked = child.mouseClicked(mouseX - x, mouseY - y, mouseButton);
            if (clicked) return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        for (Widget child : widgets) {
            boolean released = child.mouseReleased(mouseX - x, mouseY - y, mouseButton);
            child.onRelease(mouseX - x, mouseY - y);
            if (released) return true;
        }
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int pButton, double pDragX, double pDragY) {
        for (TooltipWidget child : widgets) {
            boolean dragged = child.mouseDragged(mouseX - x, mouseY - y, pButton, pDragX, pDragY);
            if (dragged) return true;
        }
        return super.mouseDragged(mouseX, mouseY, pButton, pDragX, pDragY);
    }

    private void drawTooltips(MatrixStack matrix, int mouseX, int mouseY) {
        widgets.forEach(c -> c.drawTooltips(matrix, this, mouseX, mouseY));
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        widgets.forEach(c -> c.mouseMoved(mouseX - x, mouseY - y));
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
    public boolean charTyped(char character, int pModifiers) {
        for (Widget child : widgets) {
            boolean typed = child.charTyped(character, pModifiers);
            if (typed) return true;
        }
        return super.charTyped(character, pModifiers);
    }

    @Override
    public void tick() {
        super.tick();
        ticksOpen++;
        widgets.forEach(c -> c.tick(ticksOpen));
    }

    public void addAll(TooltipWidget widget) {
        widgets.add(widget);
        widget.addParent(x, y, null);
    }

    public void reset() {
        widgets.clear();
    }

    public static boolean mouseHovering(float x, float y, int width, int height, int mouseX, int mouseY) {
        return mouseX > x && mouseY > y && mouseX < x + width && mouseY < y + height;
    }

    public <T extends TooltipWidget> @NotNull T add(@NotNull T widget) {
        widgets.add(widget);
        widget.addParent(x, y, null);
        return widget;
    }

    public <T extends TooltipWidget> void addAll(List<@NotNull T> widgets) {
        widgets.forEach(this::add);
    }

    public <T extends TooltipWidget> void addAll(@NotNull T... widgets) {
        addAll(Arrays.asList(widgets));
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
