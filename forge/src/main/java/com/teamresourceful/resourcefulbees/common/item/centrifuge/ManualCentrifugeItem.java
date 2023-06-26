package com.teamresourceful.resourcefulbees.common.item.centrifuge;

import com.teamresourceful.resourcefulbees.client.render.items.CentrifugeItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ManualCentrifugeItem extends BlockItem implements GeoAnimatable {

    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    public ManualCentrifugeItem(Block block, Properties pProperties) {
        super(block, pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {

            private final CentrifugeItemRenderer renderer = new CentrifugeItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public double getTick(Object o) {
        return 0;
    }
}
