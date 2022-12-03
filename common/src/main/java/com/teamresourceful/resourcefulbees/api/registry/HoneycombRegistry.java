package com.teamresourceful.resourcefulbees.api.registry;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface HoneycombRegistry {

    static HoneycombRegistry get() {
        return ResourcefulBeesAPI.getRegistry().getHoneycombRegistry();
    }

    /**
     * Returns an unmodifiable copy of the Honey Registry.
     * This is useful for iterating over all honey without worry of changing data
     *
     *  @return Returns unmodifiable copy of honey registry.
     */
    Map<String, OutputVariation> getData();

    @Deprecated
    @Nullable
    default OutputVariation getOutputVariation(String name) {
        return getHoneycomb(name);
    }

    /**
     * Returns the OutputVariation for the given name.
     *
     * @param name The name of the OutputVariation to get.
     * @return Returns the OutputVariation for the given name.
     */
    @Nullable
    OutputVariation getHoneycomb(String name);

    /**
     * Returns weather a honeycomb with the given name is registered.
     *
     * @param name The name of the honeycomb to check.
     * @return Returns weather a honeycomb with the given name is registered.
     */
    boolean containsHoneycomb(String name);

    /**
     * Returns a set containing all registered OutputVariation.
     * This is useful for iterating over all honey without worry of changing data
     *
     * @return Returns a set containing all registered OutputVariation.
     */
    Set<OutputVariation> getSetOfHoneycombs();

    /**
     * A helper method that returns a stream using the {@link HoneycombRegistry#getSetOfHoneycombs()} method.
     */
    Stream<OutputVariation> getStreamOfHoneycombs();

    /**
     * @return Returns a set containing all registered honeycombs ids.
     */
    Set<String> getHoneycombTypes();

    void validateDefaults(String id, Optional<ItemStack> defaultComb, Optional<ItemStack> defaultCombBlock);
}
