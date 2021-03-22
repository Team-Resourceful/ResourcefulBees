package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Smoker extends Item {

    private int flag = 0;

    public Smoker(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide && context.getItemInHand().getDamageValue() < context.getItemInHand().getMaxDamage()) {
            smokeHive(context.getClickedPos(), context.getLevel());
            return InteractionResult.PASS;
        }
        return super.useOn(context);
    }

    protected void smokeHive(BlockPos pos, Level world) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TieredBeehiveTileEntity) {
            ((TieredBeehiveTileEntity) blockEntity).smokeHive();
        }
    }

    @Override
	public @NotNull InteractionResultHolder<ItemStack> use(Level world, @NotNull Player player, @NotNull InteractionHand hand) {
	    if (!world.isClientSide) {
	        int damage = player.getItemInHand(hand).getDamageValue();
	        int maxDamage = player.getItemInHand(hand).getMaxDamage();

	        if (flag == 1 && damage < maxDamage) {
                player.getItemInHand(hand).hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(hand));
                flag = 0;
            } else {
	            flag++;
            }

	        Vec3 vec3d = player.getLookAngle();
			double x = player.getX() + vec3d.x * 2;
			double y = player.getY() + vec3d.y * 2;
			double z = player.getZ() + vec3d.z * 2;

            AABB axisalignedbb = new AABB((player.getX() + vec3d.x) - 2.5D, (player.getY() + vec3d.y) - 2D, (player.getZ() + vec3d.z) - 2.5D, (player.getX() + vec3d.x) + 2.5D, (player.getY() + vec3d.y) + 2D, (player.getZ() + vec3d.z) + 2.5D);
            List<Mob> list = world.getLoadedEntitiesOfClass(Bee.class, axisalignedbb);
            list.stream()
                    .filter(mobEntity -> mobEntity instanceof Bee && ((Bee) mobEntity).isAngry())
                    .forEach(mobEntity -> {
                        ((Bee) mobEntity).setRemainingPersistentAngerTime(0);
                        mobEntity.setLastHurtByMob(null);
                    });

			ServerLevel worldServer = (ServerLevel) world;
			worldServer.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y + 1.3D, z, 50, 0, 0, 0, 0.01F);
	    }
		return super.use(world, player, hand);
	}

    @Override
    public int getMaxDamage(ItemStack stack) {
        return Config.SMOKER_DURABILITY.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if(Screen.hasShiftDown()) {
            tooltip.add(new TextComponent(ChatFormatting.GOLD + I18n.get("item.resourcefulbees.smoker.tooltip")));
            tooltip.add(new TextComponent(ChatFormatting.GOLD + I18n.get("item.resourcefulbees.smoker.tooltip.1")));
        } else {
            tooltip.add(new TextComponent(ChatFormatting.YELLOW + I18n.get("resourcefulbees.shift_info")));
        }
    }
}
