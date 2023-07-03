package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.client.rendering.items.CentrifugeCrankItemRenderer;
import com.teamresourceful.resourcefulbees.client.rendering.items.ItemRendererProvider;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class CrankItem extends CustomGeoBlockItem {

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public CrankItem(Block block, Properties pProperties) {
        super(block, pProperties);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    public void getItemRenderer(Consumer<Object> consumer) {
        consumer.accept(new ItemRendererProvider() {

            private final CentrifugeCrankItemRenderer renderer = new CentrifugeCrankItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getRenderer() {
                return renderer;
            }
        });
    }
}
