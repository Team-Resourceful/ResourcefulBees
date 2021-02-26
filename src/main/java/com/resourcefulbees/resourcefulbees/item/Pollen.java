package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

import net.minecraft.item.Item.Properties;

public class Pollen extends Item {

    public CompoundNBT getPollenData() {
        return pollenData;
    }

    public void setPollenData(CompoundNBT pollenData) {
        this.pollenData = pollenData;
    }

    private CompoundNBT pollenData = new CompoundNBT();

    public Pollen(Properties properties) {
        super(properties);
    }

    public Pollen(Properties properties, CompoundNBT compountNBT) {
        super(properties);
        setPollenData(compountNBT);
    }

    @Override
    public void addInformation(ItemStack stack, World world, List list, ITooltipFlag par4)
    {
        if(stack.getTag() == null || !stack.getTag().contains("specific")) {
            list.add(new TranslationTextComponent(ResourcefulBees.MOD_ID + ".information.unknown_type"));
        } else {
            list.add(BeeInfoUtils.getItem(stack.getTag().getString("specific")).getName());
        }
    }

}
