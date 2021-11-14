package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge;

import com.google.common.collect.Lists;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeTerminalContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CentrifugeTerminalScreen extends BaseCrentrifugeScreen<CentrifugeTerminalContainer> {

    private final CentrifugeTier tier;
    private final CentrifugeState initialState;

    //TODO we need to synchronize the inputs/outputs lists to the client somehow so that players can assign the
    // links between them. I'm not sure yet on how we want to handle this considering the complexity of the data
    public CentrifugeTerminalScreen(CentrifugeTerminalContainer pMenu, PlayerInventory pPlayerInventory, ITextComponent pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.tier = pMenu.getTier();
        this.initialState = (CentrifugeState) pMenu.getGuiPacket();
    }

    @Override
    @Nullable List<ITextComponent> getInfoTooltip() {
        return Lists.newArrayList(new StringTextComponent("INFO TEXT"));
    }

    @Override
    void closeScreen() {
        if (minecraft != null) minecraft.setScreen(null);
    }


}
