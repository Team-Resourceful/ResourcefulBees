package com.teamresourceful.resourcefulbees.api.beedata.mutation.types;

import com.google.gson.JsonElement;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Bee;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface IMutation {

    /**
     * @param level the level to check.
     * @param bee the bee which this operation is happening on.
     * @param pos the position at which its starting.
     * @return returns the position at which it checked and was successful and null if unsuccessful.
     */
    @Nullable
    BlockPos check(ServerLevel level, Bee bee, BlockPos pos);

    /**
     * @return returns true of can execute and false if can not.
     */
    boolean activate(ServerLevel level, Bee bee, BlockPos pos);

    double chance();

    double weight();

    Optional<CompoundTag> tag();

    JsonElement toJson();
}
