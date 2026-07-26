package com.teamresourceful.resourcefulbees.client.rendering.blocks.centrifuge;

import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.teamresourceful.resourcefulbees.common.blockentities.CentrifugeCrankBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CentrifugeCrankRenderer <R extends BlockEntityRenderState & GeoRenderState> extends GeoBlockRenderer<CentrifugeCrankBlockEntity, @NonNull R> {

    public CentrifugeCrankRenderer(BlockEntityRendererProvider.Context context, BlockEntityType<CentrifugeCrankBlockEntity> blockEntityType) {
        super(context, blockEntityType);
    }

    @Override
    public @Nullable RenderType getRenderType(@NonNull R renderState, @NonNull Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
