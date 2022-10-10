package com.teamresourceful.resourcefulbees.client.gui.screen.locator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.client.FindBeePacket;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BeeLocatorScreen extends Screen {

    private final InteractionHand hand;
    private BeeListWidget listWidget;
    private Button selectButton;

    public BeeLocatorScreen(InteractionHand hand) {
        super(CommonComponents.EMPTY);
        this.hand = hand;
    }

    @Override
    protected void init() {
        super.init();

        this.listWidget = new BeeListWidget(this::setSelected, this.minecraft, this.width, this.height, 32, this.height - 32, 36);
        this.listWidget.updateEntries(BeeRegistry.getRegistry());

        var closeButton = new Button((this.width / 2) - 90, this.height - 26, 80, 20, TranslationConstants.BeeLocator.CANCEL, (button) -> this.onClose());

        this.selectButton = new Button((this.width / 2) + 10, this.height - 26, 80, 20, TranslationConstants.BeeLocator.SEARCH, (button) -> {
            getSelected().ifPresent(bee -> {
                String type = bee.getType();
                if (type != null) {
                    NetPacketHandler.CHANNEL.sendToServer(new FindBeePacket(type, this.hand));
                }
                this.onClose();
            });
        });

        this.selectButton.active = false;

        this.addRenderableWidget(listWidget);
        this.addRenderableWidget(selectButton);
        this.addRenderableWidget(closeButton);
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        Component title = Component.translatable(TranslationConstants.BeeLocator.SELECTED, this.getSelected().map(BeeLocatorEntry::getDisplayName).orElse(TranslationConstants.BeeLocator.NONE));
        GuiComponent.drawCenteredString(stack, this.font, title, this.width / 2, 11, 16777215);
    }

    public void setSelected(BeeLocatorEntry entry) {
        this.listWidget.setSelected(entry);
        this.selectButton.active = true;
    }

    public Optional<BeeLocatorEntry> getSelected() {
        return Optional.ofNullable(this.listWidget.getSelected());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
