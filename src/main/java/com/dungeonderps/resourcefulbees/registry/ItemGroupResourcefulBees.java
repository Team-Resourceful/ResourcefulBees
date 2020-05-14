package com.dungeonderps.resourcefulbees.registry;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

public class ItemGroupResourcefulBees{

	public static final ItemGroup RESOURCEFUL_BEES = (new ItemGroup(ResourcefulBees.MOD_ID) {

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
						final CompoundNBT eggItemTag = eggStack.getOrCreateChildTag(BeeConst.NBT_ROOT);
						eggEntityTag.putString(BeeConst.NBT_BEE_TYPE, beeType.getKey());
						eggItemTag.putString(BeeConst.NBT_BEE_TYPE, beeType.getKey());
						items.add(eggStack);

						final ItemStack combStack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
						final CompoundNBT combItemTag = combStack.getOrCreateChildTag(BeeConst.NBT_ROOT);
						combItemTag.putString(BeeConst.NBT_BEE_TYPE, beeType.getKey());
						combItemTag.putString(BeeConst.NBT_COLOR, beeType.getValue().getColor());
						items.add(combStack);

						final ItemStack combBlockStack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());
						final CompoundNBT combBlockItemTag = combBlockStack.getOrCreateChildTag(BeeConst.NBT_ROOT);
						combBlockItemTag.putString(BeeConst.NBT_BEE_TYPE, beeType.getKey());
						combBlockItemTag.putString(BeeConst.NBT_COLOR, beeType.getValue().getColor());
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
