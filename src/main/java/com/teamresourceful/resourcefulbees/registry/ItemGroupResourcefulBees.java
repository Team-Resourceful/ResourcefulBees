package com.teamresourceful.resourcefulbees.registry;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.item.Beepedia;
import com.teamresourceful.resourcefulbees.lib.constants.ModConstants;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
        public void fillItemList(@NotNull NonNullList<ItemStack> itemstacks) {
            super.fillItemList(itemstacks);
            ItemStack creativeBeepedia = new ItemStack(ModItems.BEEPEDIA.get());
            CompoundNBT tag = new CompoundNBT();
            tag.putBoolean(Beepedia.CREATIVE_TAG, true);
            creativeBeepedia.setTag(tag);
            itemstacks.add(creativeBeepedia);
        }
    };
}
