package com.dungeonderps.resourcefulbees.item;

import com.dungeonderps.resourcefulbees.registry.ItemGroupResourcefulBees;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static net.minecraftforge.common.loot.LootModifierManager.LOGGER;

public class Smoker extends Item {

    public Smoker() {
        super(new Properties().setNoRepair().maxStackSize(1).maxDamage(10).group(ItemGroupResourcefulBees.RESOURCEFUL_BEES));
    }
    
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
	    if (!world.isRemote)
	    {
	        Vec3d vec3d = player.getLookVec();
			double x = player.getPosX() + vec3d.x * 2;
			double y = player.getPosY() + vec3d.y * 2;
			double z = player.getPosZ() + vec3d.z * 2;

			ServerWorld worldServer = (ServerWorld)world;
			worldServer.spawnParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y + 1.3D, z, 50, 0, 0, 0, 0.01F);
	    }
		return super.onItemRightClick(world, player, hand);
	}

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
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

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity targetIn, Hand hand) {
        if (targetIn.getEntityWorld().isRemote() || (!(targetIn instanceof BeeEntity) || !targetIn.isAlive())) {
            return false;
        }
        BeeEntity target = (BeeEntity) targetIn;
        if (target.isAngry()){
            target.setAnger(0);
        }
        return true;
    }

}
