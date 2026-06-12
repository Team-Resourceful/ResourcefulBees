package com.teamresourceful.resourcefulbees.client.rendering.blocks.centrifuge;

import com.geckolib.renderer.GeoBlockRenderer;
import com.teamresourceful.resourcefulbees.common.blockentities.CentrifugeCrankBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class CentrifugeCrankRenderer extends GeoBlockRenderer<CentrifugeCrankBlockEntity, BlockEntityRenderState> {

    public CentrifugeCrankRenderer(BlockEntityRendererProvider.Context ignored) {
        super(new CentrifugeCrankModel<>());
    }

/*    @Override
    public RenderType getRenderType(CentrifugeCrankBlockEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }*/
}
