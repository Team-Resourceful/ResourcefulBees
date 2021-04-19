package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Pollen extends Item {

    public CompoundTag getPollenData() {
        return pollenData;
    }

    public void setPollenData(CompoundTag pollenData) {
        this.pollenData = pollenData;
    }

    private CompoundTag pollenData = new CompoundTag();

    public Pollen(Properties properties) {
        super(properties);
    }

    public Pollen(Properties properties, CompoundTag compountNBT) {
        super(properties);
        setPollenData(compountNBT);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, @NotNull List list, @NotNull TooltipFlag par4)
    {
        if(stack.getTag() == null || !stack.getTag().contains("specific")) {
            list.add(new TranslatableComponent(ResourcefulBees.MOD_ID + ".information.unknown_type"));
        } else {
            list.add(BeeInfoUtils.getItem(stack.getTag().getString("specific")).getDescription());
        }
    }

}
