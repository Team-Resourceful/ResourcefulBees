package com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary;

import com.resourcefulbees.resourcefulbees.lib.ApiaryTabs;
import net.minecraft.entity.player.ServerPlayerEntity;

public interface IApiaryMultiblock{
    void switchTab(ServerPlayerEntity player, ApiaryTabs tab);
}
