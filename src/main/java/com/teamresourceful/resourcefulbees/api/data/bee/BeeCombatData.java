package com.teamresourceful.resourcefulbees.api.data.bee;

import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.Map;

public interface BeeCombatData extends BeeData<BeeCombatData> {

    boolean isPassive();

    boolean removeStingerOnAttack();

    boolean inflictsPoison();

    boolean isInvulnerable();

    Map<Holder<Attribute>, Double> attributes();

    AttributeSupplier.Builder buildAttributes(AttributeSupplier.Builder builder);
}
