package com.teamresourceful.resourcefulbees.common.compat.top;

import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import mcjty.theoneprobe.api.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

@SuppressWarnings("SameReturnValue")
public class BeeDisplayOverride implements IEntityDisplayOverride {
    @Override
    public boolean overrideStandardInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, Entity entity, IProbeHitEntityData iProbeHitEntityData) {
        return entity instanceof BeeEntity && createBeeProbData(iProbeInfo, (BeeEntity) entity, probeMode);
    }

    private boolean createBeeProbData(IProbeInfo iProbeInfo, BeeEntity beeEntity, ProbeMode probeMode) {
        iProbeInfo.horizontal()
                .entity(beeEntity)
                .vertical()
                .text(beeEntity.getDisplayName())
                .text(CompoundText.create().style(TextStyleClass.MODNAME).text(BeeConstants.MOD_NAME));

        if (probeMode.equals(ProbeMode.EXTENDED)) {
            iProbeInfo.text(new StringTextComponent("Flower Pos: ").append(beeEntity.getSavedFlowerPos() == null ? "none" : beeEntity.getSavedFlowerPos().toShortString()))
                    .text(new StringTextComponent("Hive Pos: ").append(beeEntity.getHivePos() == null ? "none" : beeEntity.getHivePos().toShortString()));
        }
        return true;
    }
}
