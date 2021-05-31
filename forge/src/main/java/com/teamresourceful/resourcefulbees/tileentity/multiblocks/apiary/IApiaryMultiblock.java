package com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary;

import com.teamresourceful.resourcefulbees.lib.enums.ApiaryTab;
import net.minecraft.server.level.ServerPlayer;

public interface IApiaryMultiblock{
    void switchTab(ServerPlayer player, ApiaryTab tab);
}
