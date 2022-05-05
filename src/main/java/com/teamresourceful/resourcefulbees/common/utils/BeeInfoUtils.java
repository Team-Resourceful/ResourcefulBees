package com.teamresourceful.resourcefulbees.common.utils;

import com.teamresourceful.resourcefulbees.api.ICustomBee;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyFluidData;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.item.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeehiveEntityAccessor;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BeeInfoUtils {

    private BeeInfoUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static Pair<String, String> sortParents(String parent1, String parent2) {
        return parent1.compareTo(parent2) > 0 ? Pair.of(parent1, parent2) : Pair.of(parent2, parent1);
    }

    public static MobEffect getEffect(String effectName) {
        return ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.tryParse(effectName));
    }

    public static Optional<EntityType<?>> getOptionalEntityType(String entityName) {
         return getResourceLocation(entityName).filter(ForgeRegistries.ENTITIES::containsKey).map(ForgeRegistries.ENTITIES::getValue);
    }

    public static @Nullable EntityType<?> getEntityType(String entityName) {
        return ForgeRegistries.ENTITIES.getValue(ResourceLocation.tryParse(entityName));
    }

    public static @Nullable EntityType<?> getEntityType(ResourceLocation entityId) {
        return ForgeRegistries.ENTITIES.getValue(entityId);
    }

    public static Optional<ResourceLocation> getResourceLocation(String resource) {
        return Optional.ofNullable(ResourceLocation.tryParse(resource));
    }

    public static void flagBeesInRange(BlockPos pos, Level world) {
        if (world != null) {
            List<CustomBeeEntity> list = world.getEntitiesOfClass(CustomBeeEntity.class, new AABB(pos).inflate(10));
            list.forEach(customBeeEntity -> customBeeEntity.setHasHiveInRange(true));
        }
    }

    public static void ageBee(int ticksInHive, Animal animal) {
        int i = animal.getAge();
        if (i < 0) {
            animal.setAge(Math.min(0, i + ticksInHive));
        } else if (i > 0) {
            animal.setAge(Math.max(0, i - ticksInHive));
        }

        if (animal instanceof CustomBeeEntity bee) {
            bee.setLoveTime(Math.max(0, animal.getInLoveTime() - ticksInHive));
        } else {
            animal.setInLoveTime(Math.max(0, animal.getInLoveTime() - ticksInHive));
        }
        if (animal instanceof Bee bee) bee.resetTicksWithoutNectarSinceExitingHive();
    }

    public static void setEntityLocationAndAngle(BlockPos blockpos, Direction direction, Entity entity) {
        EntityDimensions size = entity.getDimensions(Pose.STANDING);
        double d0 = 0.65D + size.width / 2.0F;
        double d1 = blockpos.getX() + 0.5D + d0 * direction.getStepX();
        double d2 = blockpos.getY() + Math.max(0.5D - (size.height / 2.0F), 0);
        double d3 = blockpos.getZ() + 0.5D + d0 * direction.getStepZ();
        entity.moveTo(d1, d2, d3, entity.getYRot(), entity.getXRot());
    }

    public static @NotNull CompoundTag createJarBeeTag(Bee bee) {
        CompoundTag nbt = new CompoundTag();
        bee.saveAsPassenger(nbt);

        String beeColor = bee instanceof ICustomBee iBee ? iBee.getRenderData().colorData().jarColor().toString() : BeeConstants.VANILLA_BEE_COLOR;

        nbt.putString(NBTConstants.NBT_COLOR, beeColor);
        BeehiveEntityAccessor.callRemoveIgnoredBeeTags(nbt);
        return nbt;
    }

    public static Fluid getHoneyFluidFromBottle(ItemStack bottleOutput) {
        Item item = bottleOutput.getItem();
        if (item == Items.HONEY_BOTTLE) {
            return ModFluids.HONEY_STILL.get().getSource();
        } else if (item instanceof CustomHoneyBottleItem honey) {
            HoneyFluidData fluidData = HoneyRegistry.getRegistry().getHoneyData(honey.getHoneyData().name()).fluidData();
            return fluidData.stillFluid();
        }
        return Fluids.EMPTY;
    }

    public static Item getHoneyBottleFromFluid(Fluid fluid) {
        if (fluid instanceof CustomHoneyFluid honeyFluid) {
            return HoneyRegistry.getRegistry().getHoneyData(honeyFluid.getHoneyData().name()).bottleData().honeyBottle();
        }
        return Items.HONEY_BOTTLE;
    }

}
