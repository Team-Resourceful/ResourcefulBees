package com.teamresourceful.resourcefulbees.client.screen.base;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class RenderingScreen extends Screen {

    private final List<Renderable> renderables = new ArrayList<>();

    protected RenderingScreen(Component component) {
        super(component);
    }

    @Override
    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
        this.renderables.add(widget);
        return super.addRenderableWidget(widget);
    }

    @Override
    protected <T extends Renderable> T addRenderableOnly(T widget) {
        this.renderables.add(widget);
        return widget;
    }

    public List<Renderable> getRenderables() {
        return renderables;
    }
}
