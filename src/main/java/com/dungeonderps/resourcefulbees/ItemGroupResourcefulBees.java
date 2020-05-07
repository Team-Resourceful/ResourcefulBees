package com.dungeonderps.resourcefulbees;

import com.dungeonderps.resourcefulbees.config.BeeInfo;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

public class ItemGroupResourcefulBees{

	public static final ItemGroup RESOURCEFULBEES = (new ItemGroup("resourcefulbees") {

		@Override
		@OnlyIn(Dist.CLIENT)
		public void fill(NonNullList<ItemStack> items) {
			if (!BeeInfo.BEE_INFO.isEmpty()) {
				for (Map.Entry<String, BeeInfo> beeType : BeeInfo.BEE_INFO.entrySet()) {
					if (beeType.getKey() == "Default")
						continue;
					else {
						final ItemStack eggStack = new ItemStack(RegistryHandler.BEE_SPAWN_EGG.get());
						final CompoundNBT eggEntityTag = eggStack.getOrCreateChildTag("EntityTag");
						final CompoundNBT eggItemTag = eggStack.getOrCreateChildTag("ResourcefulBees");
						eggEntityTag.putString("BeeType", beeType.getKey());
						eggItemTag.putString("beeType", beeType.getKey());
						items.add(eggStack);

						final ItemStack combStack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
						final CompoundNBT combItemTag = combStack.getOrCreateChildTag("ResourcefulBees");
						combItemTag.putString("Color", beeType.getValue().getColor());
						combItemTag.putString("BeeType", beeType.getKey());
						items.add(combStack);

						final ItemStack combBlockStack = new ItemStack(RegistryHandler.HONEYCOMBBLOCKITEM.get());
						final CompoundNBT combBlockItemTag = combBlockStack.getOrCreateChildTag("ResourcefulBees");
						combBlockItemTag.putString("Color", beeType.getValue().getColor());
						combBlockItemTag.putString("BeeType", beeType.getKey());
						items.add(combBlockStack);
					}
				}
			}
			super.fill(items);
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack createIcon() {
			return new ItemStack(RegistryHandler.IRON_BEEHIVE_ITEM.get());
		}
	});
	
}
