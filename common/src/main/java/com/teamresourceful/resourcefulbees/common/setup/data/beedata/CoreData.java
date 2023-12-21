package com.teamresourceful.resourcefulbees.common.setup.data.beedata;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeCoreData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefullib.common.codecs.tags.HolderSetCodec;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public record CoreData(
        String honeycomb,
        HolderSet<Block> flowers,
        HolderSet<EntityType<?>> entityFlowers,
        int maxTimeInHive,
        List<Component> lore
) implements BeeCoreData {

    private static final HolderSet<Block> DEFAULT_FLOWERS = HolderSet.direct(Block::builtInRegistryHolder, Blocks.POPPY);
    private static final BeeCoreData DEFAULT = new CoreData("", HolderSet.direct(), HolderSet.direct(), BeeConstants.MAX_TIME_IN_HIVE, new ArrayList<>());
    private static final Codec<BeeCoreData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("honeycombVariation", "").forGetter(BeeCoreData::honeycomb),
            HolderSetCodec.of(BuiltInRegistries.BLOCK).optionalFieldOf("flower", DEFAULT_FLOWERS).forGetter(BeeCoreData::flowers),
            HolderSetCodec.of(BuiltInRegistries.ENTITY_TYPE).optionalFieldOf("entityFlower", HolderSet.direct()).forGetter(BeeCoreData::entityFlowers),
            Codec.intRange(600, Integer.MAX_VALUE).optionalFieldOf("maxTimeInHive", 2400).forGetter(BeeCoreData::maxTimeInHive),
            ExtraCodecs.COMPONENT.listOf().optionalFieldOf("lore", Lists.newArrayList()).forGetter(BeeCoreData::lore)
    ).apply(instance, CoreData::new));

    public static final BeeDataSerializer<BeeCoreData> SERIALIZER = BeeDataSerializer.of(new ModResourceLocation("core"), 1, id -> CODEC, DEFAULT);

    @Override
    public BeeDataSerializer<BeeCoreData> serializer() {
        return SERIALIZER;
    }
}
