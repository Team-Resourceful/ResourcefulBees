package com.dungeonderps.resourcefulbees.compat.hwyla;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.block.HoneycombBlock;
import com.dungeonderps.resourcefulbees.block.beehive.Tier1BeehiveBlock;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.util.ResourceLocation;

@WailaPlugin
public class HwylaCompat implements IWailaPlugin {

    static final ResourceLocation BEEHIVE_SMOKER_PROGRESS = new ResourceLocation(ResourcefulBees.MOD_ID, "beehive_smoker_progress");

    @Override
    public void register(IRegistrar iRegistrar) {
        iRegistrar.registerComponentProvider(new TieredBeehiveCompat(), TooltipPosition.BODY, Tier1BeehiveBlock.class);
        iRegistrar.registerTooltipRenderer(BEEHIVE_SMOKER_PROGRESS, new ProgressBar());
        iRegistrar.registerBlockDataProvider(new TieredBeehiveCompat(), Tier1BeehiveBlock.class);

        iRegistrar.registerBlockDataProvider(new HoneycombBlockCompat(), HoneycombBlock.class);
        iRegistrar.registerComponentProvider(new HoneycombBlockCompat(), TooltipPosition.HEAD, HoneycombBlock.class);
        iRegistrar.registerStackProvider(new HoneycombBlockCompat(), HoneycombBlock.class);
    }
}
