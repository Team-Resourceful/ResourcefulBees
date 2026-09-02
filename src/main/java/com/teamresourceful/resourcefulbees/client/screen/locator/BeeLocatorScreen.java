package com.teamresourceful.resourcefulbees.client.screen.locator;

import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeeLocatorTranslations;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.FindBeePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class BeeLocatorScreen extends Screen {

    private final int slot;

    private BeeListWidget listWidget;
    private Button selectButton;

    public BeeLocatorScreen(int slot) {
        super(CommonComponents.EMPTY);
        this.slot = slot;
    }

    @Override
    protected void init() {
        super.init();

        this.listWidget = new BeeListWidget(this::setSelected, this.minecraft, this.width, this.height, 32, this.height - 32, 36);
        this.listWidget.updateEntries(BeeRegistry.get());

        Button closeButton = Button.builder(BeeLocatorTranslations.CANCEL, button -> this.onClose())
                .bounds((this.width / 2) - 90, this.height - 26, 80, 20)
                .build();

        this.selectButton = Button.builder(BeeLocatorTranslations.SEARCH, button -> getSelected().ifPresent(entry -> {
            Identifier bee = entry.getType();

            if (bee != null) {
                NetworkHandler.NETWORK.sendToServer(new FindBeePacket(bee, this.slot));
            }

            this.onClose();
        })).bounds((this.width / 2) + 10, this.height - 26, 80, 20).build();

        this.selectButton.active = false;

        this.addRenderableWidget(this.listWidget);
        this.addRenderableWidget(this.selectButton);
        this.addRenderableWidget(closeButton);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        Component title = Component.translatable(BeeLocatorTranslations.SELECTED, getSelected().map(BeeLocatorEntry::getDisplayName).orElse(BeeLocatorTranslations.NONE));
        graphics.centeredText(this.font, title, this.width / 2, 11, 0xFFFFFFFF);
    }

    public void setSelected(BeeLocatorEntry entry) {
        this.listWidget.setSelected(entry);
        this.selectButton.active = true;
    }

    public Optional<BeeLocatorEntry> getSelected() {
        return Optional.ofNullable(
                this.listWidget.getSelected()
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static void openScreen(Player player, InteractionHand hand) {
        int slot = player.getInventory().getSelectedSlot();

        if (hand == InteractionHand.OFF_HAND) {
            slot = player.getInventory().getContainerSize() - 1;
        }

        Minecraft.getInstance().gui.setScreen(new BeeLocatorScreen(slot));
    }
}