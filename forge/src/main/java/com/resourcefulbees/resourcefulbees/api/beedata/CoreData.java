package com.resourcefulbees.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CoreData {

    private final Set<Block> blockFlowers;
    private final Optional<ResourceLocation> entityFlower;
    private final int maxTimeInHive;
    private final String name;

    public CoreData(String name, Set<Block> blockFlowers, Optional<ResourceLocation> entityFlower, int maxTimeInHive){
        this.name = name;
        this.blockFlowers = blockFlowers;
        this.entityFlower = entityFlower;
        this.maxTimeInHive = maxTimeInHive;
    }

    public CoreData(String name) {
        this.name = name;
        this.blockFlowers = new HashSet<>();
        this.entityFlower = Optional.empty();
        this.maxTimeInHive = BeeConstants.MAX_TIME_IN_HIVE;
    }

    public Set<Block> getBlockFlowers() {
        return blockFlowers;
    }

    public Optional<ResourceLocation> getEntityFlower() {
        return entityFlower;
    }

    public int getMaxTimeInHive() {
        return maxTimeInHive;
    }

    public String getName() {
        return name;
    }

    public @Nullable EntityType<?> getFlowerEntityType() {
        return BeeInfoUtils.getEntityType(entityFlower.orElse(null));
    }

    public static CoreData createDefault() {
        return new CoreData("ERROR");
    }


    public static Codec<CoreData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("name").orElse(name).forGetter(CoreData::getName),
                CodecUtils.BLOCK_SET_CODEC.fieldOf("flower").orElse(new HashSet<>()).forGetter(CoreData::getBlockFlowers),
                ResourceLocation.CODEC.optionalFieldOf("entityFlower").forGetter(CoreData::getEntityFlower),
                Codec.intRange(600, Integer.MAX_VALUE).fieldOf("maxTimeInHive").orElse(2400).forGetter(CoreData::getMaxTimeInHive)
        ).apply(instance, CoreData::new));
    }
}
