package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.client.rendering.items.ItemRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import com.geckolib.animatable.GeoItem;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * THis class gets injected/replaced methods in a mixin on each platform.
 */
public abstract class CustomGeoBlockItem extends BlockItem implements GeoItem {

    public CustomGeoBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    /**
     * @see ItemRendererProvider
     */
    public abstract void getItemRenderer(Consumer<Object> consumer);

    public void createRenderer(Consumer<Object> consumer) {
        throw new RuntimeException("This should only be called on fabric!");
    }

    public @NonNull Supplier<Object> getRenderProvider() {
        throw new RuntimeException("This should only be called on fabric!");
    }
}
