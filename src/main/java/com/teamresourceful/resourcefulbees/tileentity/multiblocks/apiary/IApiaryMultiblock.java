package com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary;

import com.teamresourceful.resourcefulbees.lib.enums.ApiaryTab;
import net.minecraft.entity.player.ServerPlayerEntity;

public interface IApiaryMultiblock{
    void switchTab(ServerPlayerEntity player, ApiaryTab tab);
}
