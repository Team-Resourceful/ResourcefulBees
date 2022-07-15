package com.teamresourceful.resourcefulbees.common.fluids;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

public class NormalHoneyFluidType extends FluidType {

    private NormalHoneyFluidType() {
        super(Properties.create()
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .density(1300)
                .temperature(300)
                .viscosity(1800)
                .rarity(Rarity.COMMON)
        );
    }

    public static NormalHoneyFluidType of() {
        return new NormalHoneyFluidType();
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(NormalHoneyRenderProperties.INSTANCE);
    }
}
