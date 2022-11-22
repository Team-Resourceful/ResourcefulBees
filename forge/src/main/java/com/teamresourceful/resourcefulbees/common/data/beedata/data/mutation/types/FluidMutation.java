package com.teamresourceful.resourcefulbees.common.data.beedata.data.mutation.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.client.util.displays.FluidDisplay;
import com.teamresourceful.resourcefulbees.common.util.GenericSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record FluidMutation(Fluid fluid, double chance, double weight) implements MutationType, FluidDisplay {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public @Nullable BlockPos check(ServerLevel level, BlockPos pos) {
        for (int i = 0; i < 2; i++) {
            pos = pos.below(1);
            if (level.getFluidState(pos).is(fluid)){
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                return pos;
            }
        }
        return null;
    }

    @Override
    public boolean activate(ServerLevel level, BlockPos pos) {
        if (!level.getBlockState(pos).getMaterial().isReplaceable()) return false;
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
                Registry.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidMutation::fluid),
                Codec.doubleRange(0D, 1D).fieldOf("chance").orElse(1D).forGetter(FluidMutation::chance),
                Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").orElse(10D).forGetter(FluidMutation::weight)
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