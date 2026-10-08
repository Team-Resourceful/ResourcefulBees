package com.teamresourceful.resourcefulbees.common.items;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.util.GeckoLibUtil;
import com.google.common.base.Suppliers;
import com.teamresourceful.resourcefulbees.client.rendering.items.BeekeeperArmorRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BeekeeperArmorItem extends Item implements GeoItem {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private final GeoRenderProvider provider = new GeoRenderProvider() {
        private final Supplier<GeoArmorRenderer<BeekeeperArmorItem, HumanoidRenderState>> renderer = Suppliers.memoize(BeekeeperArmorRenderer::new);

        @Override
        public @Nullable GeoArmorRenderer<?, ?> getGeoArmorRenderer(@NonNull ItemStack itemStack, @NonNull EquipmentSlot equipmentSlot) {
            return this.renderer.get();
        }
    };

    public BeekeeperArmorItem(Properties properties) {
        super(properties);
    }

    @Override
    public void registerControllers(AnimatableManager.@NonNull ControllerRegistrar controllers) {
        //controllers.add(DefaultAnimations.genericWalkRunIdleController(), DefaultAnimations.genericAttackAnimation(DefaultAnimations.ATTACK_SWING).additiveAnimations());
    }

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(this.provider);
    }

    @Override
    public @NonNull GeoRenderProvider getRenderProvider() {
        return this.provider;
    }
}
