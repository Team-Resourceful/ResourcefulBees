package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.registry.ItemGroupResourcefulBees;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Smoker extends Item {

    private int flag = 0;

    public Smoker() {
        super(new Properties().setNoRepair().maxStackSize(1).maxDamage(Config.SMOKER_DURABILITY.get()).group(ItemGroupResourcefulBees.RESOURCEFUL_BEES));
    }

    @Nonnull
	public ActionResult<ItemStack> onItemRightClick(World world, @Nonnull PlayerEntity player, @Nonnull Hand hand)
	{
	    if (!world.isRemote)
	    {
            int damage = player.getHeldItem(hand).getDamage();
            int maxDamage = player.getHeldItem(hand).getMaxDamage();

            if (flag == 1 && damage < maxDamage) {
                player.getHeldItem(hand).damageItem(1, player, player1 -> player1.sendBreakAnimation(hand));
                flag = 0;
            } else {
                flag++;
            }

	        Vec3d vec3d = player.getLookVec();
			double x = player.getPosX() + vec3d.x * 2;
			double y = player.getPosY() + vec3d.y * 2;
			double z = player.getPosZ() + vec3d.z * 2;

            AxisAlignedBB axisalignedbb = new AxisAlignedBB((player.getPosX() + vec3d.x) - 2.5D, (player.getPosY() + vec3d.y) - 2D, (player.getPosZ() + vec3d.z) - 2.5D, (player.getPosX() + vec3d.x) + 2.5D, (player.getPosY() + vec3d.y) + 2D, (player.getPosZ() + vec3d.z) + 2.5D);
            List<MobEntity> list = world.getLoadedEntitiesWithinAABB(BeeEntity.class, axisalignedbb);
            for (MobEntity mobEntity : list) {
                if (mobEntity instanceof BeeEntity && ((BeeEntity) mobEntity).isAngry()){
                    ((BeeEntity) mobEntity).setAnger(0);
                    mobEntity.setRevengeTarget(null);
                }
            }

            ServerWorld worldServer = (ServerWorld)world;
			worldServer.spawnParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y + 1.3D, z, 50, 0, 0, 0, 0.01F);
	    }
		return super.onItemRightClick(world, player, hand);
	}

    @Override
    public int getMaxDamage(ItemStack stack) {
        return Config.SMOKER_DURABILITY.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn)
    {
        if(Screen.hasShiftDown())
        {
            tooltip.add(new StringTextComponent(TextFormatting.GOLD + I18n.format("item.resourcefulbees.smoker.tooltip")));
            tooltip.add(new StringTextComponent(TextFormatting.GOLD + I18n.format("item.resourcefulbees.smoker.tooltip.1")));
        }
        else
        {
            tooltip.add(new StringTextComponent(TextFormatting.YELLOW + I18n.format("resourcefulbees.shift_info")));
        }
    }
}
