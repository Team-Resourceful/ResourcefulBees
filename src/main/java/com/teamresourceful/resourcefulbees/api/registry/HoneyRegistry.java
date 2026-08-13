package com.teamresourceful.resourcefulbees.api.registry;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.items.honey.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModFluidTags;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModFluids;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public interface HoneyRegistry {

    static HoneyRegistry get() {
        return ResourcefulBeesAPI.getRegistry().getHoneyRegistry();
    }

    /**
     * Returns a HoneyBottleData object for the given honey type.
     *
     * @param honey Honey type for which HoneyData is requested.
     * @return Returns a HoneyBottleData object for the given bee type.
     */
    CustomHoneyData getHoneyData(String honey);

    /**
     * Returns whether the given honey type is registered.
     *
     * @param name Honey type to check.
     * @return Returns true if the given honey type is registered.
     */
    boolean containsHoney(String name);

    /**
     * Returns an unmodifiable copy of the Honey Registry.
     * This is useful for iterating over all honey without worry of changing data
     *
     *  @return Returns unmodifiable copy of honey registry.
     */
    Map<String, CustomHoneyData> getHoneyBottles();

    /**
     * Returns a set containing all registered HoneyBottleData.
     * This is useful for iterating over all honey without worry of changing data
     *
     * @return Returns a set containing all registered HoneyBottleData.
     */
    Set<CustomHoneyData> getSetOfHoney();

    /**
     * A helper method that returns a stream using the {@link HoneyRegistry#getSetOfHoney()} method.
     */
    Stream<CustomHoneyData> getStreamOfHoney();

    /**
     * @return Returns a set containing all registered honeys ids.
     */
    Set<String> getHoneyTypes();

    /**
     * Gets the fluid resource associated with a honey bottle.
     *
     * @param item Bottle item.
     * @return Associated fluid resource, or {@link FluidResource#EMPTY} if none exists.
     */
    default FluidResource getResourceFromBottle(Item item) {
        if (item == Items.HONEY_BOTTLE) {
            return FluidResource.of(ModFluids.HONEY_STILL.get());
        }

        if (!(item instanceof CustomHoneyBottleItem bottle)) {
            return FluidResource.EMPTY;
        }

        String id = bottle.getHoneyData().id();

        if (id.isEmpty()) {
            return FluidResource.EMPTY;
        }

        CustomHoneyData honey = getHoneyData(id);

        if (honey == null) {
            return FluidResource.EMPTY;
        }

        return FluidResource.of(
                honey.getFluidData()
                        .stillFluid()
                        .get()
        );
    }

    /**
     * Gets the bottle associated with a honey fluid.
     *
     * @param fluidResource Honey fluid.
     * @return Associated honey bottle, or {@link Items#AIR} if none exists.
     */
    default Item getBottleFromFluid(FluidResource fluidResource) {
        Fluid fluid = fluidResource.getFluid();
        if (fluid instanceof CustomHoneyFluid.Still honeyFluid) {
            String id = honeyFluid.getHoneyFluidData().id();

            if (id.isEmpty()) {
                return Items.AIR;
            }

            CustomHoneyData honey = getHoneyData(id);

            if (honey == null) {
                return Items.AIR;
            }

            return honey.getBottleData()
                    .bottle()
                    .get();
        }

        return fluid.is(ModFluidTags.HONEY)
                ? Items.HONEY_BOTTLE
                : Items.AIR;
    }
}
