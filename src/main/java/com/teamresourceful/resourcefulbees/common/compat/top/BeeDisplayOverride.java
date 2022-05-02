package com.teamresourceful.resourcefulbees.common.compat.top;

import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import mcjty.theoneprobe.api.*;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BeeDisplayOverride implements IEntityDisplayOverride {

    @Override
    public boolean overrideStandardInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, Entity entity, IProbeHitEntityData iProbeHitEntityData) {
        return entity instanceof Bee bee && createBeeProbData(iProbeInfo, bee, probeMode);
    }

    private boolean createBeeProbData(IProbeInfo iProbeInfo, Bee beeEntity, ProbeMode probeMode) {
        iProbeInfo.horizontal()
                .entity(beeEntity)
                .vertical()
                .text(beeEntity.getDisplayName())
                .text(CompoundText.create().style(TextStyleClass.MODNAME).text(BeeConstants.MOD_NAME));

        if (probeMode.equals(ProbeMode.EXTENDED)) {
            iProbeInfo.text(new TextComponent("Flower Pos: ").append(beeEntity.getSavedFlowerPos() == null ? "none" : beeEntity.getSavedFlowerPos().toShortString()))
                    .text(new TextComponent("Hive Pos: ").append(beeEntity.getHivePos() == null ? "none" : beeEntity.getHivePos().toShortString()));
        }
        return true;
    }
}

