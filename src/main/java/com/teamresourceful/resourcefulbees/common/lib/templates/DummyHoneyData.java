package com.teamresourceful.resourcefulbees.common.lib.templates;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.honey.HoneyBlockData;
import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyBottleData;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import com.teamresourceful.resourcefulbees.api.intializers.HoneyInitializerApi;
import com.teamresourceful.resourcefulbees.api.intializers.InitializerApi;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.color.ConstantColors;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Map;

public final class DummyHoneyData {

    private DummyHoneyData() throws UtilityClassException {
        throw new UtilityClassException();
    }

    private static final HoneyInitializerApi HONEY_API = ResourcefulBeesAPI.getHoneyInitializers();
    private static final InitializerApi API = ResourcefulBeesAPI.getInitializers();

    private static final BeekeeperTradeData DEFAULT_TRADE = API.beekeeperTrade(
            UniformInt.of(1,1),
            Items.AIR,
            UniformInt.of(1,1),
            0,
            1,
            1
    );

    private static final HoneyBottleData BOTTLE_DATA = HONEY_API.bottle(
            "template",
            ConstantColors.blue,
            HONEY_API.food(
                2,
                4,
                false,
                2.0f,
                List.of(
                        HONEY_API.effect(LazyHolder.of(BuiltInRegistries.MOB_EFFECT, MobEffects.WITHER.value()), 25, 2, 0.75f),
                        HONEY_API.effect(LazyHolder.of(BuiltInRegistries.MOB_EFFECT, MobEffects.INVISIBILITY.value()), 50, 1, 1f)
                )
            ),
            Rarity.EPIC,
            LazyHolder.of(BuiltInRegistries.ITEM, Items.HONEY_BOTTLE),
            DEFAULT_TRADE
    );

    private static final HoneyBlockData BLOCK_DATA = HONEY_API.block(
            ConstantColors.blue,
            2,
            8,
            LazyHolder.of(BuiltInRegistries.ITEM, Items.HONEY_BLOCK),
            LazyHolder.of(BuiltInRegistries.BLOCK, Blocks.HONEY_BLOCK),
            DEFAULT_TRADE
    );

    private static final HoneyFluidData FLUID_DATA = HONEY_API.fluid(
            "template",
            HONEY_API.fluidRender(
                    Color.DEFAULT,
                    ModIdentifier.of("block/honey/custom_honey_still"),
                    ModIdentifier.of( "block/honey/custom_honey_flow"),
                    ModIdentifier.of( "block/honey/custom_honey_flow"),
                    ModIdentifier.of( "textures/block/honey/custom_honey_underwater.png")
            ),
            HONEY_API.fluidAttributes(
                    1,
                    1000,
                    300,
                    1000,
                    0.5f,
                    0.014,
                    true,
                    true,
                    true,
                    false,
                    false,
                    false,
                    false,
                    Rarity.COMMON,
                    LazyHolder.of(BuiltInRegistries.SOUND_EVENT, SoundEvents.BUCKET_FILL),
                    LazyHolder.of(BuiltInRegistries.SOUND_EVENT, SoundEvents.BUCKET_EMPTY)
            ),
            LazyHolder.of(BuiltInRegistries.FLUID, ModIdentifier.of( "honey")),
            LazyHolder.of(BuiltInRegistries.FLUID, ModIdentifier.of("honey_flowing")),
            LazyHolder.of(BuiltInRegistries.ITEM, ModIdentifier.of("honey_bucket")),
            LazyHolder.of(BuiltInRegistries.BLOCK, ModIdentifier.of("honey")),
            DEFAULT_TRADE
    );

    public static final Map<Identifier, com.teamresourceful.resourcefulbees.api.data.honey.base.HoneyData<?>> DATA = Map.of(
            BOTTLE_DATA.serializer().id(), BOTTLE_DATA,
            BLOCK_DATA.serializer().id(), BLOCK_DATA,
            FLUID_DATA.serializer().id(), FLUID_DATA
    );
}
