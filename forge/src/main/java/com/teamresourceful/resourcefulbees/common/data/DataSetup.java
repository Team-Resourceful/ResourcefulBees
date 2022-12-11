package com.teamresourceful.resourcefulbees.common.data;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.bee.base.RegisterBeeDataEvent;
import com.teamresourceful.resourcefulbees.api.data.conditions.RegisterConditionEvent;
import com.teamresourceful.resourcefulbees.api.data.honey.base.RegisterHoneyDataEvent;
import com.teamresourceful.resourcefulbees.api.intializers.HoneyInitializerApi;
import com.teamresourceful.resourcefulbees.api.intializers.InitializerApi;
import com.teamresourceful.resourcefulbees.common.data.beedata.CombatData;
import com.teamresourceful.resourcefulbees.common.data.beedata.CoreData;
import com.teamresourceful.resourcefulbees.common.data.beedata.DefaultBeeData;
import com.teamresourceful.resourcefulbees.common.data.beedata.TradeData;
import com.teamresourceful.resourcefulbees.common.data.beedata.breeding.BeeFamilyUnit;
import com.teamresourceful.resourcefulbees.common.data.beedata.breeding.BreedData;
import com.teamresourceful.resourcefulbees.common.data.beedata.mutation.MutationData;
import com.teamresourceful.resourcefulbees.common.data.beedata.mutation.types.BlockMutation;
import com.teamresourceful.resourcefulbees.common.data.beedata.mutation.types.EntityMutation;
import com.teamresourceful.resourcefulbees.common.data.beedata.mutation.types.FluidMutation;
import com.teamresourceful.resourcefulbees.common.data.beedata.mutation.types.ItemMutation;
import com.teamresourceful.resourcefulbees.common.data.beedata.rendering.ColorData;
import com.teamresourceful.resourcefulbees.common.data.beedata.rendering.LayerData;
import com.teamresourceful.resourcefulbees.common.data.beedata.rendering.LayerTexture;
import com.teamresourceful.resourcefulbees.common.data.beedata.rendering.RenderData;
import com.teamresourceful.resourcefulbees.common.data.beedata.traits.TraitData;
import com.teamresourceful.resourcefulbees.common.lib.data.conditions.AndCondition;
import com.teamresourceful.resourcefulbees.common.lib.data.conditions.ModLoadedCondition;
import com.teamresourceful.resourcefulbees.common.lib.data.conditions.OrCondition;
import com.teamresourceful.resourcefulbees.common.data.honeydata.CustomHoneyBlockData;
import com.teamresourceful.resourcefulbees.common.data.honeydata.HoneyDataImpl;
import com.teamresourceful.resourcefulbees.common.data.honeydata.bottle.CustomHoneyBottleData;
import com.teamresourceful.resourcefulbees.common.data.honeydata.bottle.CustomHoneyBottleEffectData;
import com.teamresourceful.resourcefulbees.common.data.honeydata.bottle.CustomHoneyFoodData;
import com.teamresourceful.resourcefulbees.common.data.honeydata.fluid.CustomHoneyFluidAttributesData;
import com.teamresourceful.resourcefulbees.common.data.honeydata.fluid.CustomHoneyFluidData;
import com.teamresourceful.resourcefulbees.common.data.honeydata.fluid.CustomHoneyRenderData;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;

public final class DataSetup {

    private DataSetup() {
        throw new UtilityClassError();
    }

    public static void setupInitializers(InitializerApi api) {
        api.setData(DefaultBeeData::of);

        api.setColor(ColorData::new);
        api.setCombat(CombatData::new);
        api.setCore(CoreData::new);
        api.setBeekeeperTrade(TradeData::new);

        api.setBreeding(BreedData::new);
        api.setFamilyUnit(BeeFamilyUnit::of);

        api.setMutation(MutationData::new);
        api.setItemMutation(ItemMutation::new);
        api.setBlockMutation(BlockMutation::new);
        api.setEntityMutation(EntityMutation::new);
        api.setFluidMutation(FluidMutation::new);

        api.setRender(RenderData::new);
        api.setLayerTexture(LayerTexture::new);
        api.setLayer(LayerData::new);

        api.setTraits(TraitData::of);

        ResourcefulBeesAPI.getEvents().registerBeeData(DataSetup::setupRegister);
    }

    public static void setupInitializers(HoneyInitializerApi api) {
        api.setData(HoneyDataImpl::new);

        api.setBlock(CustomHoneyBlockData::new);

        api.setBottle(CustomHoneyBottleData::new);
        api.setFood(CustomHoneyFoodData::new);
        api.setEffect(CustomHoneyBottleEffectData::new);

        api.setFluid(CustomHoneyFluidData::new);
        api.setFluidRender(CustomHoneyRenderData::new);
        api.setFluidAttributes(CustomHoneyFluidAttributesData::new);

        ResourcefulBeesAPI.getEvents().registerHoneyData(DataSetup::setupRegister);
    }

    public static void setupRegister(RegisterBeeDataEvent registrar) {
        registrar.register(BreedData.SERIALIZER);
        registrar.register(CombatData.SERIALIZER);
        registrar.register(RenderData.SERIALIZER);
        registrar.register(MutationData.SERIALIZER);
        registrar.register(CoreData.SERIALIZER);
        registrar.register(TraitData.SERIALIZER);
        registrar.register(TradeData.SERIALIZER);
    }

    public static void setupRegister(RegisterHoneyDataEvent registrar) {
        registrar.register(CustomHoneyBlockData.SERIALIZER);
        registrar.register(CustomHoneyBottleData.SERIALIZER);
        registrar.register(CustomHoneyFluidData.SERIALIZER);
    }

    public static void setupRegister(RegisterConditionEvent registrar) {
        registrar.register(ModLoadedCondition.SERIALIZER);
        registrar.register(OrCondition.SERIALIZER);
        registrar.register(AndCondition.SERIALIZER);
    }
}
