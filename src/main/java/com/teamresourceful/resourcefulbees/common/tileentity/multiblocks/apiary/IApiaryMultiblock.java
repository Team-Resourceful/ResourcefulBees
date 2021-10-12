package com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary;

import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTab;
import net.minecraft.entity.player.ServerPlayerEntity;

public interface IApiaryMultiblock{
    void switchTab(ServerPlayerEntity player, ApiaryTab tab);
}
