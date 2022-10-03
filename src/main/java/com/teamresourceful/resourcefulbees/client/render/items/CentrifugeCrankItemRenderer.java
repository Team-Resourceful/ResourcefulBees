package com.teamresourceful.resourcefulbees.client.render.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamresourceful.resourcefulbees.client.render.blocks.centrifuge.CentrifugeCrankModel;
import com.teamresourceful.resourcefulbees.common.item.centrifuge.CrankItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class CentrifugeCrankItemRenderer extends GeoItemRenderer<CrankItem> {

    public CentrifugeCrankItemRenderer() {
        super(new CentrifugeCrankModel<>());
    }

    @Override
    public RenderType getRenderType(CrankItem animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }
}
