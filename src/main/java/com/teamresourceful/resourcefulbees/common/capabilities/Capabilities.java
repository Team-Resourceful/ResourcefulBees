package com.teamresourceful.resourcefulbees.common.capabilities;

import com.teamresourceful.resourcefulbees.api.capabilities.IBeepediaData;
import net.minecraftforge.common.capabilities.Capability;

public class Capabilities {

    private Capabilities() {

    }

    //@CapabilityInject(IBeepediaData.class)
    public static Capability<IBeepediaData> BEEPEDIA_DATA;
}
