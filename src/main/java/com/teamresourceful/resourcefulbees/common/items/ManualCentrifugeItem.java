package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.client.rendering.items.CentrifugeItemRenderer;
import com.teamresourceful.resourcefulbees.client.rendering.items.ItemRendererProvider;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ManualCentrifugeItem extends CustomGeoBlockItem {

    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    public ManualCentrifugeItem(Block block, Properties pProperties) {
        super(block, pProperties);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
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
