package com.teamresourceful.resourcefulbees.client.rendering.items;

import com.geckolib.renderer.GeoArmorRenderer;
import com.teamresourceful.resourcefulbees.client.model.armor.BeekeeperArmorModel;
import com.teamresourceful.resourcefulbees.common.items.BeekeeperArmorItem;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public class BeekeeperArmorRenderer
        extends GeoArmorRenderer<BeekeeperArmorItem, HumanoidRenderState> {

    public BeekeeperArmorRenderer() {
        super(new BeekeeperArmorModel());
    }
}