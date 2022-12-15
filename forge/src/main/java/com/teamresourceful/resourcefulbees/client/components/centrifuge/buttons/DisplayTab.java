package com.teamresourceful.resourcefulbees.client.components.centrifuge.buttons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.utils.TextUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefullib.client.components.ImageButton;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DisplayTab extends ImageButton {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/centrifuges/display_tab.png");

    private final ControlPanelTabs type;
    private final Supplier<Boolean> isSelected;
    private final Supplier<Boolean> isEnabled;
    protected final Runnable onPress;
    private final boolean showArrow;

    public DisplayTab(int x, int y, ControlPanelTabs type, Supplier<Boolean> isSelected, Supplier<Boolean> isEnabled, Runnable onPress, boolean showArrow) {
        super(x+1, y+1, 69, 13);
        this.imageWidth = 71;
        this.imageHeight = 45;
        this.type = type;
        this.isSelected = isSelected;
        this.isEnabled = isEnabled;
        this.onPress = onPress;
        this.showArrow = showArrow;
    }

    /**
     *  Use this to show the arrow on the button and enable by default
     */
    public DisplayTab(int x, int y, ControlPanelTabs type, Supplier<Boolean> isSelected, Runnable onPress) {
        this(x, y, type, isSelected, () -> true, onPress, true);
    }

    @Override
    public void renderButton(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.bindTexture(getTexture(mouseX, mouseY));
        blit(stack, this.x-1, this.y-1, getU(mouseX, mouseY), getV(mouseX, mouseY), this.width+2, this.height+2, this.imageWidth, this.imageHeight);
        int color;
        if (isEnabled()) {
            if (isSelected()) {
                color = TextUtils.FONT_COLOR_2;
            } else if (isHovered) {
                color = TextUtils.FONT_COLOR_3;
            } else {
                color = TextUtils.FONT_COLOR_1;
            }
        } else {
            color = TextUtils.FONT_COLOR_4;
        }
        TextUtils.TERMINAL_FONT_8.draw(stack, type.label, x+4f, y+6f, color);
    }

    public boolean isSelected() {
        return isSelected.get();
    }

    public boolean isEnabled() {
        return isEnabled.get();
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
        if (!isEnabled() || !showArrow) return 0;
        if (isSelected()) {
            return 30;
        }

        return isHovered ? 15 : 0;
    }

    @Override
    public void onPress() {
        if (isEnabled() && !isSelected()) onPress.run();
    }
}
