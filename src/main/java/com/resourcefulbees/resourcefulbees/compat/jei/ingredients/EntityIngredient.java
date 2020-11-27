package com.resourcefulbees.resourcefulbees.compat.jei.ingredients;

import net.minecraft.client.resources.I18n;

public class EntityIngredient {

    private String beeType;
    private float rotation;

    public EntityIngredient(String beeType, float rotation){
        this.beeType = beeType;
        this.rotation = rotation;
    }

    public String getBeeType(){
        return beeType;
    }
    public float getRotation(){
        return rotation;
    }

    public String getDisplayName(){
        return I18n.format("entity.resourcefulbees."+ beeType + "_bee");
    }
}
