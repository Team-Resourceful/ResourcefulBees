package com.teamresourceful.resourcefulbees.registry;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.item.Beepedia;
import com.teamresourceful.resourcefulbees.lib.constants.ModConstants;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class ItemGroupResourcefulBees {

    private ItemGroupResourcefulBees() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final CreativeModeTab RESOURCEFUL_BEES = (new CreativeModeTab(ResourcefulBees.MOD_ID) {

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
            CompoundTag tag = new CompoundTag();
            tag.putBoolean(Beepedia.CREATIVE_TAG, true);
            creativeBeepedia.setTag(tag);
            itemstacks.add(creativeBeepedia);
        }
    });


}
