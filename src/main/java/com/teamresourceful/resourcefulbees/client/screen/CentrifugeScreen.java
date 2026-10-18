package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.screen.base.CustomContainerScreen;
import com.teamresourceful.resourcefulbees.client.screen.base.CustomWidgets;
import com.teamresourceful.resourcefulbees.common.blockentities.CentrifugeBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.menus.CentrifugeMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CentrifugeScreen extends CustomContainerScreen<CentrifugeMenu, CentrifugeBlockEntity> {

    public CentrifugeScreen(CentrifugeMenu menu, Inventory inventory, Component displayName) {
        super(menu, inventory, displayName, 180, 200);

        this.background = ModIdentifier.of("textures/gui/containers/centrifuge.png");
        this.titleLabelX = 7;
        this.inventoryLabelX = 7;
        this.inventoryLabelY = 103;
    }

    @Override
    protected void init() {
        super.init();

        for (var i = 0; i < CentrifugeBlockEntity.TANKS; i++) {
            final int index = i;
            this.addRenderableWidget(
                    CustomWidgets.tank(() -> this.block().tankData().get(index))
                            .withSize(14, 56)
                            .withPosition(leftPos + 147 + i * 15, topPos + 26)
            );
        }
    }
}