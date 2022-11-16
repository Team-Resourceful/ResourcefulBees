package com.teamresourceful.resourcefulbees.common.lib.builders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryOutputType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public record ApiaryTier(ResourceLocation id, int max, double time, Supplier<ApiaryOutputType> output, IntSupplier amount, Supplier<BlockEntityType<? extends BlockEntity>> blockEntity, Supplier<? extends Item> item) {

    private static final Map<ResourceLocation, ApiaryTier> TIERS = new HashMap<>();
    public static final Codec<ApiaryTier> CODEC = ResourceLocation.CODEC.comapFlatMap(ApiaryTier::get, ApiaryTier::id);

    public ApiaryTier {
        if (TIERS.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate Apiary Tier: " + id);
        }
        TIERS.put(id, this);
    }

    public Item getItem() {
        return item.get();
    }

    public BlockEntityType<?> getBlockEntityType() {
        return blockEntity.get();
    }


    public static DataResult<ApiaryTier> get(ResourceLocation id) {
        if (TIERS.containsKey(id)) {
            return DataResult.success(TIERS.get(id));
        }
        return DataResult.error("Unknown Beehive Tier: " + id);
    }

    public static Collection<ApiaryTier> values() {
        return TIERS.values();
    }

    public static class Builder {
        private int max;
        private double time;
        private Supplier<ApiaryOutputType> output;
        private IntSupplier amount;
        private Supplier<BlockEntityType<? extends BlockEntity>> blockEntity;
        private Supplier<? extends Item> item;

        public Builder max(int max) {
            this.max = max;
            return this;
        }

        public Builder time(double time) {
            this.time = time;
            return this;
        }

        public Builder output(Supplier<ApiaryOutputType> output) {
            this.output = output;
            return this;
        }

        public Builder amount(IntSupplier amount) {
            this.amount = amount;
            return this;
        }

        public Builder blockEntity(Supplier<BlockEntityType<? extends BlockEntity>> blockEntity) {
            this.blockEntity = blockEntity;
            return this;
        }

        public Builder item(Supplier<? extends Item> item) {
            this.item = item;
            return this;
        }

        public ApiaryTier build(ResourceLocation id) {
            return new ApiaryTier(id, max, time, output, amount, blockEntity, item);
        }
    }

}
