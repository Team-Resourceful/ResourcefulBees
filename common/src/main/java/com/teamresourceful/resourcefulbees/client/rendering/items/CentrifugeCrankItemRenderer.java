package com.teamresourceful.resourcefulbees.client.rendering.items;

import com.teamresourceful.resourcefulbees.client.rendering.blocks.centrifuge.CentrifugeCrankModel;
import com.teamresourceful.resourcefulbees.common.items.CrankItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class CentrifugeCrankItemRenderer extends GeoItemRenderer<CrankItem> {

    public CentrifugeCrankItemRenderer() {
        super(new CentrifugeCrankModel<>());
    }

    @Override
    public RenderType getRenderType(CrankItem animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
