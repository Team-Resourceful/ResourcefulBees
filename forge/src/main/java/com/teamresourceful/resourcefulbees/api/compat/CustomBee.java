package com.teamresourceful.resourcefulbees.api.compat;

import com.teamresourceful.resourcefulbees.api.data.bee.BeeCombatData;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeCoreData;
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

    /**
     * Gets "this" bee's type.
     *
     *  @return "This" bee's type.
     */
    String getBeeType();

    BeeCoreData getCoreData();

    Optional<OutputVariation> getHoneycombData();

    BeeRenderData getRenderData();

    BeeBreedData getBreedData();

    BeeCombatData getCombatData();

    BeeMutationData getMutationData();

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
