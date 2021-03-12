package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class ItemGroupResourcefulBees {

	private ItemGroupResourcefulBees() {
		throw new IllegalStateException(ModConstants.UTILITY_CLASS);
	}

	public static final ItemGroup RESOURCEFUL_BEES = (new ItemGroup(ResourcefulBees.MOD_ID) {

		@Override
		@Nonnull
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(ModItems.T1_BEEHIVE_ITEM.get());
		}
	});
	
}
