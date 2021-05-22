package com.teamresourceful.resourcefulbees.api.beedata;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.lib.BeeConstants;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.utils.color.Color;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Immutable
public class CoreData {
    public static final CoreData DEFAULT = new CoreData("error");

    private final Set<Block> blockFlowers;
    private final Optional<ResourceLocation> entityFlower;
    private final Optional<String> lore;
    private final Optional<String> creator;
    private final int maxTimeInHive;
    private final String name;
    private final Color loreColor;

    public CoreData(String name, Set<Block> blockFlowers, Optional<ResourceLocation> entityFlower, int maxTimeInHive, Optional<String> lore, Optional<String> creator, Color loreColor){
        this.name = name;
        this.blockFlowers = blockFlowers;
        this.entityFlower = entityFlower;
        this.maxTimeInHive = maxTimeInHive;
        this.lore = lore;
        this.creator = creator;
        this.loreColor = loreColor;
    }

    public CoreData(String name) {
        this.name = name;
        this.blockFlowers = new HashSet<>();
        this.entityFlower = Optional.empty();
        this.maxTimeInHive = BeeConstants.MAX_TIME_IN_HIVE;
        this.lore = Optional.empty();
        this.creator = Optional.empty();
        this.loreColor = Color.DEFAULT;
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

    public Optional<String> getLore() {
        return lore;
    }

    public Optional<String> getCreator() {
        return creator;
    }

    public Color getLoreColor() {
        return loreColor;
    }

    public Style getLoreColorStyle() {
        return Style.EMPTY.withColor(loreColor.getTextColor());
    }

    public @Nullable EntityType<?> getFlowerEntityType() {
        return BeeInfoUtils.getEntityType(entityFlower.orElse(null));
    }

    public static Codec<CoreData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("name").orElse(name).forGetter(CoreData::getName),
                CodecUtils.BLOCK_SET_CODEC.fieldOf("flower").orElse(Sets.newHashSet(Blocks.POPPY)).forGetter(CoreData::getBlockFlowers),
                ResourceLocation.CODEC.optionalFieldOf("entityFlower").forGetter(CoreData::getEntityFlower),
                Codec.intRange(600, Integer.MAX_VALUE).fieldOf("maxTimeInHive").orElse(2400).forGetter(CoreData::getMaxTimeInHive),
                Codec.STRING.optionalFieldOf("lore").forGetter(CoreData::getLore),
                Codec.STRING.optionalFieldOf("creator").forGetter(CoreData::getCreator),
                Color.CODEC.fieldOf("loreColor").orElse(Color.DEFAULT).forGetter(CoreData::getLoreColor)
        ).apply(instance, CoreData::new));
    }
}
