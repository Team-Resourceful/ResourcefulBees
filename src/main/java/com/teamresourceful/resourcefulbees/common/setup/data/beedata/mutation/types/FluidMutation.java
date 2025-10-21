package com.teamresourceful.resourcefulbees.common.setup.data.beedata.mutation.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.client.util.displays.FluidDisplay;
import com.teamresourceful.resourcefulbees.common.util.GenericSerializer;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record FluidMutation(Fluid fluid, double chance, double weight) implements MutationType, FluidDisplay {

    public static final GenericSerializer<MutationType> SERIALIZER = new Serializer();

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
    public Optional<CompoundTag> tag() {
        return Optional.empty();
    }

    @Override
    public GenericSerializer<MutationType> serializer() {
        return SERIALIZER;
    }

    @Override
    public Fluid displayedFluid() {
        return fluid;
    }

    private static class Serializer implements GenericSerializer<MutationType> {

        public static final Codec<FluidMutation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidMutation::fluid),
            CodecExtras.DOUBLE_UNIT_INTERVAL.optionalFieldOf("chance", 1D).forGetter(FluidMutation::chance),
            CodecExtras.NON_NEGATIVE_DOUBLE.optionalFieldOf("weight", 10D).forGetter(FluidMutation::weight)
        ).apply(instance, FluidMutation::new));

        @Override
        public Codec<FluidMutation> codec() {
            return CODEC;
        }

        @Override
        public String id() {
            return "fluid";
        }
    }
}