package com.resourcefulbees.resourcefulbees.compat.jei.ingredients;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EntityIngredient {

    @Nullable
    private final Entity entity;
    private final String beeType;
    private final CustomBeeData beeData;
    private final float rotation;

    public EntityIngredient(String beeType, float rotation) {
        this.beeType = beeType;
        this.beeData = BeeRegistry.getRegistry().getBeeData(beeType);
        this.rotation = rotation;

        EntityType<?> entityType = beeType.equals(BeeConstants.VANILLA_BEE_TYPE) ? EntityType.BEE : ForgeRegistries.ENTITIES.getValue(beeData.getEntityTypeRegistryID());

        Minecraft mc = Minecraft.getInstance();
        entity = mc.level != null && entityType != null ? entityType.create(mc.level) : null;
    }

    public String getBeeType() {
        return beeType;
    }

    public @Nullable Entity getEntity() {
        return entity;
    }

    public float getRotation() {
        return rotation;
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("entity.resourcefulbees." + beeType + "_bee");
    }

    public List<ITextComponent> getTooltip() {
        List<ITextComponent> tooltip = new ArrayList<>(BeeInfoUtils.getBeeLore(beeData));
        String desc = I18n.get("tooltip.resourcefulbees.jei.click_bee_info");
        String[] descTooltip = desc.split("\\r?\\n");
        for (String s : descTooltip) {
            tooltip.add(new StringTextComponent(s).withStyle(TextFormatting.GOLD));
        }
        if (Minecraft.getInstance().options.advancedItemTooltips) {
            tooltip.add(new StringTextComponent(beeData.getEntityTypeRegistryID().toString()).withStyle(TextFormatting.DARK_GRAY));
        }
        return tooltip;
    }


}
