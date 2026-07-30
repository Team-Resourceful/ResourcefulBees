package com.teamresourceful.resourcefulbees.common.util;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CodecUtils {
    /**
     * Treats the patch as a partial exact-match predicate.
     *
     * <p>Components not present in the patch are ignored.</p>
     *
     * <p>A populated patch entry requires an equal component value.
     * A removed patch entry requires the component to be absent.</p>
     */
    public static boolean matchesComponents(
            DataComponentPatch expected,
            DataComponentGetter actual
    ) {
        for (Map.Entry<DataComponentType<?>, Optional<?>> entry
                : expected.entrySet()) {

            Object actualValue = actual.get(entry.getKey());
            Optional<?> expectedValue = entry.getValue();

            if (expectedValue.isEmpty()) {
                if (actualValue != null) {
                    return false;
                }
            } else if (!Objects.equals(expectedValue.get(), actualValue)) {
                return false;
            }
        }

        return true;
    }
}
