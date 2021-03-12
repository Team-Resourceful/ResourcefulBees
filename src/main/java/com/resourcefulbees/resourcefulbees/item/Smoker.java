package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Smoker extends Item {

    private int flag = 0;

    public Smoker(Item.Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ActionResultType useOn(ItemUseContext context) {
        if (!context.getLevel().isClientSide && context.getItemInHand().getDamageValue() < context.getItemInHand().getMaxDamage()) {
            smokeHive(context.getClickedPos(), context.getLevel());
            return ActionResultType.PASS;
        }
        return super.useOn(context);
    }

    protected void smokeHive(BlockPos pos, World world) {
        TileEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TieredBeehiveTileEntity) {
            ((TieredBeehiveTileEntity) blockEntity).smokeHive();
        }
    }

    @Override
	public @NotNull ActionResult<ItemStack> use(World world, @NotNull PlayerEntity player, @NotNull Hand hand) {
	    if (!world.isClientSide) {
	        int damage = player.getItemInHand(hand).getDamageValue();
	        int maxDamage = player.getItemInHand(hand).getMaxDamage();

	        if (flag == 1 && damage < maxDamage) {
                player.getItemInHand(hand).hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(hand));
                flag = 0;
            } else {
	            flag++;
            }

	        Vector3d vec3d = player.getLookAngle();
			double x = player.getX() + vec3d.x * 2;
			double y = player.getY() + vec3d.y * 2;
			double z = player.getZ() + vec3d.z * 2;

            AxisAlignedBB axisalignedbb = new AxisAlignedBB((player.getX() + vec3d.x) - 2.5D, (player.getY() + vec3d.y) - 2D, (player.getZ() + vec3d.z) - 2.5D, (player.getX() + vec3d.x) + 2.5D, (player.getY() + vec3d.y) + 2D, (player.getZ() + vec3d.z) + 2.5D);
            List<MobEntity> list = world.getLoadedEntitiesOfClass(BeeEntity.class, axisalignedbb);
            list.stream()
                    .filter(mobEntity -> mobEntity instanceof BeeEntity && ((BeeEntity) mobEntity).isAngry())
                    .forEach(mobEntity -> {
                        ((BeeEntity) mobEntity).setRemainingPersistentAngerTime(0);
                        mobEntity.setLastHurtByMob(null);
                    });

			ServerWorld worldServer = (ServerWorld)world;
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
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        if(Screen.hasShiftDown()) {
            tooltip.add(new StringTextComponent(TextFormatting.GOLD + I18n.get("item.resourcefulbees.smoker.tooltip")));
            tooltip.add(new StringTextComponent(TextFormatting.GOLD + I18n.get("item.resourcefulbees.smoker.tooltip.1")));
        } else {
            tooltip.add(new StringTextComponent(TextFormatting.YELLOW + I18n.get("resourcefulbees.shift_info")));
        }
    }
}
