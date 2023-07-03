package com.teamresourceful.resourcefulbees.common.mixin;

import com.teamresourceful.resourcefulbees.client.rendering.items.ItemRendererProvider;
import com.teamresourceful.resourcefulbees.common.items.CustomGeoBlockItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

@Mixin(CustomGeoBlockItem.class)
public class CustomGeoBlockItemMixin extends Item {

    public CustomGeoBlockItemMixin(Properties arg) {
        super(arg);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        CustomGeoBlockItem item = (CustomGeoBlockItem) (Object) this;
        consumer.accept(new IClientItemExtensions() {

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
}
