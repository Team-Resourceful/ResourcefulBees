package com.dungeonderps.resourcefulbees;

import java.util.Map;

import com.dungeonderps.resourcefulbees.config.BeeInfo;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemGroupResourcefulBees{

	public static final ItemGroup RESOURCEFULBEES = (new ItemGroup("resourcefulbees") {

		@Override
		@OnlyIn(Dist.CLIENT)
		public void fill(NonNullList<ItemStack> items) {
			if (!CustomBeeEntity.BEE_INFO.isEmpty()) {
				for (Map.Entry<String, BeeInfo> beeType : CustomBeeEntity.BEE_INFO.entrySet()) {
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
