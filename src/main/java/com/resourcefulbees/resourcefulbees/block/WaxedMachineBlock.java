package com.resourcefulbees.resourcefulbees.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;

public class WaxedMachineBlock extends Block {

    public WaxedMachineBlock() {
        super(Properties.of(Material.METAL, MaterialColor.WOOD).harvestTool(ToolType.PICKAXE).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    }
}
