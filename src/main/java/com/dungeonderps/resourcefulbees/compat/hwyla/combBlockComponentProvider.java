package com.dungeonderps.resourcefulbees.compat.hwyla;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.HoneycombBlockEntity;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class combBlockComponentProvider implements IComponentProvider {

    @Override
    public void appendHead(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        tooltip.clear();
        CompoundNBT data = accessor.getServerData();
        if(accessor.getTileEntity() instanceof HoneycombBlockEntity){
            TranslationTextComponent text = new TranslationTextComponent("block" + '.' + ResourcefulBees.MOD_ID + '.' + data.getString(BeeConst.NBT_BEE_TYPE).toLowerCase() + "_honeycomb_block");
            tooltip.add(text);
        }

    }

    @Override
    public ItemStack getStack(IDataAccessor accessor, IPluginConfig config) {
        CompoundNBT nbt = accessor.getServerData();
        ItemStack stack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());
        stack.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_BEE_TYPE, nbt.getString(BeeConst.NBT_BEE_TYPE));
        stack.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_COLOR, nbt.getString(BeeConst.NBT_COLOR));

        return stack;
    }
}
