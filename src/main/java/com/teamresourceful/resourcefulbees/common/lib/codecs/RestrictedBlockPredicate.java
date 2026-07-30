package com.teamresourceful.resourcefulbees.common.lib.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.util.CodecUtils;
import com.teamresourceful.resourcefulbees.common.util.bytecodecs.StreamCodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.predicates.properties.BlockStatePredicate;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record RestrictedBlockPredicate(
        @NotNull Block block,
        Optional<DataComponentPatch> components,
        Optional<LocationPredicate> location,
        @NotNull BlockStatePredicate properties
) {

    public static final Codec<RestrictedBlockPredicate> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    BuiltInRegistries.BLOCK
                            .byNameCodec()
                            .fieldOf("id")
                            .forGetter(RestrictedBlockPredicate::block),

                    DataComponentPatch.CODEC
                            .optionalFieldOf("components")
                            .forGetter(RestrictedBlockPredicate::components),

                    LocationPredicate.CODEC
                            .optionalFieldOf("location")
                            .forGetter(RestrictedBlockPredicate::location),

                    BlockStatePredicate.CODEC
                            .fieldOf("properties")
                            .orElse(BlockStatePredicate.ANY)
                            .forGetter(RestrictedBlockPredicate::properties)
            ).apply(instance, RestrictedBlockPredicate::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RestrictedBlockPredicate> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.registry(BuiltInRegistries.BLOCK.key()),
                    RestrictedBlockPredicate::block,

                    ByteBufCodecs.optional(DataComponentPatch.STREAM_CODEC),
                    RestrictedBlockPredicate::components,

                    ByteBufCodecs.optional(StreamCodecExtras.LOCATION_PREDICATE_STREAM_CODEC),
                    RestrictedBlockPredicate::location,

                    StreamCodecExtras.BLOCK_STATE_PREDICATE_STREAM_CODEC,
                    RestrictedBlockPredicate::properties,

                    RestrictedBlockPredicate::new
            );

    public Optional<DataComponentPatch> getComponents() {
        return components;
    }

    public boolean matches(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);

        if (!state.is(this.block)) {
            return false;
        }

        if (!this.properties.matches(state)) {
            return false;
        }

        if (this.location.isPresent()
                && !this.location.get().matches(
                level,
                pos.getX(),
                pos.getY(),
                pos.getZ()
        )) {
            return false;
        }

        if (this.components.isEmpty()) {
            return true;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            return false;
        }

        return CodecUtils.matchesComponents(
                this.components.get(),
                blockEntity.collectComponents()
        );
    }
}