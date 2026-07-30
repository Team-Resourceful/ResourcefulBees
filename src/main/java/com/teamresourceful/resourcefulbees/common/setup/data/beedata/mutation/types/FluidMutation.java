package com.teamresourceful.resourcefulbees.common.setup.data.beedata.mutation.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.client.util.displays.FluidDisplay;
import com.teamresourceful.resourcefulbees.common.util.GenericSerializer;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record FluidMutation(Fluid fluid, double chance, double weight) implements MutationType, FluidDisplay {

    public static final GenericSerializer<FluidMutation> SERIALIZER = new Serializer();

    @Override
    public @Nullable BlockPos check(ServerLevel level, BlockPos pos) {
        for (int i = 0; i < 2; i++) {
            pos = pos.below(1);
            if (level.getFluidState(pos).is(fluid)) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                return pos;
            }
        }
        return null;
    }

    @Override
    public boolean activate(ServerLevel level, BlockPos pos) {
        if (!level.getBlockState(pos).canBeReplaced()) return false;
        level.setBlock(pos, fluid.defaultFluidState().createLegacyBlock(), Block.UPDATE_ALL);
        return true;
    }

    @Override
    public Optional<DataComponentPatch> components() {
        return Optional.empty();
    }

    @Override
    public GenericSerializer<FluidMutation> serializer() {
        return SERIALIZER;
    }

    @Override
    public Fluid displayedFluid() {
        return fluid;
    }

    private static class Serializer implements GenericSerializer<FluidMutation> {

        private static final MapCodec<FluidMutation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidMutation::fluid),
            CodecExtras.DOUBLE_UNIT_INTERVAL.optionalFieldOf("chance", 1D).forGetter(FluidMutation::chance),
            CodecExtras.NON_NEGATIVE_DOUBLE.optionalFieldOf("weight", 10D).forGetter(FluidMutation::weight)
        ).apply(instance, FluidMutation::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FluidMutation> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.registry(BuiltInRegistries.FLUID.key()),
                        FluidMutation::fluid,

                        ByteBufCodecs.DOUBLE,
                        FluidMutation::chance,

                        ByteBufCodecs.DOUBLE,
                        FluidMutation::weight,

                        FluidMutation::new
                );

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FluidMutation> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<FluidMutation> codec() {
            return CODEC;
        }

        @Override
        public String id() {
            return "fluid";
        }
    }
}