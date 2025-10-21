package com.teamresourceful.resourcefulbees.api.compat;

import com.teamresourceful.resourcefulbees.api.data.bee.BeeCombatData;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeCoreData;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeTraitData;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.BeeBreedData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.FamilyUnit;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.BeeMutationData;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeRenderData;
import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import net.minecraft.world.entity.AgeableMob;

import java.util.Optional;

/**
 * Implemented in CustomBeeEntity and ResourcefulBee
 * <p>
 * It is recommended to use ResourcefulBee (or a class that is child of it) when referred to ICustomBee
 */
public interface CustomBee {

    CustomBeeData getBeeData();

    default String getBeeType() {
        return getBeeData().name();
    }

    default BeeCoreData getCoreData() {
        return getBeeData().getCoreData();
    }

    default Optional<OutputVariation> getHoneycombData() {
        return getBeeData().getCoreData().getHoneycombData();
    }

    default BeeRenderData getRenderData() {
        return getBeeData().getRenderData();
    }

    default BeeBreedData getBreedData() {
        return getBeeData().getBreedData();
    }

    default BeeCombatData getCombatData() {
        return getBeeData().getCombatData();
    }

    default BeeMutationData getMutationData() {
        return getBeeData().getMutationData();
    }

    default BeeTraitData getTraitData() {
        return getBeeData().getTraitData();
    }

    /**
     * Gets "this" bee's current number of times fed.
     *
     * @return "This" bee's feed count.
     */
    int getFeedCount();

    /**
     * Resets "this" bee's current number of times fed.
     */
    void resetFeedCount();

    /**
     * Increments "this" bee's current number of times fed by 1.
     */
    void addFeedCount();

    AgeableMob createSelectedChild(FamilyUnit beeType);
}
