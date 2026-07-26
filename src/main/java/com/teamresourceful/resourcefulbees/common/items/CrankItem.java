package com.teamresourceful.resourcefulbees.common.items;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;
import com.google.common.base.Suppliers;
import com.teamresourceful.resourcefulbees.client.rendering.items.CentrifugeCrankItemRenderer;
import com.teamresourceful.resourcefulbees.common.blocks.CentrifugeBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CrankItem extends BlockItem implements GeoItem {

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public CrankItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(@NonNull BlockPlaceContext context, @NonNull BlockState stateForPlacement) {
        return context.getLevel().getBlockState(context.getClickedPos().below()).hasProperty(CentrifugeBlock.ROTATION) && super.canPlace(context, stateForPlacement);
    }

    @Override
    public void registerControllers(AnimatableManager.@NonNull ControllerRegistrar data) {}

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final Supplier<GeoItemRenderer<CrankItem>> renderer = Suppliers.memoize(() -> new GeoItemRenderer<>(CrankItem.this));

            @Override
            public @Nullable GeoItemRenderer<?> getGeoItemRenderer() {
                return this.renderer.get();
            }
        });
    }
}
