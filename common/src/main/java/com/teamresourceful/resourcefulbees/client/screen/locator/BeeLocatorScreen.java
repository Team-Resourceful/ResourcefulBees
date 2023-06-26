package com.teamresourceful.resourcefulbees.client.screen.locator;

import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeeLocatorTranslations;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.FindBeePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

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

        var closeButton = Button.builder(BeeLocatorTranslations.CANCEL, button -> this.onClose())
            .bounds((this.width / 2) - 90, this.height - 26, 80, 20)
            .build();

        this.selectButton = Button.builder(BeeLocatorTranslations.SEARCH, button -> getSelected().ifPresent(bee -> {
                String type = bee.getType();
                if (type != null) {
                    NetworkHandler.CHANNEL.sendToServer(new FindBeePacket(type, this.slot));
                }
                this.onClose();
            }))
            .bounds((this.width / 2) + 10, this.height - 26, 80, 20)
            .build();

        this.selectButton.active = false;

        this.addRenderableWidget(listWidget);
        this.addRenderableWidget(selectButton);
        this.addRenderableWidget(closeButton);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        Component title = Component.translatable(BeeLocatorTranslations.SELECTED, this.getSelected().map(BeeLocatorEntry::getDisplayName).orElse(BeeLocatorTranslations.NONE));
        graphics.drawCenteredString(this.font, title, this.width / 2, 11, 16777215);
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

    public static void openScreen(Player player, InteractionHand hand) {
        int slot = player.getInventory().selected;
        if (hand == InteractionHand.OFF_HAND) {
            slot = player.getInventory().getContainerSize() - 1;
        }
        Minecraft.getInstance().setScreen(new BeeLocatorScreen(slot));
    }
}
