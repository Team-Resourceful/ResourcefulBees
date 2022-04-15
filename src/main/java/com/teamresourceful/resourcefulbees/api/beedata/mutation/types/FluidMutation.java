package com.teamresourceful.resourcefulbees.api.beedata.mutation.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.display.IFluidRender;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record FluidMutation(Fluid fluid, double chance, double weight) implements IMutation, IFluidRender {

    public static final Codec<FluidMutation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidMutation::fluid),
            Codec.doubleRange(0D, 1D).fieldOf("chance").orElse(1D).forGetter(FluidMutation::chance),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").orElse(10D).forGetter(FluidMutation::weight)
    ).apply(instance, FluidMutation::new));

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
    public JsonElement toJson() {
        Optional<JsonElement> json = CODEC.encodeStart(JsonOps.INSTANCE, this).result();
        if (json.isEmpty() || !(json.get() instanceof JsonObject jsonObject)) return JsonNull.INSTANCE;
        jsonObject.addProperty("type", "fluid");
        return jsonObject;
    }

    @Override
    public FluidStack fluidRender() {
        return new FluidStack(fluid, 1000);
    }
}