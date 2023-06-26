package com.teamresourceful.resourcefulbees.common.lib.templates;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.honey.HoneyBlockData;
import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyBottleData;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import com.teamresourceful.resourcefulbees.api.intializers.HoneyInitializerApi;
import com.teamresourceful.resourcefulbees.api.intializers.InitializerApi;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.color.ConstantColors;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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

    private DummyHoneyData() {
        throw new UtilityClassError();
    }

    private static final HoneyInitializerApi HONEY_API = ResourcefulBeesAPI.getHoneyInitalizers();
    private static final InitializerApi API = ResourcefulBeesAPI.getInitializers();

    private static final BeekeeperTradeData DEFAULT_TRADE = API.beekeeperTrade(
            UniformInt.of(1,1),
            ItemStack.EMPTY,
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
                false,
                List.of(
                        HONEY_API.effect(LazyHolder.of(BuiltInRegistries.MOB_EFFECT, MobEffects.WITHER), 25, 2, 0.75f),
                        HONEY_API.effect(LazyHolder.of(BuiltInRegistries.MOB_EFFECT, MobEffects.INVISIBILITY), 50, 1, 1f)
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
                    new ResourceLocation(ModConstants.MOD_ID, "block/honey/custom_honey_still"),
                    new ResourceLocation(ModConstants.MOD_ID, "block/honey/custom_honey_flow"),
                    new ResourceLocation(ModConstants.MOD_ID, "block/honey/custom_honey_flow"),
                    new ResourceLocation(ModConstants.MOD_ID, "textures/block/honey/custom_honey_underwater.png")
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
                    LazyHolder.of(BuiltInRegistries.SOUND_EVENT, SoundEvents.BUCKET_FILL),
                    LazyHolder.of(BuiltInRegistries.SOUND_EVENT, SoundEvents.BUCKET_EMPTY)
            ),
            LazyHolder.of(BuiltInRegistries.FLUID, new ResourceLocation(ModConstants.MOD_ID, "honey")),
            LazyHolder.of(BuiltInRegistries.FLUID, new ResourceLocation(ModConstants.MOD_ID, "honey_flowing")),
            LazyHolder.of(BuiltInRegistries.ITEM, new ResourceLocation(ModConstants.MOD_ID, "honey_bucket")),
            LazyHolder.of(BuiltInRegistries.BLOCK, new ResourceLocation(ModConstants.MOD_ID, "honey")),
            DEFAULT_TRADE
    );

    public static final Map<ResourceLocation, com.teamresourceful.resourcefulbees.api.data.honey.base.HoneyData<?>> DATA = Map.of(
            BOTTLE_DATA.serializer().id(), BOTTLE_DATA,
            BLOCK_DATA.serializer().id(), BLOCK_DATA,
            FLUID_DATA.serializer().id(), FLUID_DATA
    );
}
