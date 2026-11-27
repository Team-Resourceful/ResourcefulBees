package com.teamresourceful.resourcefulbees.common.entities.goals;

import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;
import com.teamresourceful.resourcefulbees.api.data.trait.abilities.ClingyAbility;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.registries.custom.TraitAbilityRegistry;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEffects;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class ClingyFollowGoal extends Goal {

    private final ResourcefulBee bee;
    private Player target;
    private ClingyAbility config;
    private int recalculatePath;

    public ClingyFollowGoal(ResourcefulBee bee) {
        this.bee = bee;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    private ClingyAbility getAbility() {
        TraitAbilityRegistry registry = TraitAbilityRegistry.getRegistry();

        for (Identifier id : bee.getTraitData().specialAbilities()) {
            TraitAbility ability = registry.getAbility(id);
            if (ability instanceof ClingyAbility clingy) {
                return clingy;
            }
        }

        return null;
    }

    private boolean isValidTarget(Player player) {
        return player != null
                && player.isAlive()
                && !player.isSpectator()
                && !player.isCreative()
                && !player.hasEffect(ModEffects.BEEKEEPERS_RESOLVE.holder());
    }

    private boolean isValidTarget(Entity entity) {
        return entity instanceof Player player && isValidTarget(player);
    }

    @Override
    public boolean canUse() {
        config = getAbility();
        if (config == null) return false;

        target = bee.level().getNearestPlayer(
                bee.getX(),
                bee.getY(),
                bee.getZ(),
                config.followRange(),
                this::isValidTarget
        );

        return target != null && bee.distanceToSqr(target) > Mth.square(config.startDistance());
    }

    @Override
    public boolean canContinueToUse() {
        config = getAbility();

        return config != null
                && isValidTarget(target)
                && bee.distanceToSqr(target) <= Mth.square(config.followRange())
                && bee.distanceToSqr(target) > Mth.square(config.stopDistance());
    }

    @Override
    public void start() {
        recalculatePath = 0;
    }

    @Override
    public void tick() {
        if (target == null || config == null) return;

        bee.getLookControl().setLookAt(target, 10.0F, 10.0F);

        if (--recalculatePath <= 0) {
            recalculatePath = 10;

            bee.getNavigation().moveTo(
                    target,
                    config.speedModifier()
            );
        }
    }

    @Override
    public void stop() {
        bee.getNavigation().stop();
        target = null;
        config = null;
    }
}