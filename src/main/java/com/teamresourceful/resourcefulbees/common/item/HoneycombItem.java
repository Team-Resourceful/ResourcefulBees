package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ItemGroupResourcefulBees;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoneycombItem extends Item {

    private final RegistryObject<Item> storageBlock;
    private final Color color;
    private final boolean isEdible;
    private final boolean enchanted;

    public HoneycombItem(Color color, boolean isEdible, RegistryObject<Item> storageBlock, boolean enchanted) {
        super(new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES_COMBS));
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
    public Food getFoodProperties() {
        if (Boolean.TRUE.equals(CommonConfig.EDIBLE_HONEYCOMBS.get()) && !isEdible) {
            return super.getFoodProperties();
        }
        return new Food.Builder()
                .nutrition(CommonConfig.HONEYCOMB_HUNGER.get())
                .saturationMod(CommonConfig.HONEYCOMB_SATURATION.get().floatValue())
                .fast()
                .build();
    }
}
