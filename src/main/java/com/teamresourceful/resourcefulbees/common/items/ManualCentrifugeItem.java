package com.teamresourceful.resourcefulbees.common.items;

import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import com.teamresourceful.resourcefulbees.client.rendering.items.CentrifugeItemRenderer;
import com.teamresourceful.resourcefulbees.client.rendering.items.ItemRendererProvider;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class ManualCentrifugeItem extends CustomGeoBlockItem {

    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    public ManualCentrifugeItem(Block block, Properties pProperties) {
        super(block, pProperties);
    }

    @Override
    public void registerControllers(AnimatableManager.@NonNull ControllerRegistrar controllerRegistrar) {}

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void getItemRenderer(Consumer<Object> consumer) {
        consumer.accept(new ItemRendererProvider() {

            private final CentrifugeItemRenderer renderer = new CentrifugeItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getRenderer() {
                return renderer;
            }
        });
    }
}
