package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ItemGroupResourcefulBees;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoneycombItem extends net.minecraft.world.item.HoneycombItem {

    private final RegistryObject<Item> storageBlock;
    private final Color color;
    private final boolean isEdible;
    private final boolean enchanted;

    public HoneycombItem(Color color, boolean isEdible, RegistryObject<Item> storageBlock, boolean enchanted) {
        super(new Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES_COMBS));
        this.color = color;
        this.isEdible = isEdible;
        this.storageBlock = storageBlock;
        this.enchanted = enchanted;
    }

    @SuppressWarnings("unused")
    public static int getColor(ItemStack stack, int tintIndex) {
        return ((HoneycombItem) stack.getItem()).getHoneycombColor();
    }

    public int getHoneycombColor() { return color.getValue(); }

    public Item getStorageBlockItem() {
        return storageBlock.get();
    }

    public boolean hasStorageBlockItem() {
        return storageBlock != null;
    }

    @Override
    public boolean isEdible() {
        return isEdible;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return this.enchanted || stack.isEnchanted();
    }

    @Nullable
    @Override
    public FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        if (Boolean.TRUE.equals(CommonConfig.EDIBLE_HONEYCOMBS.get()) && !isEdible) {
            return super.getFoodProperties(stack, entity);
        }
        return new FoodProperties.Builder()
                .nutrition(CommonConfig.HONEYCOMB_HUNGER.get())
                .saturationMod(CommonConfig.HONEYCOMB_SATURATION.get().floatValue())
                .fast()
                .build();
    }
}
