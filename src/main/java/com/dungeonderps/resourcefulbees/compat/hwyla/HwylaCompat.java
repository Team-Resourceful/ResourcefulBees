package com.dungeonderps.resourcefulbees.compat.hwyla;

import com.dungeonderps.resourcefulbees.block.HoneycombBlock;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;

@WailaPlugin
public class HwylaCompat implements IWailaPlugin {

    @Override
    public void register(IRegistrar iRegistrar) {
        //iRegistrar.registerComponentProvider(new IronBeeHiveComponentProvider(), TooltipPosition.BODY, IronBeehiveBlock.class);
        iRegistrar.registerBlockDataProvider(new combBlockDataProvider(), HoneycombBlock.class);
        iRegistrar.registerComponentProvider(new combBlockComponentProvider(), TooltipPosition.HEAD, HoneycombBlock.class);
        iRegistrar.registerStackProvider(new combBlockComponentProvider(), HoneycombBlock.class);
    }
}
