package com.teamresourceful.resourcefulbees.datagen.providers.recipes;

import net.minecraft.advancements.ICriterionInstance;

public class RecipeCriteria {

    private final String id;
    private final ICriterionInstance instance;

    public RecipeCriteria(String id, ICriterionInstance instance) {
        this.id = id;
        this.instance = instance;
    }

    public String getId() {
        return id;
    }

    public ICriterionInstance getInstance() {
        return instance;
    }
}
