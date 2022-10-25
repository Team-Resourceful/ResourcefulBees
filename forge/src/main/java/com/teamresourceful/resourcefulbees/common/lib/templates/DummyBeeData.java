package com.teamresourceful.resourcefulbees.common.lib.templates;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CombatData;
import com.teamresourceful.resourcefulbees.api.beedata.CoreData;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BeeFamily;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BreedData;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.MutationData;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.BlockMutation;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.IMutation;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.ItemMutation;
import com.teamresourceful.resourcefulbees.api.beedata.render.BeeTexture;
import com.teamresourceful.resourcefulbees.api.beedata.render.ColorData;
import com.teamresourceful.resourcefulbees.api.beedata.render.LayerData;
import com.teamresourceful.resourcefulbees.api.beedata.render.RenderData;
import com.teamresourceful.resourcefulbees.api.beedata.traits.TraitData;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.LayerEffect;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DummyBeeData {

    private DummyBeeData() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static final IMutation MUTATION_INPUT = new BlockMutation(new RestrictedBlockPredicate(Blocks.COAL_BLOCK, NbtPredicate.ANY, LocationPredicate.ANY, StatePropertiesPredicate.ANY), 0.65, 35);

    private static final WeightedCollection<IMutation> MUTATION_OUTPUT = new WeightedCollection<IMutation>().add(25, new ItemMutation(new RestrictedItemPredicate(Items.DIAMOND, NbtPredicate.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.exactly(2)), 1.0, 25));

    private static ItemStack getBucketWithNbt() {
        ItemStack bucket = Items.BUCKET.getDefaultInstance();
        CompoundTag nbt = new CompoundTag();
        nbt.putString("this_is", "an_nbt_tag");
        bucket.setTag(nbt);
        return bucket;
    }

    public static final CustomBeeData DUMMY_CUSTOM_BEE_DATA = new CustomBeeData(
            new CoreData("template",
                    "dummy_honeycomb",
                    HolderSet.direct(Block::builtInRegistryHolder, Blocks.DIAMOND_BLOCK, Blocks.EMERALD_BLOCK),
                    HolderSet.direct(EntityType::builtInRegistryHolder, EntityType.COW, EntityType.SHEEP),
                    4000,
                    List.of(Component.literal("This is a bee template"),
                            Component.literal("This bee is a dummy and does not exist"),
                            Component.literal("Let's hope this works..."))
            ),
            new RenderData(
                    Set.of(new LayerData(Color.RAINBOW, BeeTexture.MISSING_TEXTURE, LayerEffect.NONE, false, 5),
                            new LayerData(ConstantColors.cyan, BeeTexture.MISSING_TEXTURE, LayerEffect.GLOW, false, 5)),
                    new ColorData(ConstantColors.antiquewhite, ConstantColors.chocolate, ConstantColors.cadetblue),
                    RenderData.BASE_MODEL,
                    BeeTexture.MISSING_TEXTURE,
                    RenderData.BASE_ANIMATION,
                    1.0f
            ),
            new BreedData(
                    Set.of(BeeFamily.of(75, 0.67, "diamond", "coal", "compressed_diamond"),
                            BeeFamily.of(24, 0.67, "diamond", "diamond", "compressed_diamond")),
                    HolderSet.direct(Item::builtInRegistryHolder, Items.POPPY, Items.CARROT),
                    Optional.of(getBucketWithNbt()),
                    2,
                    -45_000,
                    2_400
            ),
            CombatData.DEFAULT,
            new MutationData(5, Map.of(MUTATION_INPUT, MUTATION_OUTPUT)),
            TraitData.DEFAULT,
            new JsonObject(),
            new ResourceLocation(ResourcefulBees.MOD_ID, "template_bee"),
            Component.literal("template"),
            () -> EntityType.BEE
    );
}
