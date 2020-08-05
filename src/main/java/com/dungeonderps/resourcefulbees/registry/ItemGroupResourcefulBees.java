package com.dungeonderps.resourcefulbees.registry;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.utils.NBTHelper;
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

		@Override
		@OnlyIn(Dist.CLIENT)
		public void fill(@Nonnull NonNullList<ItemStack> items) {
			if (!BeeInfo.BEE_INFO.isEmpty()) {
				for (Map.Entry<String, BeeData> bee : BeeInfo.BEE_INFO.entrySet()) {
					if (!bee.getKey().equals(BeeConstants.DEFAULT_BEE_TYPE)) {
						final ItemStack eggStack = new ItemStack(RegistryHandler.BEE_SPAWN_EGG.get());
						final CompoundNBT eggEntityTag = eggStack.getOrCreateChildTag("EntityTag");
						final CompoundNBT eggItemTag = eggStack.getOrCreateChildTag(BeeConstants.NBT_ROOT);
						eggEntityTag.putString(BeeConstants.NBT_BEE_TYPE, bee.getValue().getName());
						eggItemTag.putString(BeeConstants.NBT_BEE_TYPE, bee.getValue().getName());
						items.add(eggStack);

						if (bee.getValue().getHoneycombColor() != null && !bee.getValue().getHoneycombColor().isEmpty()) {
							final ItemStack combStack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
							combStack.setTag(NBTHelper.createHoneycombItemTag(bee.getValue().getName(), bee.getValue().getHoneycombColor()));
							items.add(combStack);

							final ItemStack combBlockStack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());
							combBlockStack.setTag(NBTHelper.createHoneycombItemTag(bee.getValue().getName(), bee.getValue().getHoneycombColor()));
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
