package com.teamresourceful.resourcefulbees.api.data.trait.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.trait.AbilityDisplay;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.util.ModUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public record TeleportAbility(
        Item displayItem,
        int cooldown,
        double range
) implements TraitAbility {

    public static final MapCodec<TeleportAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec()
                    .optionalFieldOf("displayItem", Items.ENDER_PEARL)
                    .forGetter(TeleportAbility::displayItem),

            Codec.intRange(1, 72000)
                    .optionalFieldOf("cooldown", 150)
                    .forGetter(TeleportAbility::cooldown),

            Codec.doubleRange(1.0, 64.0)
                    .optionalFieldOf("range", 8.0)
                    .forGetter(TeleportAbility::range)
    ).apply(instance, TeleportAbility::new));

    @Override
    public ItemStack displayedItem() {
        return new ItemStack(displayItem);
    }

    @Override
    public Component getTitle() {
        return AbilityDisplay.title(ModIdentifier.of("teleport"));
    }

    @Override
    public Component getDescription() {
        return AbilityDisplay.description(ModIdentifier.of("teleport"));
    }

    @Override
    public void run(ResourcefulBee bee) {
        if (bee.level().isClientSide()
                || !bee.isAlive()
                || bee.tickCount % cooldown != 0
                || bee.hasCustomName()
                || !bee.level().isBrightOutside()
                || bee.isPollinating()
                || bee.hasHiveInRange()
                || bee.hasDisruptorInRange()) {
            return;
        }

        double x = bee.getX() + (bee.getRandom().nextDouble() - 0.5) * 2 * range;
        double y = bee.getY() + bee.getRandom().nextInt(-2, 2);
        double z = bee.getZ() + (bee.getRandom().nextDouble() - 0.5) * 2 * range;

        var result = ModUtils.enderEntityTeleport(bee, x, y, z);

        if (result.keyBoolean()) {
            return;
        }

        Vec3 target = result.value();

        if (bee.randomTeleport(target.x(), target.y(), target.z(), true, _ -> false)) {
            bee.level().playSound(null, target.x(), target.y(), target.z(), SoundEvents.ENDERMAN_TELEPORT, bee.getSoundSource(), 1.0F, 1.0F);
        }
    }
}