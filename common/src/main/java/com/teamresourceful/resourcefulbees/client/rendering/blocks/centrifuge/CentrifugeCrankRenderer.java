package com.teamresourceful.resourcefulbees.client.rendering.blocks.centrifuge;

import com.teamresourceful.resourcefulbees.common.blockentities.CentrifugeCrankBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CentrifugeCrankRenderer extends GeoBlockRenderer<CentrifugeCrankBlockEntity> {

    public CentrifugeCrankRenderer(BlockEntityRendererProvider.Context ignored) {
        super(new CentrifugeCrankModel<>());
    }

    @Override
    public RenderType getRenderType(CentrifugeCrankBlockEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
