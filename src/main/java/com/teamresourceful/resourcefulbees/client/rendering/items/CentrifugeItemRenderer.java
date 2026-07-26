package com.teamresourceful.resourcefulbees.client.rendering.items;

import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.teamresourceful.resourcefulbees.client.rendering.blocks.centrifuge.CentrifugeModel;
import com.teamresourceful.resourcefulbees.common.items.CentrifugeItem;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CentrifugeItemRenderer extends GeoItemRenderer<CentrifugeItem> {

    public CentrifugeItemRenderer() {
        super(new CentrifugeModel<>());
    }

    @Override
    public @Nullable RenderType getRenderType(@NonNull GeoRenderState renderState, @NonNull Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
