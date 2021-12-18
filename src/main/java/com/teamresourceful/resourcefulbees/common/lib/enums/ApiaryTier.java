package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.blockentity.ApiaryBlockEntity;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.IExtensibleEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum ApiaryTier implements IExtensibleEnum, StringRepresentable {
    T1_APIARY("t1_apiary", 8, 0.8, () -> CommonConfig.T1_APIARY_OUTPUT.get(), () -> CommonConfig.T1_APIARY_QUANTITY.get(), ModBlockEntityTypes.T1_APIARY_ENTITY, ModItems.T1_APIARY_ITEM),
    T2_APIARY("t2_apiary", 12, 0.7, () -> CommonConfig.T2_APIARY_OUTPUT.get(), () -> CommonConfig.T2_APIARY_QUANTITY.get(), ModBlockEntityTypes.T2_APIARY_ENTITY, ModItems.T2_APIARY_ITEM),
    T3_APIARY("t3_apiary", 16, 0.6, () -> CommonConfig.T3_APIARY_OUTPUT.get(), () -> CommonConfig.T3_APIARY_QUANTITY.get(), ModBlockEntityTypes.T3_APIARY_ENTITY, ModItems.T3_APIARY_ITEM),
    T4_APIARY("t4_apiary", 20, 0.5, () -> CommonConfig.T4_APIARY_OUTPUT.get(), () -> CommonConfig.T4_APIARY_QUANTITY.get(), ModBlockEntityTypes.T4_APIARY_ENTITY, ModItems.T4_APIARY_ITEM);

    public static final Codec<ApiaryTier> CODEC = IExtensibleEnum.createCodecForExtensibleEnum(ApiaryTier::values, ApiaryTier::byName);
    private static final Map<String, ApiaryTier> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(ApiaryTier::getName, tier -> tier));
    private final String name;
    private final int maxBees;
    private final double timeModifier;
    private final Supplier<ApiaryOutputType> outputType;
    private final Supplier<Integer> outputAmount;
    private final Supplier<BlockEntityType<? extends ApiaryBlockEntity>> blockEntityType;
    private final Supplier<Item> item;

    ApiaryTier(String name, int maxBees, double timeModifier, Supplier<ApiaryOutputType> outputType, Supplier<Integer> outputAmount, Supplier<BlockEntityType<? extends ApiaryBlockEntity>> blockEntityType, Supplier<Item> item) {
        this.name = name;
        this.maxBees = maxBees;
        this.timeModifier = timeModifier;
        this.outputType = outputType;
        this.outputAmount = outputAmount;
        this.blockEntityType = blockEntityType;
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public int getMaxBees() {
        return maxBees;
    }

    public double getTimeModifier() {
        return timeModifier;
    }

    public ApiaryOutputType getOutputType() {
        return outputType.get();
    }

    public Integer getOutputAmount() {
        return outputAmount.get();
    }

    public BlockEntityType<? extends ApiaryBlockEntity> getBlockEntityType() {
        return blockEntityType.get();
    }

    public Item getItem() {
        return item.get();
    }

    public static ApiaryTier byName(String s) {
        return BY_NAME.get(s);
    }

    @SuppressWarnings("unused")
    public static ApiaryTier create(String name, String id, int maxBees, double timeModifier, Supplier<ApiaryOutputType> outputType, Supplier<Integer> outputAmount, Supplier<BlockEntityType<? extends ApiaryBlockEntity>> blockEntityType, Supplier<Item> item) {
        throw new IllegalStateException("Enum not extended");
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
