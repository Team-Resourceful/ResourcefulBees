package com.dungeonderps.resourcefulbees;

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemGroupResourcefulBees extends ItemGroup {
	
	public static final HashMap<String, String> bees = new HashMap<>();
	
    public ItemGroupResourcefulBees() {
        
        super("resourcefulbees");
        this.setBackgroundImageName("item_search.png");
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void fill (NonNullList<ItemStack> items) {
    	if (!bees.isEmpty()) {
	        for (Entry<String, String> beeType : bees.entrySet()) {
		        final ItemStack eggStack = new ItemStack(RegistryHandler.BEE_SPAWN_EGG.get());
		        final CompoundNBT eggEntityTag = eggStack.getOrCreateChildTag("EntityTag");
		        final CompoundNBT eggItemTag = eggStack.getOrCreateChildTag("ResourcefulBees");
		        eggEntityTag.putString("BeeType", beeType.getKey());
		        eggItemTag.putString("beeType", beeType.getKey());
		        items.add(eggStack);
		        
		        final ItemStack combStack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
		        final CompoundNBT combItemTag = combStack.getOrCreateChildTag("ResourcefulBees");
		        combItemTag.putString("Color", beeType.getValue());
		        combItemTag.putString("BeeType", beeType.getKey());
		        items.add(combStack);
	        } 
    	}
    	items.add(new ItemStack(RegistryHandler.BEE_SPAWN_EGG.get()));
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack createIcon () {
        
        return new ItemStack(RegistryHandler.IRON_BEEHIVE_ITEM.get());
    }
	
}
