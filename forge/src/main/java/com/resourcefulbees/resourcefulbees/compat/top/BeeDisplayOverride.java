package com.resourcefulbees.resourcefulbees.compat.top;

import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import mcjty.theoneprobe.api.*;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@SuppressWarnings("SameReturnValue")
public class BeeDisplayOverride implements IEntityDisplayOverride {
    @Override
    public boolean overrideStandardInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player playerEntity, Level world, Entity entity, IProbeHitEntityData iProbeHitEntityData) {
        return entity instanceof Bee && createBeeProbData(iProbeInfo, (Bee) entity, probeMode);
    }

    private boolean createBeeProbData(IProbeInfo iProbeInfo, Bee beeEntity, ProbeMode probeMode) {
        iProbeInfo.horizontal()
                .entity(beeEntity)
                .vertical()
                .text(beeEntity.getDisplayName())
                .text(CompoundText.create().style(TextStyleClass.MODNAME).text(BeeConstants.MOD_NAME));

        if (probeMode.equals(ProbeMode.EXTENDED)) {
            iProbeInfo.text(new TextComponent("Flower Pos: ").append(beeEntity.savedFlowerPos == null ? "none" : beeEntity.savedFlowerPos.toShortString()))
                    .text(new TextComponent("Hive Pos: ").append(beeEntity.hivePos == null ? "none" : beeEntity.hivePos.toShortString()));
        }
        return true;
    }
}
