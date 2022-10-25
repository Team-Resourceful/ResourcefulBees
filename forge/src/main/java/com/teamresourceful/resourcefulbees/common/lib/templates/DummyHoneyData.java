package com.teamresourceful.resourcefulbees.common.lib.templates;

import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.api.honeydata.*;
import com.teamresourceful.resourcefulbees.api.honeydata.fluid.FluidAttributeData;
import com.teamresourceful.resourcefulbees.api.honeydata.fluid.FluidRenderData;
import com.teamresourceful.resourcefullib.common.color.ConstantColors;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

public class DummyHoneyData {

    public static final HoneyData DUMMY_HONEY_DATA = new HoneyData(
            "template",
            new HoneyBottleData(
                    "template",
                    ConstantColors.blue,
                    2,
                    4,
                    List.of(
                            new HoneyEffect(MobEffects.WITHER, 25, 2, 0.75f),
                            new HoneyEffect(MobEffects.INVISIBILITY, 50, 1, 1f)
                    ),
                    CodecUtils.itemHolder("honey_bottle")
            ),
            new HoneyBlockData(
                    true,
                    "template",
                    ConstantColors.blue,
                    2,
                    8,
                    CodecUtils.itemHolder("honey_block"),
                    CodecUtils.blockHolder("honey_block")
            ),
            new HoneyFluidData(
                    true,
                    "template",
                    FluidRenderData.DEFAULT,
                    FluidAttributeData.DEFAULT,
                    CodecUtils.fluidHolder("resourcefulbees:honey"),
                    CodecUtils.fluidHolder("resourcefulbees:honey_flowing"),
                    CodecUtils.itemHolder("resourcefulbees:honey_fluid_bucket"),
                    CodecUtils.blockHolder("resourcefulbees:honey_fluid_block")
            )
    );
}
