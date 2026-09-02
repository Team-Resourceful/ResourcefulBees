package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.components.predicates.JarBeePredicate;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModDataComponentPredicates {

    public static final ResourcefulRegistry<DataComponentPredicate.Type<?>> PREDICATES = RegistryHelper.create(BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE, ModConstants.MOD_ID);

    public static final RegistryEntry<DataComponentPredicate.Type<JarBeePredicate>> JAR_BEE = PREDICATES.register("jar_bee", () -> JarBeePredicate.TYPE);

    private ModDataComponentPredicates() throws UtilityClassException {
        throw new UtilityClassException();
    }
}
