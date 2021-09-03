package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.item.Beepedia;
import com.teamresourceful.resourcefulbees.common.recipe.NestIngredient;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class ItemGroupResourcefulBees {

    private ItemGroupResourcefulBees() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final ItemGroup RESOURCEFUL_BEES = new ItemGroup(ResourcefulBees.MOD_ID) {

        @Override
        @NotNull
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.OAK_BEE_NEST_ITEM.get());
        }

        @Override
        public void fillItemList(@NotNull NonNullList<ItemStack> stacks) {
            super.fillItemList(stacks);

            ItemStack creativeBeepedia = new ItemStack(ModItems.BEEPEDIA.get());
            creativeBeepedia.getOrCreateTag().putBoolean(Beepedia.CREATIVE_TAG, true);
            stacks.add(creativeBeepedia);
        }
    };

    public static final ItemGroup RESOURCEFUL_BEES_HIVES = new ItemGroup(ResourcefulBees.MOD_ID + "_hives") {

        @Override
        @NotNull
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.OAK_BEE_NEST_ITEM.get());
        }

        @Override
        public void fillItemList(@NotNull NonNullList<ItemStack> stacks) {
            super.fillItemList(stacks);
            stacks.addAll(NestIngredient.getNests(1));
            stacks.addAll(NestIngredient.getNests(2));
            stacks.addAll(NestIngredient.getNests(3));
            stacks.addAll(NestIngredient.getNests(4));
        }
    };
}
