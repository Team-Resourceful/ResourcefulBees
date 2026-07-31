package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.common.blockentities.SmokeableHive;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SmokerItem extends Item {

    public SmokerItem(Properties properties) {
        super(properties.durability(GeneralConfig.smokerDurability));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide() && context.getItemInHand().getDamageValue() < context.getItemInHand().getMaxDamage()) {
            smokeHive(context.getClickedPos(), context.getLevel());
            return InteractionResult.PASS;
        }
        return super.useOn(context);
    }

    protected void smokeHive(BlockPos pos, Level world) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SmokeableHive hive) {
            hive.smokeHive();
        }
    }

    @Override
	public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
	    if (level instanceof ServerLevel serverLevel) {
            player.getItemInHand(hand).hurtAndBreak(1, player, hand);

	        Vec3 vec3d = player.getLookAngle();
			double x = player.getX() + vec3d.x * 2;
			double y = player.getY() + vec3d.y * 2;
			double z = player.getZ() + vec3d.z * 2;

            AABB aabb = new AABB((player.getX() + vec3d.x), (player.getY() + vec3d.y), (player.getZ() + vec3d.z), (player.getX() + vec3d.x), (player.getY() + vec3d.y), (player.getZ() + vec3d.z)).inflate(2.5D);
            level.getEntitiesOfClass(Bee.class, aabb)
                .stream()
                .filter(NeutralMob::isAngry)
                .forEach(bee -> {
                    bee.setPersistentAngerEndTime(0);
                    bee.setLastHurtByMob(null);
                });

            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y + 1.3D, z, 50, 0, 0, 0, 0.01F);
	    }
		return super.use(level, player, hand);
	}

/*    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        components.add(ItemTranslations.SMOKER_TOOLTIP.withStyle(ChatFormatting.GOLD));
        components.add(ItemTranslations.SMOKER_TOOLTIP1.withStyle(ChatFormatting.GOLD));
    }*/
}
