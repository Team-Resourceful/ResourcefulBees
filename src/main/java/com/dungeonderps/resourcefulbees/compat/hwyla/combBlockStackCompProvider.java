package com.dungeonderps.resourcefulbees.compat.hwyla;

import com.dungeonderps.resourcefulbees.RegistryHandler;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class combBlockStackCompProvider implements IComponentProvider {
    @Override
    public ItemStack getStack(IDataAccessor accessor, IPluginConfig config) {
        CompoundNBT nbt = accessor.getServerData();
        ItemStack stack = new ItemStack(RegistryHandler.HONEYCOMBBLOCKITEM.get());
        stack.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_BEE_TYPE, nbt.getString(BeeConst.NBT_BEE_TYPE));
        stack.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_COLOR, nbt.getString(BeeConst.NBT_COLOR));

        return stack;
    }
}
