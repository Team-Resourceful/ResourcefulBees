package com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.terminal.output;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.AbstractInfoPanel;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractCentrifugeOutputEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.client.utils.TextUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class TerminalOutputHomePanel<T extends AbstractCentrifugeOutputEntity<?, ?>> extends AbstractInfoPanel<T> {

    private final Class<T> clazz;

    public TerminalOutputHomePanel(int x, int y, Class<T> clazz, boolean displayTitleBar) {
        super(x, y, displayTitleBar);
        this.clazz = clazz;
        init();
    }

    public TerminalOutputHomePanel(int x, int y, Class<T> clazz) {
        this(x, y, clazz, true);
    }

    @Override
    public void updateSelectedEntity(AbstractGUICentrifugeEntity selectedEntity) {
        this.selectedEntity = clazz.cast(selectedEntity);
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (selectedEntity == null) return;
        super.render(stack, mouseX, mouseY, partialTicks);
        int tX = x+14;
        int tY = y+14;

        drawLocationString(stack, CentrifugeUtils.formatBlockPos(selectedEntity.getBlockPos()), tX+6, tY+16);
    }

    private static void drawLocationString(PoseStack stack, String location, int x, int y) {
        drawString(stack, Component.translatable(CentrifugeTranslations.LOCATION, location), x, y);
    }

    private static void drawString(PoseStack stack, Component component, int x, int y) {
        TextUtils.TERMINAL_FONT_8.draw(stack, component, x, y, TextUtils.FONT_COLOR_1);
    }
}
