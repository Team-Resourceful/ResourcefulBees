package com.teamresourceful.resourcefulbees.client.fluids;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefullib.client.fluid.data.ClientFluidProperties;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class HoneyClientFluidProperties {

    private static final Identifier STILL_TEXTURE = ModIdentifier.of("block/honey/honey_still");
    private static final Identifier FLOWING_TEXTURE = ModIdentifier.of("block/honey/honey_flow");
    private static final Identifier OVERLAY_TEXTURE = ModIdentifier.of("block/honey/honey_underwater");

    public static ClientFluidProperties create() {
        return new ClientFluidProperties() {
            @Override
            public Material still() {
                return new Material(STILL_TEXTURE);
            }

            @Override
            public Material flowing() {
                return new Material(FLOWING_TEXTURE);
            }

            @Override
            public Material overlay() {
                return null;
            }

            @Override
            public Identifier screenOverlay() {
                return OVERLAY_TEXTURE;
            }

            @Override
            public int tintColor(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, @Nullable FluidState state) {
                return -1;
            }
        };
    }
}
