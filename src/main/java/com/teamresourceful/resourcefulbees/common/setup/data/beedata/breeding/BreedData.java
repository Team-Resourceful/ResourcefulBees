package com.teamresourceful.resourcefulbees.common.setup.data.beedata.breeding;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.BeeBreedData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.FamilyUnit;
import com.teamresourceful.resourcefulbees.api.data.shared.RegistryPredicate;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public record BreedData(
        Set<FamilyUnit> families,
        RegistryPredicate<Item> feedItems, Optional<ItemStackTemplate> feedReturnItem, int feedAmount,
        int childGrowthDelay, int breedDelay
) implements BeeBreedData {

    private static final RegistryPredicate<Item> DEFAULT_FEED_ITEM = RegistryPredicate.create(Item::builtInRegistryHolder, Items.POPPY);
    private static final BeeBreedData DEFAULT = new BreedData(Collections.emptySet(), DEFAULT_FEED_ITEM, Optional.empty(), 0, 0, 0);
    public static final BeeDataSerializer<BeeBreedData> SERIALIZER = BeeDataSerializer.of(ModIdentifier.of("breeding"), 1, BreedData::codec, DEFAULT);

    private static Codec<BeeBreedData> codec(Identifier name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                CodecExtras.set(BeeFamilyUnit.codec(name)).optionalFieldOf("families", new HashSet<>()).forGetter(BeeBreedData::families),
                RegistryPredicate.codec(BuiltInRegistries.ITEM).optionalFieldOf("feedItems", DEFAULT_FEED_ITEM).forGetter(BeeBreedData::feedItems),
                ItemStackTemplate.CODEC.optionalFieldOf("feedReturnItem").forGetter(BeeBreedData::feedReturnItem),
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
