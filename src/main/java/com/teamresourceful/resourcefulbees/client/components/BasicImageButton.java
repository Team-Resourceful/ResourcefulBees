package com.teamresourceful.resourcefulbees.client.components;

import com.teamresourceful.resourcefulbees.client.components.base.ImageButton;
import net.minecraft.resources.ResourceLocation;

public class BasicImageButton extends ImageButton {

    private final ResourceLocation texture;
    private final int u;
    private final int v;
    private boolean selected;

    public BasicImageButton(int x, int y, int u, int v, boolean selected, ResourceLocation texture) {
        super(x, y, 20, 20);
        this.u = u;
        this.v = v;
        this.selected = selected;
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTexture(int mouseX, int mouseY) {
        return this.texture;
    }

    @Override
    public int getU(int mouseX, int mouseY) {
        return selected ? u + 20 : u;
    }

    @Override
    public int getV(int mouseX, int mouseY) {
        return isHovered ? v + 20 : v;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void onPress() {
        setSelected(!this.selected);
    }
}
