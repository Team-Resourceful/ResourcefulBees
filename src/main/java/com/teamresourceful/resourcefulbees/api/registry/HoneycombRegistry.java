package com.teamresourceful.resourcefulbees.api.registry;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
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
     * Returns an unmodifiable copy of the Honeycomb Variation Registry.
     * This is useful for iterating over all Variations without worry of changing data
     *
     *  @return Returns unmodifiable copy of honeycomb variation registry.
     */
    Map<Identifier, OutputVariation> getVariationData();

    /**
     * Returns the OutputVariation for the given name.
     *
     * @param name The name of the OutputVariation to get.
     * @return Returns the OutputVariation for the given name.
     */
    @Nullable
    OutputVariation getHoneycombVariation(Identifier name);

    /**
     * Returns weather a honeycomb variation with the given name is registered.
     *
     * @param name The name of the honeycomb variation to check.
     * @return Returns `true` if a honeycomb variation with the given name is registered.
     */
    boolean containsVariation(Identifier name);

    /**
     * Returns a set containing all registered OutputVariation.
     * This is useful for iterating over all honey without worry of changing data
     *
     * @return Returns a set containing all registered OutputVariation.
     */
    Set<OutputVariation> getSetOfVariations();

    /**
     * A helper method that returns a stream using the {@link HoneycombRegistry#getSetOfVariations()} method.
     */
    Stream<OutputVariation> getStreamOfVariations();

    /**
     * @return Returns an immutable set containing all registered honeycomb variation ids.
     */
    Set<Identifier> getVariationIDs();

    void validateDefaults(Identifier id, Optional<ItemStackTemplate> defaultComb, Optional<ItemStackTemplate> defaultCombBlock);
}
