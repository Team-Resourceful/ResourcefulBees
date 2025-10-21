package com.teamresourceful.resourcefulbees.common.setup.data.beedata.breeding;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.BeeBreedData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.FamilyUnit;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
import com.teamresourceful.resourcefullib.common.codecs.tags.HolderSetCodec;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public record BreedData(
        Set<FamilyUnit> families,
        HolderSet<Item> feedItems, Optional<ItemStack> feedReturnItem, int feedAmount,
        int childGrowthDelay, int breedDelay
) implements BeeBreedData {

    private static final HolderSet<Item> DEFAULT_FEED_ITEM = HolderSet.direct(Item::builtInRegistryHolder, Items.POPPY);
    private static final BeeBreedData DEFAULT = new BreedData(Collections.emptySet(), DEFAULT_FEED_ITEM, Optional.empty(), 0, 0, 0);
    public static final BeeDataSerializer<BeeBreedData> SERIALIZER = BeeDataSerializer.of(new ModResourceLocation("breeding"), 1, BreedData::codec, DEFAULT);

    private static Codec<BeeBreedData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                CodecExtras.set(BeeFamilyUnit.codec(name)).optionalFieldOf("parents", new HashSet<>()).forGetter(BeeBreedData::families),
                HolderSetCodec.of(BuiltInRegistries.ITEM).optionalFieldOf("feedItem", DEFAULT_FEED_ITEM).forGetter(BeeBreedData::feedItems),
                ItemStackCodec.CODEC.optionalFieldOf("feedReturnItem").forGetter(BeeBreedData::feedReturnItem),
                CodecExtras.POSITIVE_INT.optionalFieldOf("feedAmount", 1).forGetter(BeeBreedData::feedAmount),
                CodecExtras.NON_POSITIVE_INT.optionalFieldOf("childGrowthDelay", BeeConstants.CHILD_GROWTH_DELAY).forGetter(BeeBreedData::childGrowthDelay),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("breedDelay", BeeConstants.BREED_DELAY).forGetter(BeeBreedData::breedDelay)
        ).apply(instance, BreedData::new));
    }

    @Override
    public boolean hasParents() {
        return !families.isEmpty();
    }

    @Override
    public BeeDataSerializer<BeeBreedData> serializer() {
        return SERIALIZER;
    }
}
