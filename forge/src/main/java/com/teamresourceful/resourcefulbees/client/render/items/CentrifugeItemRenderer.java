package com.teamresourceful.resourcefulbees.client.render.items;

import com.teamresourceful.resourcefulbees.client.render.blocks.centrifuge.CentrifugeModel;
import com.teamresourceful.resourcefulbees.common.item.centrifuge.ManualCentrifugeItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class CentrifugeItemRenderer extends GeoItemRenderer<ManualCentrifugeItem> {

    public CentrifugeItemRenderer() {
        super(new CentrifugeModel<>());
    }

    @Override
    public RenderType getRenderType(ManualCentrifugeItem animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
