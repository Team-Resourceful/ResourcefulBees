package com.teamresourceful.resourcefulbees.common.setup.data.honeydata;

import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;
import com.teamresourceful.resourcefulbees.api.data.honey.HoneyBlockData;
import com.teamresourceful.resourcefulbees.api.data.honey.base.HoneyData;
import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyBottleData;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import com.teamresourceful.resourcefulbees.common.setup.data.honeydata.bottle.CustomHoneyBottleData;
import com.teamresourceful.resourcefulbees.common.setup.data.honeydata.fluid.CustomHoneyFluidData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record HoneyDataImpl(String name, Map<ResourceLocation, HoneyData<?>> data) implements CustomHoneyData {

    @Override
    public MutableComponent displayName() {
        return Component.translatable("honey_type.resourcefulbees." + name());
    }

    @Override
    public HoneyBlockData getBlockData() {
        return getData(CustomHoneyBlockData.SERIALIZER);
    }

    @Override
    public HoneyFluidData getFluidData() {
        return getData(CustomHoneyFluidData.SERIALIZER);
    }

    @Override
    public HoneyBottleData getBottleData() {
        return getData(CustomHoneyBottleData.SERIALIZER);
    }
}
