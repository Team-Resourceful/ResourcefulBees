package com.teamresourceful.resourcefulbees.client.screen.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.mixin.client.ScreenInvoker;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.screens.HistoryScreen;
import com.teamresourceful.resourcefullib.client.screens.ScreenHistory;
import com.teamresourceful.resourcefullib.client.screens.TooltipProvider;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class SubdividedScreen extends HistoryScreen implements TooltipProvider {

    private final List<Widget> renderables = new ArrayList<>();

    protected int screenWidth;
    protected int screenHeight;
    protected int subX;
    protected int subY;

    private Screen subScreen;

    protected SubdividedScreen(Component title, int width, int height, int subX, int subY, UnaryOperator<Screen> subScreenFactory) {
        this(title, width, height, subX, subY);
        this.subScreen = subScreenFactory.apply(this);
        if (this.subScreen instanceof ScreenInvoker invoker) {
            invoker.callInit();
        }
    }

    protected SubdividedScreen(Component title, int width, int height, int subX, int subY) {
        super(title);
        this.screenWidth = width;
        this.screenHeight = height;
        this.subX = subX;
        this.subY = subY;
    }

    public int left() {
        return this.width == 0 ? 0 : (this.width - this.screenWidth) / 2;
    }

    public int top() {
        return this.height == 0 ? 0 : (this.height - this.screenHeight) / 2;
    }

    public void setSubScreen(@Nullable Screen subScreen) {
        if (subScreen instanceof ScreenHistory screenHistory) {
            screenHistory.setLastScreen(this.subScreen);
        }
        setSubScreenNow(subScreen);
    }

    public void forceSubScreen(@Nullable Screen screen) {
        this.subScreen = screen;
    }

    public void setSubScreenNow(Screen screen) {
        forceSubScreen(screen);
        if (this.subScreen instanceof ScreenInvoker invoker) {
            invoker.callInit();
        }
    }

    @Nullable
    public Screen getSubScreen() {
        return subScreen;
    }

    public int subLeft() {
        return left() + subX;
    }

    public int subTop() {
        return top() + subY;
    }

    public void renderScreen(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    //region Event Listeners
    @Override
    public void render(@NotNull PoseStack stackIn, int mouseX, int mouseY, float partialTicks) {
        try (var stack = new CloseablePoseStack(stackIn)) {
            stack.translate(left(), top(), 0);
            renderScreen(stackIn, mouseX - left(), mouseY - top(), partialTicks);
            if (getSubScreen() != null) {
                stack.translate(subX, subY, 0);
                getSubScreen().render(stackIn, mouseX - subLeft(), mouseY - subTop(), partialTicks);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX - left(), mouseY - top(), button) || (getSubScreen() != null && getSubScreen().mouseClicked(mouseX - subLeft(), mouseY - subTop(), button));
    }

    @Override
    public @NotNull Optional<GuiEventListener> getChildAt(double x, double y) {
        return super.getChildAt(x, y).or(() -> Optional.ofNullable(getSubScreen()).flatMap(screen -> screen.getChildAt(x - subLeft(), y - subTop())));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers) || (getSubScreen() != null && getSubScreen().keyPressed(keyCode, scanCode, modifiers));
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(keyCode, scanCode, modifiers) || (getSubScreen() != null && getSubScreen().keyReleased(keyCode, scanCode, modifiers));
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return super.charTyped(codePoint, modifiers) || (getSubScreen() != null && getSubScreen().charTyped(codePoint, modifiers));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX - left(), mouseY - top(), button, dragX - left(), dragY - top()) || (getSubScreen() != null && getSubScreen().mouseDragged(mouseX - subLeft(), mouseY - subTop(), button, dragX - subLeft(), dragY - subTop()));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        return super.mouseScrolled(mouseX - left(), mouseY - top(), scrollAmount) || (getSubScreen() != null && getSubScreen().mouseScrolled(mouseX - subLeft(), mouseY - subTop(), scrollAmount));
    }

    @Override
    public void tick() {
        super.tick();
        if (getSubScreen() != null) {
            getSubScreen().tick();
        }
    }
    //endregion

    //region Screen History

    @Override
    public @Nullable Screen getLastScreen() {
        if (this.subScreen instanceof ScreenHistory screenHistory) {
            Screen lastScreen1 = screenHistory.getLastScreen();
            if (lastScreen1 != null) {
                forceSubScreen(lastScreen1);
                return null;
            }
        }
        return super.getLastScreen();
    }

    @Override
    public boolean canGoBack() {
        return (this.subScreen instanceof ScreenHistory screenHistory && screenHistory.canGoBack()) || super.canGoBack();
    }
    //endregion

    @Override
    public @NotNull List<Component> getTooltip(int mouseX, int mouseY) {
        List<Component> components = TooltipProvider.getTooltips(this.renderables, mouseX, mouseY);
        if (getSubScreen() instanceof TooltipProvider provider) {
            components.addAll(provider.getTooltip(mouseX - subLeft(), mouseY - subTop()));
        }
        return components;
    }

    @Override
    protected <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T widget) {
        this.renderables.add(widget);
        return super.addRenderableWidget(widget);
    }

    @Override
    protected <T extends Widget> T addRenderableOnly(T widget) {
        this.renderables.add(widget);
        return super.addRenderableOnly(widget);
    }
}
