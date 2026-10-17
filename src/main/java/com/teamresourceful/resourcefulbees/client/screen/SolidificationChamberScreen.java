package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.client.screen.base.CustomContainerScreen;
import com.teamresourceful.resourcefulbees.client.screen.base.CustomWidgets;
import com.teamresourceful.resourcefulbees.common.blockentities.SolidificationChamberBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.menus.SolidificationChamberMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SolidificationChamberScreen extends CustomContainerScreen<SolidificationChamberMenu, SolidificationChamberBlockEntity> {

    public SolidificationChamberScreen(SolidificationChamberMenu menu, Inventory inventory, Component displayName) {
        super(menu, inventory, displayName, 180, 200);

        this.background = ModIdentifier.of("textures/gui/containers/solidifier.png");
        this.titleLabelX = 7;
        this.inventoryLabelX = 7;
        this.inventoryLabelY = 103;
    }

    @Override
    protected void init() {
        super.init();

        this.putAt(CustomWidgets.tank(this.block()::tankData).withSize(14, 56), 63, 26);
    }
}
