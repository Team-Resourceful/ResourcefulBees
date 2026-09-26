package com.teamresourceful.resourcefulbees.common.lib.util;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;

import java.util.Objects;

public class CodecUtils {

    /**
     * Treats the patch as a partial exact-match predicate.
     *
     * <p>Components not present in the patch are ignored.</p>
     *
     * <p>A populated patch entry requires an equal component value.
     * A removed patch entry requires the component to be absent.</p>
     */
    public static boolean matchesComponents(DataComponentPatch expected, DataComponentGetter actual) {
        for (DataComponentType<?> type : expected.keySet()) {
            if (!matchesComponent(expected, actual, type)) {
                return false;
            }
        }

        return true;
    }

    private static <T> boolean matchesComponent(DataComponentPatch expected, DataComponentGetter actual, DataComponentType<T> type) {
        T expectedValue = expected.getPatch(type);
        T actualValue = actual.get(type);

        return Objects.equals(expectedValue, actualValue);
    }
}
