package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    public void addInformation(ItemStack stack, World world, @NotNull List list, @NotNull ITooltipFlag par4)
    {
        if(stack.getTag() == null || !stack.getTag().contains("specific")) {
            list.add(new TranslationTextComponent(ResourcefulBees.MOD_ID + ".information.unknown_type"));
        } else {
            list.add(BeeInfoUtils.getItem(stack.getTag().getString("specific")).getName());
        }
    }

}
