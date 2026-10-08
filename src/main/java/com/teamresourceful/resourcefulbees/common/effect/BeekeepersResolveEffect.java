package com.teamresourceful.resourcefulbees.common.effect;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEffects;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.bee.Bee;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModConstants.MOD_ID)
public class BeekeepersResolveEffect extends MobEffect {

    public BeekeepersResolveEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xE5B93F);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (player.tickCount % 20 != 0) {
            return;
        }

        if (hasFullBeekeeperSet(player)) {
            player.addEffect(new MobEffectInstance(ModEffects.BEEKEEPERS_RESOLVE.holder(), 40, 0, false, false, true));
        }
    }

    private static boolean hasFullBeekeeperSet(ServerPlayer player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.BEEKEEPER_HELMET.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.BEEKEEPER_CHESTPLATE.get())
                && player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.BEEKEEPER_LEGGINGS.get())
                && player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.BEEKEEPER_BOOTS.get());
    }

    public static void invulnerabilityEvent(EntityInvulnerabilityCheckEvent event) {
        if (event.getSource().getEntity() instanceof Bee && event.getEntity() instanceof ServerPlayer player && player.hasEffect(ModEffects.BEEKEEPERS_RESOLVE.holder()))  {
            event.setInvulnerable(true);
        }
    }

    public static boolean beekeeperInRange(ServerLevel level) {
        if (level != null) {
            return !level.getPlayers(serverPlayer -> serverPlayer.hasEffect(ModEffects.BEEKEEPERS_RESOLVE.holder())).isEmpty();
        }
        return false;
    }
}