package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.item.Beepedia;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class ItemGroupResourcefulBees {

    private ItemGroupResourcefulBees() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final CreativeModeTab RESOURCEFUL_BEES = new CreativeModeTab(ResourcefulBees.MOD_ID) {

        @Override
        @NotNull
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.OAK_BEE_NEST_ITEM.get());
        }

        @Override
        public void fillItemList(@NotNull NonNullList<ItemStack> stacks) {
            Item beepedia = ModItems.BEEPEDIA.get();
            for(Item item : Registry.ITEM) {
                item.fillItemCategory(this, stacks);
                if (item.equals(beepedia)) {
                    ItemStack creativeBeepedia = new ItemStack(ModItems.BEEPEDIA.get());
                    creativeBeepedia.getOrCreateTag().putBoolean(Beepedia.CREATIVE_TAG, true);
                    stacks.add(creativeBeepedia);
                }
            }
        }
    };

    public static final CreativeModeTab RESOURCEFUL_BEES_HIVES = new CreativeModeTab(ResourcefulBees.MOD_ID + ".hives") {

        @Override
        @NotNull
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.OAK_BEE_NEST_ITEM.get());
        }
    };

    public static final CreativeModeTab RESOURCEFUL_BEES_HONEY = new CreativeModeTab(ResourcefulBees.MOD_ID + ".honey") {

        @Override
        @NotNull
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Items.HONEY_BOTTLE);
        }
    };

    public static final CreativeModeTab RESOURCEFUL_BEES_COMBS = new CreativeModeTab(ResourcefulBees.MOD_ID + ".combs") {

        @Override
        @NotNull
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Items.HONEYCOMB);
        }
    };

    public static final CreativeModeTab RESOURCEFUL_BEES_BEES = new CreativeModeTab(ResourcefulBees.MOD_ID + ".bees") {

        @Override
        @NotNull
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Items.BEE_SPAWN_EGG);
        }
    };
}
