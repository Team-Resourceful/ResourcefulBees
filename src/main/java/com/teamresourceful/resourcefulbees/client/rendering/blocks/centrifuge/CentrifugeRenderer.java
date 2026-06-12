package com.teamresourceful.resourcefulbees.client.rendering.blocks.centrifuge;

import com.geckolib.renderer.GeoBlockRenderer;
import com.teamresourceful.resourcefulbees.common.blockentities.CentrifugeBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class CentrifugeRenderer extends GeoBlockRenderer<CentrifugeBlockEntity, BlockEntityRenderState> {

    public CentrifugeRenderer(BlockEntityRendererProvider.Context ignored) {
        super(new CentrifugeModel<>());
    }

/*    @Override
    public RenderType getRenderType(CentrifugeBlockEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }*/
}
