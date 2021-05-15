package com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary;

import com.teamresourceful.resourcefulbees.lib.ApiaryTabs;
import net.minecraft.server.level.ServerPlayer;

public interface IApiaryMultiblock{
    void switchTab(ServerPlayer player, ApiaryTabs tab);
}
