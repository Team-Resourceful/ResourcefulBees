package com.teamresourceful.resourcefulbees.platform.common.entity.forge;

import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Event;

public class DespawnHandlerImpl {

    public static void checkDespawn(CustomBeeEntity bee, RandomSource random, boolean despawnInPeaceful) {
        if (bee.level.getDifficulty() == Difficulty.PEACEFUL && despawnInPeaceful) {
            bee.discard();
        } else if (!bee.isPersistenceRequired() && !bee.requiresCustomPersistence()) {
            Player entity = bee.level.getNearestPlayer(bee, -1.0);
            Event.Result result = ForgeEventFactory.canEntityDespawn(bee);
            if (result == Event.Result.DENY) {
                bee.setNoActionTime(0);
                bee.setHasHiveInRange(false);
                entity = null;
            } else if (result == Event.Result.ALLOW) {
                bee.discard();
                entity = null;
            }
            if (entity != null) {
                int i = bee.getType().getCategory().getDespawnDistance();
                double d = entity.distanceToSqr(bee);
                if (d > (i * i) && bee.removeWhenFarAway(d)) {
                    bee.discard();
                }
                int k = bee.getType().getCategory().getNoDespawnDistance();
                int l = k * k;
                if (bee.getNoActionTime() > 600 && random.nextInt(800) == 0 && d > l && bee.removeWhenFarAway(d)) {
                    bee.discard();
                } else if (d < l) {
                    bee.setNoActionTime(0);
                }
            }
        } else {
            bee.setNoActionTime(0);
            bee.setHasHiveInRange(false);
        }
    }
}
