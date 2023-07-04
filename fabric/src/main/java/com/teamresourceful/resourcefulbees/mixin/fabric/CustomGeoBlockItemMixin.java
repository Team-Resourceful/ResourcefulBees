package com.teamresourceful.resourcefulbees.mixin.fabric;

import com.teamresourceful.resourcefulbees.client.rendering.items.ItemRendererProvider;
import com.teamresourceful.resourcefulbees.common.items.CustomGeoBlockItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(CustomGeoBlockItem.class)
public abstract class CustomGeoBlockItemMixin extends Item implements GeoItem {

    public CustomGeoBlockItemMixin(Properties arg) {
        super(arg);
    }

    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        CustomGeoBlockItem item = (CustomGeoBlockItem) (Object) this;
        consumer.accept(new RenderProvider() {

            private ItemRendererProvider provider;
            {
                item.getItemRenderer(value -> provider = (ItemRendererProvider) value);
            }

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return provider.getRenderer();
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
    }
}
