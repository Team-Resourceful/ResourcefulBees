package com.teamresourceful.resourcefulbees.capabilities;

import com.teamresourceful.resourcefulbees.api.capabilities.IBeepediaData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class Capabilities {

    private Capabilities() {

    }

    @CapabilityInject(IBeepediaData.class)
    public static Capability<IBeepediaData> BEEPEDIA_DATA;
}
