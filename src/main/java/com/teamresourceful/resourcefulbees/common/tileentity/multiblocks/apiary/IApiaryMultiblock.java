package com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary;

import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTab;
import net.minecraft.server.level.ServerPlayer;

public interface IApiaryMultiblock{
    void switchTab(ServerPlayer player, ApiaryTab tab);
}
