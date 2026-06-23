package com.teamresourceful.resourcefulbees.common.setup.data.beedata;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeCoreData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.api.data.shared.RegistryPredicate;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public record CoreData(
        String honeycomb,
        RegistryPredicate<Block> blockFlowers,
        RegistryPredicate<EntityType<?>> entityFlowers,
        int maxTimeInHive,
        List<Component> lore
) implements BeeCoreData {

    private static final RegistryPredicate<Block> DEFAULT_FLOWERS = RegistryPredicate.create(Block::builtInRegistryHolder, Blocks.POPPY);
    private static final BeeCoreData DEFAULT = new CoreData("", RegistryPredicate.empty(), RegistryPredicate.empty(), BeeConstants.MAX_TIME_IN_HIVE, new ArrayList<>());
    private static final Codec<BeeCoreData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("honeycombVariation", "").forGetter(BeeCoreData::honeycomb),
            RegistryPredicate.codec(BuiltInRegistries.BLOCK).optionalFieldOf("flower", DEFAULT_FLOWERS).forGetter(BeeCoreData::blockFlowers),
            RegistryPredicate.codec(BuiltInRegistries.ENTITY_TYPE).optionalFieldOf("entityFlower", RegistryPredicate.empty()).forGetter(BeeCoreData::entityFlowers),
            Codec.intRange(600, Integer.MAX_VALUE).optionalFieldOf("maxTimeInHive", 2400).forGetter(BeeCoreData::maxTimeInHive),
            ComponentSerialization.CODEC.listOf().optionalFieldOf("lore", Lists.newArrayList()).forGetter(BeeCoreData::lore)
    ).apply(instance, CoreData::new));

    public static final BeeDataSerializer<BeeCoreData> SERIALIZER = BeeDataSerializer.of(ModIdentifier.of("core"), 1, id -> CODEC, DEFAULT);

    @Override
    public BeeDataSerializer<BeeCoreData> serializer() {
        return SERIALIZER;
    }
}
