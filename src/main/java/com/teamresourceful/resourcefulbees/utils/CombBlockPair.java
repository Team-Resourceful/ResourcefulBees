package com.teamresourceful.resourcefulbees.utils;

import com.teamresourceful.resourcefulbees.api.beedata.HoneycombData;
import com.teamresourceful.resourcefulbees.block.HoneycombBlock;
import com.teamresourceful.resourcefulbees.item.HoneycombItem;
import net.minecraft.util.ResourceLocation;

public class CombBlockPair {
    public final HoneycombItem comb;
    public final HoneycombBlock block;
    public final HoneycombData data;
    public final ResourceLocation resourceLocation;

    public CombBlockPair(HoneycombItem comb, HoneycombBlock block) {
        this.comb = comb;
        this.block = block;
        this.data = comb.getHoneycombData();
        this.resourceLocation = comb.getRegistryName();
    }
}
