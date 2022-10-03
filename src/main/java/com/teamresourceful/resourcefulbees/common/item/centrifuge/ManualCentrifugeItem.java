package com.teamresourceful.resourcefulbees.common.item.centrifuge;

import com.teamresourceful.resourcefulbees.client.render.items.CentrifugeItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.function.Consumer;

public class ManualCentrifugeItem extends BlockItem implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);

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
    public void registerControllers(AnimationData data) {}

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
