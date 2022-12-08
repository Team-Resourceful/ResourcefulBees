package com.teamresourceful.resourcefulbees.common.lib.tools;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.Parents;
import it.unimi.dsi.fastutil.Hash;

import java.util.Objects;

public class MapStrategies {

    public static final Hash.Strategy<? super Object> BREED_TREE = new Hash.Strategy<>() {
        @Override
        public int hashCode(Object o) {
            if (o instanceof String) {
                return Objects.hash(o, o);
            } else if (o instanceof Pair<?, ?> pair) {
                if (pair.getFirst() instanceof String first && pair.getSecond() instanceof String second) {
                    if (first.compareTo(second) > 0) {
                        return Objects.hash(first, second);
                    }
                    return Objects.hash(second, first);
                }
            }
            return o.hashCode();
        }

        @Override
        public boolean equals(Object a, Object b) {
            if (Objects.equals(a, b)) return true;
            String a1 = getParent1(a);
            String a2 = getParent2(a);
            String b1 = getParent1(b);
            String b2 = getParent2(b);
            if (a1 == null || a2 == null || b1 == null || b2 == null) return false;
            return (a1.equals(b1) && a2.equals(b2));
        }
    };

    private static String getParent1(Object o) {
        if (o instanceof String str) {
            return str;
        } else if (o instanceof Pair<?, ?> pair) {
            if (pair.getFirst() instanceof String first && pair.getSecond() instanceof String second) {
                if (first.compareTo(second) > 0) {
                    return first;
                }
                return second;
            }
        } else if (o instanceof Parents parents) {
            return parents.getParent1();
        }
        return null;
    }

    private static String getParent2(Object o) {
        if (o instanceof String str) {
            return str;
        } else if (o instanceof Pair<?, ?> pair) {
            if (pair.getFirst() instanceof String first && pair.getSecond() instanceof String second) {
                if (first.compareTo(second) > 0) {
                    return second;
                }
                return first;
            }
        } else if (o instanceof Parents parents) {
            return parents.getParent2();
        }
        return null;
    }
}
