package com.teamresourceful.resourcefulbees.client.component;

import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefullib.client.components.ImageButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Function;

public class BeepediaMainButton extends ImageButton {

    public static final ResourceLocation HOME_BUTTONS = new ResourceLocation(BeeConstants.MOD_ID, "textures/gui/beepedia/home_buttons.png");

    private final int column;

    private final Consumer<BeepediaMainButton> onClick;
    private final Function<BeepediaMainButton, Boolean> activeChecker;

    public BeepediaMainButton(int x, int y, int column, Consumer<BeepediaMainButton> onClick) {
        this(x, y, column, onClick, AbstractWidget::isActive);
    }

    public BeepediaMainButton(int x, int y, int column, Consumer<BeepediaMainButton> onClick, Function<BeepediaMainButton, Boolean> activeChecker) {
        super(x, y, 20, 20);
        this.column = column;
        this.onClick = onClick;
        this.activeChecker = activeChecker;

        this.imageWidth = 60;
        this.imageHeight = 60;
    }

    @Override
    public ResourceLocation getTexture(int mouseX, int mouseY) {
        return HOME_BUTTONS;
    }

    @Override
    public int getU(int mouseX, int mouseY) {
        return column * 20;
    }

    @Override
    public int getV(int mouseX, int mouseY) {
        return Boolean.FALSE.equals(activeChecker.apply(this)) ? 40 : this.isHoveredOrFocused() ? 20 : 0;
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return this.activeChecker.apply(this) && super.isValidClickButton(button);
    }

    @Override
    public void onPress() {
        this.onClick.accept(this);
    }
}
