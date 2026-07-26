package com.teamresourceful.resourcefulbees.client.rendering.items;

import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.teamresourceful.resourcefulbees.client.rendering.blocks.centrifuge.CentrifugeCrankModel;
import com.teamresourceful.resourcefulbees.common.items.CrankItem;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class CentrifugeCrankItemRenderer extends GeoItemRenderer<CrankItem> {

    public CentrifugeCrankItemRenderer() {
        super(new CentrifugeCrankModel<>());
    }

    @Override
    public @Nullable RenderType getRenderType(@NonNull GeoRenderState renderState, @NonNull Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
