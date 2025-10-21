package com.teamresourceful.resourcefulbees.common.fluids;

import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidAttributesData;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyRenderData;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import earth.terrarium.botarium.common.registry.fluid.FluidProperties;
import net.minecraft.resources.ResourceLocation;

public class HoneyFluidInformation extends WrappedFluidInformation {

    private final ResourceLocation id;
    private final HoneyRenderData renderData;

    public HoneyFluidInformation(String id, HoneyRenderData renderData, HoneyFluidAttributesData attributes) {
        super(FluidProperties.create()
                .lightLevel(attributes.lightLevel())
                .density(attributes.density())
                .temperature(attributes.temperature())
                .viscosity(attributes.viscosity())
                .fallDistanceModifier(attributes.fallDistanceModifier())
                .motionScale(attributes.motionScale())
                .canPushEntity(attributes.canPushEntities())
                .canSwim(attributes.canSwimIn())
                .canDrown(attributes.canDrownIn())
                .canExtinguish(attributes.canExtinguish())
                .canConvertToSource(attributes.canConvertToSource())
                .supportsBloating(attributes.supportsBoating())
                .canHydrate(attributes.canHydrate())
                .sounds("bucket_fill", attributes.bucketFill().get())
                .sounds("bucket_empty", attributes.bucketEmpty().get())
        );
        this.id = new ResourceLocation(ModConstants.MOD_ID, id);
        this.renderData = renderData;
    }

    @Override
    public ResourceLocation id() {
        return this.id;
    }

    @Override
    public int tintColor() {
        return renderData.color().getValue() | 0xff000000;
    }

    @Override
    public ResourceLocation still() {
        return renderData.still();
    }

    @Override
    public ResourceLocation flowing() {
        return renderData.flowing();
    }

    @Override
    public ResourceLocation overlay() {
        return renderData.face();
    }

    @Override
    public ResourceLocation screenOverlay() {
        return renderData.overlay();
    }
}
