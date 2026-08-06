package com.teamresourceful.resourcefulbees.common.lib.tools;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.Parents;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public final class MapStrategies {

    private MapStrategies() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final Hash.Strategy<? super Object> BREED_TREE = new Hash.Strategy<>() {
        @Override
        public int hashCode(Object o) {
            if (o instanceof Identifier) {
                return Objects.hash(o, o);
            } else if (o instanceof Pair<?, ?> pair && pair.getFirst() instanceof Identifier first && pair.getSecond() instanceof Identifier second) {
                if (first.compareTo(second) > 0) {
                    return Objects.hash(first, second);
                }
                return Objects.hash(second, first);
            }
            return o.hashCode();
        }

        @Override
        public boolean equals(Object a, Object b) {
            if (Objects.equals(a, b)) return true;
            Identifier a1 = getParent1(a);
            Identifier a2 = getParent2(a);
            Identifier b1 = getParent1(b);
            Identifier b2 = getParent2(b);
            if (a1 == null || a2 == null || b1 == null || b2 == null) return false;
            return (a1.equals(b1) && a2.equals(b2));
        }
    };

    private static Identifier getParent1(Object o) {
        if (o instanceof Identifier str) {
            return str;
        } else if (o instanceof Pair<?, ?> pair && pair.getFirst() instanceof Identifier first && pair.getSecond() instanceof Identifier second) {
            if (first.compareTo(second) > 0) {
                return first;
            }
            return second;
        } else if (o instanceof Parents parents) {
            return parents.getParent1();
        }
        return null;
    }

    private static Identifier getParent2(Object o) {
        if (o instanceof Identifier str) {
            return str;
        } else if (o instanceof Pair<?, ?> pair && pair.getFirst() instanceof Identifier first && pair.getSecond() instanceof Identifier second) {
            if (first.compareTo(second) > 0) {
                return second;
            }
            return first;
        } else if (o instanceof Parents parents) {
            return parents.getParent2();
        }
        return null;
    }
}
