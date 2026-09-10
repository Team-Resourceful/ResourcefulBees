package com.teamresourceful.resourcefulbees.api.data.bee.breeding;

import net.minecraft.resources.Identifier;

public record ParentPair(Identifier parent1, Identifier parent2) {

    public static ParentPair of(Identifier parent1, Identifier parent2) {
        if (parent1.compareTo(parent2) > 0) {
            return new ParentPair(parent1, parent2);
        } else {
            return new ParentPair(parent2, parent1);
        }
    }

    public static ParentPair of(String parent1, String parent2) {
        return of(Identifier.parse(parent1), Identifier.parse(parent2));
    }
}
