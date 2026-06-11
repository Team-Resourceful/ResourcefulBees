package com.teamresourceful.resourcefulbees.common.items;

import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import com.teamresourceful.resourcefulbees.client.rendering.items.CentrifugeCrankItemRenderer;
import com.teamresourceful.resourcefulbees.client.rendering.items.ItemRendererProvider;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class CrankItem extends CustomGeoBlockItem {

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public CrankItem(Block block, Properties properties) {
        super(block, properties);
    }



    @Override
    public void registerControllers(AnimatableManager.@NonNull ControllerRegistrar data) {}

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
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
