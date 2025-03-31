package com.teamresourceful.resourcefulbees.client.component.search;

import com.teamresourceful.resourcefulbees.client.screen.beepedia.state.BeepediaState;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefullib.client.components.ImageButton;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class SearchButton extends ImageButton {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/beepedia/search_buttons.png");

    private final BeepediaState state;
    private final BeepediaState.Sorting type;
    private final Runnable onPress;

    public SearchButton(int x, int y, BeepediaState state, BeepediaState.Sorting type, Runnable onPress) {
        super(x, y, 13, 13);
        this.state = state;
        this.type = type;
        this.onPress = onPress;

        this.imageWidth = 128;
        this.imageHeight = 128;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
        int u = this.type.u + (this.state.getSorting(this.type).isTrue() ? 0 : 13);
        int v = 39 + (this.state.getSorting(this.type).isDefined() ? 26 : this.isHovered ? 13 : 0);
        graphics.blit(getTexture(mouseX, mouseY), this.getX(), this.getY(), u, v, 13, 13, 128, 128);

        if (isHovered()) {
            String name = this.type.name().toLowerCase(Locale.ROOT);
            Component sortingState = switch (this.state.getSorting(this.type)) {
                case TRUE -> Component.translatable(GuiTranslations.SORT_TRUE, name);
                case FALSE -> Component.translatable(GuiTranslations.SORT_FALSE, name);
                case UNDEFINED -> Component.translatable(GuiTranslations.SORT_UNSET, name);
            };

            ScreenUtils.setTooltip(List.of(Component.translatable(GuiTranslations.SORT_BY, name), sortingState));
        }
    }

    @Override
    public ResourceLocation getTexture(int mouseX, int mouseY) {
        return TEXTURE;
    }

    @Override
    public int getU(int mouseX, int mouseY) {
        return this.state.getSorting(this.type).isUndefined() ? 13 : 0;
    }

    @Override
    public int getV(int mouseX, int mouseY) {
        return this.state.getSorting(this.type).isDefined() ? 26 : this.isHovered ? 13 : 0;
    }

    @Override
    public void onPress() {
        TriState next = switch (this.state.getSorting(this.type)) {
            case TRUE -> TriState.FALSE;
            case FALSE -> TriState.UNDEFINED;
            case UNDEFINED -> TriState.TRUE;
        };
        this.state.setSorting(this.type, next);
        this.onPress.run();
    }
}
