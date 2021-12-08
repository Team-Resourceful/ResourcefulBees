package com.teamresourceful.resourcefulbees.api;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.api.beedata.CombatData;
import com.teamresourceful.resourcefulbees.api.beedata.CoreData;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BeeFamily;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BreedData;
import com.teamresourceful.resourcefulbees.api.beedata.centrifuge.CentrifugeData;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.MutationData;
import com.teamresourceful.resourcefulbees.api.beedata.render.RenderData;
import com.teamresourceful.resourcefulbees.api.beedata.spawning.SpawnData;
import com.teamresourceful.resourcefulbees.api.honeycombdata.OutputVariation;
import net.minecraft.world.entity.AgeableMob;

import java.util.Optional;

/**
 * Implemented in CustomBeeEntity and ResourcefulBee
 *
 * It is recommended to use ResourcefulBee (or a class that is child of it) when referred to ICustomBee
 */
public interface ICustomBee {

    /**
     * Gets "this" bee's type.
     *
     *  @return "This" bee's type.
     */
    String getBeeType();

    /**
     * Gets "this" bee's information card from the BEE_INFO hashmap.
     *
     *  @return "This" bee's info card.
     */
    JsonObject getRawBeeData();

    CoreData getCoreData();

    Optional<OutputVariation> getHoneycombData();

    RenderData getRenderData();

    BreedData getBreedData();

    CentrifugeData getCentrifugeData();

    CombatData getCombatData();

    MutationData getMutationData();

    SpawnData getSpawnData();

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

    AgeableMob createSelectedChild(BeeFamily beeType);
}
