package com.teamresourceful.resourcefulbees.api.beedata.mutation.types;

import com.teamresourceful.resourcefulbees.common.util.GenericSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface Mutation {

    /**
     * @param level the level to check.
     * @param pos the position at which its starting.
     * @return returns the position at which it checked and was successful and null if unsuccessful.
     */
    @Nullable
    BlockPos check(ServerLevel level, BlockPos pos);

    /**
     * @return returns true of can execute and false if can not.
     */
    boolean activate(ServerLevel level, BlockPos pos);

    double chance();

    double weight();

    Optional<CompoundTag> tag();

    GenericSerializer<Mutation> serializer();
}
