package com.teamresourceful.resourcefulbees.api.honeydata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class HoneyData {

    public static Codec<HoneyData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> name)).forGetter(HoneyData::getName),
                HoneyBottleData.codec(name).fieldOf("bottleData").orElseGet((Consumer<String>) s -> ResourcefulBees.LOGGER.error("bottleData is REQUIRED!"), null).forGetter(HoneyData::getBottleData),
                HoneyBlockData.codec(name).fieldOf("blockData").orElse(HoneyBlockData.getDefault(name)).forGetter(HoneyData::getBlockData),
                HoneyFluidData.codec(name).fieldOf("fluidData").orElse(HoneyFluidData.getDefault(name)).forGetter(HoneyData::getFluidData)
        ).apply(instance, HoneyData::new));
    }

    private final String name;
    private final HoneyBottleData bottleData;
    private final HoneyBlockData blockData;
    private final HoneyFluidData fluidData;

    public HoneyData(String name, HoneyBottleData bottleData, HoneyBlockData blockData, HoneyFluidData fluidData) {
        this.name = name;
        this.bottleData = bottleData;
        this.blockData = blockData;
        this.fluidData = fluidData;
    }

    public String getName() {
        return name;
    }

    public HoneyBottleData getBottleData() {
        return bottleData;
    }

    public HoneyBlockData getBlockData() {
        return blockData;
    }

    public HoneyFluidData getFluidData() {
        return fluidData;
    }

    public ResourceLocation getRegistryID() {
        return bottleData.getHoneyBottle().getId();
    }
}
