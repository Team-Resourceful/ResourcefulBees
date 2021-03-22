package com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary;

import com.resourcefulbees.resourcefulbees.lib.ApiaryTabs;
import net.minecraft.server.level.ServerPlayer;

public interface IApiaryMultiblock{
    void switchTab(ServerPlayer player, ApiaryTabs tab);
}
