package com.teamresourceful.resourcefulbees.api.data.trait.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.trait.AbilityDisplay;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record SlimyAbility(
        Item displayItem,
        int particleCount,
        float particleSpread,
        float soundVolume
) implements TraitAbility {

    public static final MapCodec<SlimyAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec()
                    .optionalFieldOf("displayItem", Items.SLIME_BALL)
                    .forGetter(SlimyAbility::displayItem),
            Codec.intRange(0, 256)
                    .optionalFieldOf("particleCount", 8)
                    .forGetter(SlimyAbility::particleCount),
            Codec.floatRange(0.0F, 16.0F)
                    .optionalFieldOf("particleSpread", 0.5F)
                    .forGetter(SlimyAbility::particleSpread),
            Codec.floatRange(0.0F, 4.0F)
                    .optionalFieldOf("soundVolume", 0.4F)
                    .forGetter(SlimyAbility::soundVolume)
    ).apply(instance, SlimyAbility::new));

    @Override
    public ItemStack displayedItem() {
        return new ItemStack(displayItem);
    }

    @Override
    public Component getTitle() {
        return AbilityDisplay.title(ModIdentifier.of("slimy"));
    }

    @Override
    public Component getDescription() {
        return AbilityDisplay.description(ModIdentifier.of("slimy"));
    }

    @Override
    public void run(ResourcefulBee bee) {
        if (!bee.checkSpawnObstruction(bee.level()) && !bee.wasColliding()) {
            for (int j = 0; j < particleCount; ++j) {
                float angle = bee.getRandom().nextFloat() * (Mth.PI * 2F);
                float distance = bee.getRandom().nextFloat() * 0.5F + 0.5F;
                float xOffset = Mth.sin(angle) * particleSpread * distance;
                float zOffset = Mth.cos(angle) * particleSpread * distance;

                bee.level().addParticle(
                        ParticleTypes.ITEM_SLIME,
                        bee.getX() + xOffset,
                        bee.getY(),
                        bee.getZ() + zOffset,
                        0.0D, 0.0D, 0.0D
                );
            }

            bee.playSound(
                    SoundEvents.SLIME_SQUISH,
                    soundVolume,
                    ((bee.getRandom().nextFloat()
                            - bee.getRandom().nextFloat()) * 0.2F + 1.0F) / 0.8F
            );

            bee.setColliding();
        }
    }
}