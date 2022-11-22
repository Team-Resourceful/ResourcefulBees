package com.teamresourceful.resourcefulbees.common.data.beedata;

import com.teamresourceful.resourcefulbees.api.data.bee.base.RegisterBeeDataEvent;
import com.teamresourceful.resourcefulbees.api.intializers.InitializerApi;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.CombatData;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.CoreData;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.DefaultBeeData;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.breeding.BeeFamilyUnit;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.breeding.BreedData;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.mutation.MutationData;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.mutation.types.BlockMutation;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.mutation.types.EntityMutation;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.mutation.types.FluidMutation;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.mutation.types.ItemMutation;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.rendering.ColorData;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.rendering.LayerData;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.rendering.LayerTexture;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.rendering.RenderData;
import com.teamresourceful.resourcefulbees.common.data.beedata.data.traits.TraitData;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;

public final class DataSetup {

    private DataSetup() {
        throw new UtilityClassError();
    }

    public static void setupInitalizers(InitializerApi api) {
        api.setData(DefaultBeeData::of);

        api.setColor(ColorData::new);
        api.setCombat(CombatData::new);
        api.setCore(CoreData::new);

        api.setBreeding(BreedData::new);
        api.setFamilyUnit(BeeFamilyUnit::of);

        api.setItemMutation(ItemMutation::new);
        api.setBlockMutation(BlockMutation::new);
        api.setEntityMutation(EntityMutation::new);
        api.setFluidMutation(FluidMutation::new);

        api.setRender(RenderData::new);
        api.setLayerTexture(LayerTexture::new);
        api.setLayer(LayerData::new);
    }

    public static void setupRegister(RegisterBeeDataEvent.DataRegisterer registerer) {
        registerer.register(BreedData.SERIALIZER);
        registerer.register(CombatData.SERIALIZER);
        registerer.register(RenderData.SERIALIZER);
        registerer.register(MutationData.SERIALIZER);
        registerer.register(CoreData.SERIALIZER);
        registerer.register(TraitData.SERIALIZER);
    }
}
