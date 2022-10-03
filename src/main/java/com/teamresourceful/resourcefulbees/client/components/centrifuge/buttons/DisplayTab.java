package com.teamresourceful.resourcefulbees.client.components.centrifuge.buttons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.client.utils.TextUtils;
import com.teamresourceful.resourcefullib.client.components.ImageButton;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DisplayTab extends ImageButton {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/centrifuges/display_tab.png");

    private final ControlPanelTabs type;
    private final Supplier<Boolean> isSelected;
    private final Runnable onPress;

    public DisplayTab(int x, int y, ControlPanelTabs type, Supplier<Boolean> isSelected, Runnable onPress) {
        super(x+1, y+1, 69, 13);
        this.imageWidth = 71;
        this.imageHeight = 45;
        this.type = type;
        this.isSelected = isSelected;
        this.onPress = onPress;
    }

    @Override
    public void renderButton(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.bindTexture(getTexture(mouseX, mouseY));
        blit(stack, this.x-1, this.y-1, getU(mouseX, mouseY), getV(mouseX, mouseY), this.width+2, this.height+2, this.imageWidth, this.imageHeight);
        int color;
        if (isSelected()) {
            color = TextUtils.FONT_COLOR_2;
        } else if (isHovered) {
            color = 0xffc7bf2c;
        } else {
            color = TextUtils.FONT_COLOR_1;
        }
        TextUtils.TERMINAL_FONT_8.draw(stack, type.label, x+4f, y+6f, color);
    }

    public boolean isSelected() {
        return isSelected.get();
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
        if (isSelected()) {
            return 30;
        }

        return isHovered ? 15 : 0;
    }

    @Override
    public void onPress() {
        onPress.run();
    }
}
