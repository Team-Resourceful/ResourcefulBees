package com.teamresourceful.resourcefulbees.client.fluids;

import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyRenderData;
import com.teamresourceful.resourcefullib.client.fluid.data.ClientFluidProperties;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class CustomHoneyClientFluidProperties {


    public static ClientFluidProperties create(HoneyRenderData renderData) {
        return new ClientFluidProperties() {
            @Override
            public Material still() {
                return new Material(renderData.still());
            }

            @Override
            public Material flowing() {
                return new Material(renderData.flowing());
            }

            @Override
            public Material overlay() {
                return new Material(renderData.overlay());
            }

            @Override
            public Identifier screenOverlay() {
                return renderData.overlay();
            }

            @Override
            public int tintColor(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, @Nullable FluidState state) {
                return renderData.color().withAlpha(255).getValue();
            }
        };
    }
}
