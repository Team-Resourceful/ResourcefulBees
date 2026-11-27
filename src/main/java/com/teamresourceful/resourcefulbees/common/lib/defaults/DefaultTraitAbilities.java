package com.teamresourceful.resourcefulbees.common.lib.defaults;

import com.teamresourceful.resourcefulbees.api.data.trait.abilities.*;
import com.teamresourceful.resourcefulbees.common.lib.constants.TraitConstants;
import com.teamresourceful.resourcefulbees.common.registries.custom.TraitAbilityRegistry;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.world.item.Items;

public final class DefaultTraitAbilities {

    private DefaultTraitAbilities() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static TraitAbilityRegistry registerDefaultAbilities(TraitAbilityRegistry registry) {
        registry.register(TraitConstants.TELEPORT, new TeleportAbility(Items.ENDER_PEARL, 150, 8.0D));
        registry.register(TraitConstants.SLIMY, new SlimyAbility(Items.SLIME_BALL, 8, 0.5F, 0.4F));
        registry.register(TraitConstants.FLAMMABLE, new FlammableAbility(Items.BLAZE_POWDER, 150, 3));
        registry.register(TraitConstants.ANGRY, new AngryAbility(Items.IRON_AXE, 20.0D, 1000));
        registry.register(TraitConstants.SPIDER, new SpiderAbility(Items.COBWEB));
        registry.register(TraitConstants.SCULK, new SculkAbility(Items.SCULK_CATALYST));
        registry.register(TraitConstants.CLINGY, new ClingyAbility(Items.LEAD, 12, 1, 2, 3));
        registry.register(TraitConstants.COURIER, new CourierAbility(Items.CHEST, 16, 1000, 1000));
        return registry;
    }
}