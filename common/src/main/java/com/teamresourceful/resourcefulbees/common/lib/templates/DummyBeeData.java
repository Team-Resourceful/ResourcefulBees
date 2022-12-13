package com.teamresourceful.resourcefulbees.common.lib.templates;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeCombatData;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeCoreData;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeTraitData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.BeeBreedData;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.BeeMutationData;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerTexture;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeRenderData;
import com.teamresourceful.resourcefulbees.api.intializers.InitializerApi;
import com.teamresourceful.resourcefulbees.common.config.BeeConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.LayerEffect;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.color.ConstantColors;
import net.minecraft.Util;
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

    private static final InitializerApi API = ResourcefulBeesAPI.getInitializers();

    private static ItemStack getBucketWithNbt() {
        ItemStack bucket = Items.BUCKET.getDefaultInstance();
        CompoundTag nbt = new CompoundTag();
        nbt.putString("this_is", "an_nbt_tag");
        bucket.setTag(nbt);
        return bucket;
    }

    private static final BeeCoreData CORE_DATA = API.core(
            "dummy_honeycomb",
            HolderSet.direct(Block::builtInRegistryHolder, Blocks.DIAMOND_BLOCK, Blocks.EMERALD_BLOCK),
            HolderSet.direct(EntityType::builtInRegistryHolder, EntityType.COW, EntityType.SHEEP),
            4000,
            List.of(Component.literal("This is a bee template"),
                    Component.literal("This bee is a dummy and does not exist"),
                    Component.literal("Let's hope this works...")
            )
    );

    private static final BeeLayerTexture MISSING_LAYER = API.layerTexture(new ResourceLocation(BeeConstants.MOD_ID, "textures/entity/missing_texture.png"), new ResourceLocation(BeeConstants.MOD_ID, "textures/entity/missing_texture.png"));

    private static final BeeRenderData RENDER_DATA = API.render(
            Set.of(
                API.layer(Color.RAINBOW, MISSING_LAYER, LayerEffect.NONE, false, 5),
                API.layer(ConstantColors.cyan, MISSING_LAYER, LayerEffect.GLOW, false, 5)
            ),
            API.color(ConstantColors.antiquewhite, ConstantColors.chocolate, ConstantColors.cadetblue),
            new ModResourceLocation("geo/base.geo.json"),
            MISSING_LAYER,
            new ModResourceLocation("animations/bee.animation.json"),
            1.0f
    );

    private static final BeeBreedData BREED_DATA = API.breeding(
            Set.of(
                API.familyUnit(75, 0.67, "diamond", "coal", "compressed_diamond"),
                API.familyUnit(24, 0.67, "diamond", "diamond", "compressed_diamond")
            ),
            HolderSet.direct(Item::builtInRegistryHolder, Items.POPPY, Items.CARROT),
            Optional.of(getBucketWithNbt()),
            2,
            -45_000,
            2_400
    );

    private static final BeeCombatData COMBAT_DATA = API.combat(
            false,
            true,
            true,
            false,
            Util.make(new HashMap<>(), map -> {
                map.put(Attributes.MAX_HEALTH, 10d);
                map.put(Attributes.FLYING_SPEED, 0.6D);
                map.put(Attributes.MOVEMENT_SPEED, 0.3D);
                map.put(Attributes.ATTACK_DAMAGE, 1d);
                map.put(Attributes.FOLLOW_RANGE, 48D);
                map.put(Attributes.ARMOR, 0d);
                map.put(Attributes.ARMOR_TOUGHNESS, 0d);
                map.put(Attributes.ATTACK_KNOCKBACK, 0d);
            })
    );

    private static final BeeMutationData MUTATION_DATA = API.mutation(
            5,
            new ResourceLocation(BeeConstants.MOD_ID, "mutations/template")
    );

    private static final BeeTraitData TRAIT_DATA = API.trait(BeeConfig.defaultAuraRange, Set.of());

    public static final Map<ResourceLocation, BeeData<?>> DATA = Map.of(
            CORE_DATA.serializer().id(), CORE_DATA,
            RENDER_DATA.serializer().id(), RENDER_DATA,
            BREED_DATA.serializer().id(), BREED_DATA,
            COMBAT_DATA.serializer().id(), COMBAT_DATA,
            MUTATION_DATA.serializer().id(), MUTATION_DATA,
            TRAIT_DATA.serializer().id(), TRAIT_DATA
    );
}
