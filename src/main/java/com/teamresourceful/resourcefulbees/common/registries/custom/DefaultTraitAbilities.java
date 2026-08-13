package com.teamresourceful.resourcefulbees.common.registries.custom;

import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.lib.constants.TraitConstants;
import com.teamresourceful.resourcefulbees.common.lib.util.ModUtils;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEffects;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public final class DefaultTraitAbilities {

    private DefaultTraitAbilities() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static TraitAbilityRegistry registerDefaultAbilities(TraitAbilityRegistry registry) {
        registry.register(TraitConstants.TELEPORT, new DefaultAbility("teleport", Items.ENDER_PEARL, DefaultTraitAbilities::enderAbility));
        registry.register(TraitConstants.SLIMY, new DefaultAbility("slimy", Items.SLIME_BALL, DefaultTraitAbilities::slimeAbility));
        registry.register(TraitConstants.FLAMMABLE, new DefaultAbility("flammable", Items.BLAZE_POWDER, DefaultTraitAbilities::fireAbility));
        registry.register(TraitConstants.ANGRY, new DefaultAbility("angry", Items.IRON_AXE, DefaultTraitAbilities::angryAbility));
        registry.register(TraitConstants.SPIDER, new DefaultAbility("spider", Items.COBWEB, null));
        return registry;
    }

    private static boolean canTeleport(ResourcefulBee bee) {
        return !bee.level().isClientSide() && bee.isAlive() && bee.tickCount % 150 == 0 && !bee.hasCustomName() && bee.level().isBrightOutside() && !bee.isPollinating() && !bee.hasHiveInRange() && !bee.hasDisruptorInRange();
    }

    private static void enderAbility(Bee input) {
        if (input instanceof ResourcefulBee bee && canTeleport(bee)) {
            double x = bee.getX() + (bee.getRandom().nextDouble() - 0.5D) * 16.0D;
            double y = bee.getY() + (bee.getRandom().nextInt(3) - 2);
            double z = bee.getZ() + (bee.getRandom().nextDouble() - 0.5D) * 16.0D;

            var result = ModUtils.enderEntityTeleport(bee, x, y, z);
            if (result.keyBoolean()) return;
            Vec3 target = result.value();
            if (bee.randomTeleport(target.x(), target.y(), target.z(), true)) {
                bee.level().playSound(null, target.x(), target.y(), target.z(), SoundEvents.ENDERMAN_TELEPORT, bee.getSoundSource(), 1.0F, 1.0F);
                bee.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }
        }
    }

    private static void slimeAbility(Bee input) {
        if (input instanceof ResourcefulBee bee && !bee.checkSpawnObstruction(bee.level()) && !bee.wasColliding()) {
            for (int j = 0; j < 8; ++j) {
                float f = bee.getRandom().nextFloat() * ((float) Math.PI * 2F);
                float f1 = bee.getRandom().nextFloat() * 0.5F + 0.5F;
                float f2 = Mth.sin(f) * 1 * 0.5F * f1;
                float f3 = Mth.cos(f) * 1 * 0.5F * f1;
                bee.level().addParticle(ParticleTypes.ITEM_SLIME, bee.getX() + f2, bee.getY(), bee.getZ() + f3, 0.0D, 0.0D, 0.0D);
            }

            bee.playSound(SoundEvents.SLIME_SQUISH, 0.4F, ((bee.getRandom().nextFloat() - bee.getRandom().nextFloat()) * 0.2F + 1.0F) / 0.8F);
            bee.setColliding();
        }
    }

    private static void fireAbility(Bee bee) {
        if (bee.tickCount % 150 == 0) {
            bee.igniteForSeconds(3);
        }
    }

    private static void angryAbility(Bee bee) {
        if (!bee.hasEffect(Holder.direct(ModEffects.CALMING.get()))) {
            LivingEntity player = bee.level().getNearestPlayer(bee, 20);
            bee.setPersistentAngerTarget(player != null ? EntityReference.of(player) : null);
            bee.setPersistentAngerEndTime(1000);
        }
    }

    public record DefaultAbility(String id, Item item, Consumer<Bee> ability) implements TraitAbility {

        @Override
        public ItemStack displayedItem() {
            return new ItemStack(item);
        }

        @Override
        public Component getTitle() {
            return Component.translatable("trait_ability.resourcefulbees." + id);
        }

        @Override
        public Component getDescription() {
            return Component.translatable("trait_ability.resourcefulbees.desc." + id);
        }

        @Override
        public boolean canRun() {
            return ability != null;
        }

        @Override
        public void run(Bee bee) {
            ability.accept(bee);
        }
    }

}
