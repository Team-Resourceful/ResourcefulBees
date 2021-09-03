package com.teamresourceful.resourcefulbees.common.compat.top;

import mcjty.theoneprobe.api.IBlockDisplayOverride;
import mcjty.theoneprobe.api.IEntityDisplayOverride;
import mcjty.theoneprobe.api.ITheOneProbe;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TopCompat implements Function<ITheOneProbe, Void> {

    private static final IBlockDisplayOverride TIERED_BEEHIVE_DISPLAY_OVERRIDE = new TieredBeehiveDisplayOverride();
    private static final IEntityDisplayOverride BEE_DISPLAY_OVERRIDE = new BeeDisplayOverride();

    @Nullable
    @Override
    public Void apply(ITheOneProbe theOneProbe) {
        theOneProbe.registerBlockDisplayOverride(TIERED_BEEHIVE_DISPLAY_OVERRIDE);
        theOneProbe.registerEntityDisplayOverride(BEE_DISPLAY_OVERRIDE);
        return null;
    }
}