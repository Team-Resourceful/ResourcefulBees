package com.resourcefulbees.resourcefulbees.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;

public class WaxedPlanks extends Block {

    public WaxedPlanks() {
        super(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).harvestTool(ToolType.AXE).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    }
}
