package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.CustomBee;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.utils.NBTHelper;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Map;

public class ItemGroupResourcefulBees{

	public static final ItemGroup RESOURCEFUL_BEES = (new ItemGroup(ResourcefulBees.MOD_ID) {

		//TODO replace with registry objects

		@Override
		@OnlyIn(Dist.CLIENT)
		public void fill(@Nonnull NonNullList<ItemStack> items) {
			if (!BeeRegistry.getBees().isEmpty()) {
				for (Map.Entry<String, CustomBee> bee : BeeRegistry.getBees().entrySet()) {
					if (!bee.getKey().equals(BeeConstants.DEFAULT_BEE_TYPE)) {
						final ItemStack eggStack = new ItemStack(RegistryHandler.BEE_SPAWN_EGG.get());
						final CompoundNBT eggEntityTag = eggStack.getOrCreateChildTag("EntityTag");
						final CompoundNBT eggItemTag = eggStack.getOrCreateChildTag(NBTConstants.NBT_ROOT);
						eggEntityTag.putString(NBTConstants.NBT_BEE_TYPE, bee.getValue().getName());
						eggItemTag.putString(NBTConstants.NBT_BEE_TYPE, bee.getValue().getName());
						items.add(eggStack);

						if (bee.getValue().ColorData.getHoneycombColor() != null && !bee.getValue().ColorData.getHoneycombColor().isEmpty()) {
							final ItemStack combStack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
							combStack.setTag(NBTHelper.createHoneycombItemTag(bee.getValue().getName()));
							items.add(combStack);

							final ItemStack combBlockStack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());
							combBlockStack.setTag(NBTHelper.createHoneycombItemTag(bee.getValue().getName()));
							items.add(combBlockStack);
						}
					}
				}
			}
			super.fill(items);
		}

		@Override
		@Nonnull
		@OnlyIn(Dist.CLIENT)
		public ItemStack createIcon() {
			return new ItemStack(RegistryHandler.T1_BEEHIVE_ITEM.get());
		}
	});
	
}
