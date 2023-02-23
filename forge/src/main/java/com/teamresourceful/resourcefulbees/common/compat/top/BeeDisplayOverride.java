package com.teamresourceful.resourcefulbees.common.compat.top;

import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.TopTranslations;
import mcjty.theoneprobe.api.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BeeDisplayOverride implements IEntityDisplayOverride {

    @Override
    public boolean overrideStandardInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, Entity entity, IProbeHitEntityData iProbeHitEntityData) {
        if (entity instanceof Bee bee) {
            createBeeProbData(iProbeInfo, bee, probeMode);
            return true;
        }
        return false;
    }

    private void createBeeProbData(IProbeInfo iProbeInfo, Bee beeEntity, ProbeMode probeMode) {
        iProbeInfo.horizontal()
                .entity(beeEntity)
                .vertical()
                .text(beeEntity.getDisplayName())
                .text(CompoundText.create().style(TextStyleClass.MODNAME).text(BeeConstants.MOD_NAME));

        if (probeMode.equals(ProbeMode.EXTENDED)) {
            iProbeInfo.text(displayFormattedFlowerPos(beeEntity))
                    .text(displayFormattedHivePos(beeEntity));
        }
    }

    private static Component displayFormattedFlowerPos(Bee beeEntity) {
        return beeEntity.getSavedFlowerPos() == null
                ? TopTranslations.FLOWER_POS_NONE
                : Component.translatable(TopTranslations.FLOWER_POS, beeEntity.getSavedFlowerPos().toShortString());
    }

    private static Component displayFormattedHivePos(Bee beeEntity) {
        return beeEntity.getHivePos() == null
                ? TopTranslations.HIVE_POS_NONE
                : Component.translatable(TopTranslations.HIVE_POS, beeEntity.getHivePos().toShortString());
    }
}

