package com.dungeonderps.resourcefulbees.compat.hwyla;

import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.tileentity.IronBeehiveBlockEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class IronBeeHiveComponentProvider implements IComponentProvider {

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if(!(accessor.getTileEntity() instanceof IronBeehiveBlockEntity)) {
            return;
        }

        IronBeehiveBlockEntity ironBeehive = (IronBeehiveBlockEntity) accessor.getTileEntity();
        if(ironBeehive.isSmoked()) {
            tooltip.add(new StringTextComponent("Smoked: " + ironBeehive.isSmoked() ));
            //TODO figure out why it gets stuck to always true (probs a server to client desync)
        }

        return;
    }
}