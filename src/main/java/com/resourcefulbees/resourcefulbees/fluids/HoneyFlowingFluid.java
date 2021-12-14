package com.resourcefulbees.resourcefulbees.fluids;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IEntity;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.lib.TagConstants;
import com.resourcefulbees.resourcefulbees.mixin.EntityAccessor;
import com.resourcefulbees.resourcefulbees.mixin.LivingEntityAccessor;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectUtils;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public abstract class HoneyFlowingFluid extends ForgeFlowingFluid {


    private final HoneyBottleData honeyData;

    protected HoneyFlowingFluid(Properties properties, HoneyBottleData honeyData) {
        super(properties);
        this.honeyData = honeyData;
    }

    public HoneyBottleData getHoneyData() {
        return honeyData;
    }

    public static class Flowing extends HoneyFlowingFluid {

        public Flowing(Properties properties, HoneyBottleData honeyData) {
            super(properties, honeyData);
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        @Override
        protected void createFluidStateDefinition(@NotNull StateContainer.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(@NotNull FluidState state) {
            return false;
        }
    }

    public static class Source extends HoneyFlowingFluid {

        public Source(Properties properties, HoneyBottleData honeyData) {
            super(properties, honeyData);
        }

        public int getAmount(@NotNull FluidState state) {
            return 8;
        }

        public boolean isSource(@NotNull FluidState state) {
            return true;
        }
    }
}
