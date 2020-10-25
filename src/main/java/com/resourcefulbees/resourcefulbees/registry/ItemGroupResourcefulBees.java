package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class ItemGroupResourcefulBees{

	public static final ItemGroup RESOURCEFUL_BEES = (new ItemGroup(ResourcefulBees.MOD_ID) {

/*		@Override
		@OnlyIn(Dist.CLIENT)
		public void fill(@Nonnull NonNullList<ItemStack> items) {
			BeeRegistry.getRegistry().getBees().forEach(((s, customBeeData) -> {
					final ItemStack eggStack = new ItemStack(customBeeData.getSpawnEggItemRegistryObject().get());
					items.add(eggStack);
					if (customBeeData.hasHoneycomb()) {
						final ItemStack combStack = new ItemStack(customBeeData.getCombRegistryObject().get());
						items.add(combStack);

						final ItemStack combBlockStack = new ItemStack(customBeeData.getCombBlockItemRegistryObject().get());
						items.add(combBlockStack);
					}
			}));
			super.fill(items);
		}*/

		@Override
		@Nonnull
		@OnlyIn(Dist.CLIENT)
		public ItemStack createIcon() {
			return new ItemStack(ModItems.T1_BEEHIVE_ITEM.get());
		}
	});
	
}
