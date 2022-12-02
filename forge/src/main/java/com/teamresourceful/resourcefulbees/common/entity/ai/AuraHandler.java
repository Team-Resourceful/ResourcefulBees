package com.teamresourceful.resourcefulbees.common.entity.ai;

import com.teamresourceful.resourcefulbees.api.data.trait.Aura;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;

import java.util.List;
import java.util.Set;

public class AuraHandler {

    private final CustomBeeEntity bee;
    private final int auraRange;
    private final Set<Aura> auras;

    public AuraHandler(CustomBeeEntity bee) {
        this.bee = bee;
        this.auraRange = this.bee.getTraitData().auraRange();
        this.auras = this.bee.getTraitData().auras();
    }

    public void tick() {
        ServerLevel level = (ServerLevel)this.bee.level;
        List<ServerPlayer> players = level.getPlayers(this::isPlayerApplicable);

        for (Aura aura : auras) {
            if (cannotPerform(aura)) continue;
            for (ServerPlayer player : players) {
                aura.apply(player, CommonConfig.AURA_FREQUENCY.get() * 20);
            }
            Aura.spawnParticles(this.bee, aura.type().particle);
        }
    }

    private boolean isPlayerApplicable(ServerPlayer player) {
        return player.gameMode.isSurvival() && !bee.isAlliedTo(player) && bee.position().closerThan(player.position(), auraRange);
    }

    private boolean cannotPerform(Aura aura) {
        return isCalmed(aura) || isPeaceful(aura);
    }

    private boolean isCalmed(Aura aura) {
        return this.bee.hasEffect(ModEffects.CALMING.get()) && aura.calmingDisabled();
    }

    private boolean isPeaceful(Aura aura) {
        return !aura.isBeneficial() && this.bee.level.getDifficulty() == Difficulty.PEACEFUL;
    }
}
