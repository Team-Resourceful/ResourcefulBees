package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.lib.ApiaryTabs;
import net.minecraft.entity.player.ServerPlayerEntity;

public interface IApiaryMultiblock{
    void switchTab(ServerPlayerEntity player, ApiaryTabs tab);
}
