package com.teamresourceful.resourcefulbees.common.data.beedata;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeCoreData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.tags.HolderSetCodec;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
        List<MutableComponent> lore
) implements BeeCoreData {

    private static final BeeCoreData DEFAULT = new CoreData("", HolderSet.direct(), HolderSet.direct(), BeeConstants.MAX_TIME_IN_HIVE, new ArrayList<>());
    private static final Codec<BeeCoreData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("honeycombVariation").orElse("").forGetter(BeeCoreData::honeycomb),
            HolderSetCodec.of(Registry.BLOCK).fieldOf("flower").orElse(HolderSet.direct(Block::builtInRegistryHolder, Blocks.POPPY)).forGetter(BeeCoreData::flowers),
            HolderSetCodec.of(Registry.ENTITY_TYPE).fieldOf("entityFlower").orElse(HolderSet.direct()).forGetter(BeeCoreData::entityFlowers),
            Codec.intRange(600, Integer.MAX_VALUE).fieldOf("maxTimeInHive").orElse(2400).forGetter(BeeCoreData::maxTimeInHive),
            CodecExtras.passthrough(Component.Serializer::toJsonTree, Component.Serializer::fromJson).listOf().fieldOf("lore").orElse(Lists.newArrayList()).forGetter(BeeCoreData::lore)
    ).apply(instance, CoreData::new));

    public static final BeeDataSerializer<BeeCoreData> SERIALIZER = BeeDataSerializer.of(new ModResourceLocation("core"), 1, id -> CODEC, DEFAULT);

    @Override
    public BeeDataSerializer<BeeCoreData> serializer() {
        return SERIALIZER;
    }
}
