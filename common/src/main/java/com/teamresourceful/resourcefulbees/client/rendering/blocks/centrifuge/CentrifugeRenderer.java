package com.teamresourceful.resourcefulbees.client.rendering.blocks.centrifuge;

import com.teamresourceful.resourcefulbees.common.blockentities.CentrifugeBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CentrifugeRenderer extends GeoBlockRenderer<CentrifugeBlockEntity> {

    public CentrifugeRenderer(BlockEntityRendererProvider.Context ignored) {
        super(new CentrifugeModel<>());
    }

    @Override
    public RenderType getRenderType(CentrifugeBlockEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
