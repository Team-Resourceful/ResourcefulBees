package com.teamresourceful.resourcefulbees.common.lib.templates;

import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.CombatData;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.CoreData;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.TradeData;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.breeding.BeeFamilyUnit;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.breeding.BreedData;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.mutation.MutationData;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.mutation.types.BlockMutation;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.mutation.types.ItemMutation;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.rendering.ColorData;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.rendering.LayerData;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.rendering.LayerTexture;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.rendering.RenderData;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.traits.TraitData;
import com.teamresourceful.resourcefulbees.common.lib.enums.LayerEffect;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import com.teamresourceful.resourcefullib.common.codecs.predicates.RestrictedBlockPredicate;
import com.teamresourceful.resourcefullib.common.codecs.predicates.RestrictedItemPredicate;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.color.ConstantColors;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.*;

public class DummyBeeData {

    private DummyBeeData() {
        throw new UtilityClassError();
    }

    private static final MutationType MUTATION_INPUT = new BlockMutation(new RestrictedBlockPredicate(Blocks.COAL_BLOCK, NbtPredicate.ANY, LocationPredicate.ANY, StatePropertiesPredicate.ANY), 0.65, 35);

    private static final WeightedCollection<MutationType> MUTATION_OUTPUT = new WeightedCollection<MutationType>().add(25, new ItemMutation(new RestrictedItemPredicate(Items.DIAMOND, NbtPredicate.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.exactly(2)), 1.0, 25));

    private static ItemStack getBucketWithNbt() {
        ItemStack bucket = Items.BUCKET.getDefaultInstance();
        CompoundTag nbt = new CompoundTag();
        nbt.putString("this_is", "an_nbt_tag");
        bucket.setTag(nbt);
        return bucket;
    }

    public static final Map<ResourceLocation, BeeData<?>> DATA = Map.of(
            CoreData.SERIALIZER.id(), new CoreData(
                    "dummy_honeycomb",
                    HolderSet.direct(Block::builtInRegistryHolder, Blocks.DIAMOND_BLOCK, Blocks.EMERALD_BLOCK),
                    HolderSet.direct(EntityType::builtInRegistryHolder, EntityType.COW, EntityType.SHEEP),
                    4000,
                    List.of(Component.literal("This is a bee template"),
                            Component.literal("This bee is a dummy and does not exist"),
                            Component.literal("Let's hope this works...")
                    )
            ),
            RenderData.SERIALIZER.id(), new RenderData(
                    Set.of(
                            new LayerData(Color.RAINBOW, LayerTexture.MISSING_TEXTURE, LayerEffect.NONE, false, 5),
                            new LayerData(ConstantColors.cyan, LayerTexture.MISSING_TEXTURE, LayerEffect.GLOW, false, 5)
                    ),
                    new ColorData(ConstantColors.antiquewhite, ConstantColors.chocolate, ConstantColors.cadetblue),
                    new ModResourceLocation("geo/base.geo.json"),
                    LayerTexture.MISSING_TEXTURE,
                    new ModResourceLocation("animations/bee.animation.json"),
                    1.0f
            ),
            BreedData.SERIALIZER.id(), new BreedData(
                    Set.of(
                            BeeFamilyUnit.of(75, 0.67, "diamond", "coal", "compressed_diamond"),
                            BeeFamilyUnit.of(24, 0.67, "diamond", "diamond", "compressed_diamond")
                    ),
                    HolderSet.direct(Item::builtInRegistryHolder, Items.POPPY, Items.CARROT),
                    Optional.of(getBucketWithNbt()),
                    2,
                    -45_000,
                    2_400
            ),
            CombatData.SERIALIZER.id(), Objects.requireNonNull(CombatData.SERIALIZER.defaultValue()),
            MutationData.SERIALIZER.id(), new MutationData(5, Map.of(MUTATION_INPUT, MUTATION_OUTPUT)),
            TraitData.SERIALIZER.id(), Objects.requireNonNull(TraitData.SERIALIZER.defaultValue()),
            TraitData.SERIALIZER.id(), Objects.requireNonNull(TradeData.SERIALIZER.defaultValue())
    );
}
