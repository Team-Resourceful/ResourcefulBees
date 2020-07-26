package com.dungeonderps.resourcefulbees.tileentity;

import com.dungeonderps.resourcefulbees.lib.ApiaryTabs;
import net.minecraft.entity.player.ServerPlayerEntity;

public interface IApiaryMultiblock{
    void switchTab(ServerPlayerEntity player, ApiaryTabs tab);
}
