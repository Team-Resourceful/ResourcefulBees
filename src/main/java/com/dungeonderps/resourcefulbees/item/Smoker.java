package com.dungeonderps.resourcefulbees.item;

import com.dungeonderps.resourcefulbees.ItemGroupResourcefulBees;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

public class Smoker extends Item {

    public Smoker() {
        super(new Properties().setNoRepair().maxStackSize(1).maxDamage(10).group(ItemGroupResourcefulBees.RESOURCEFULBEES));
    }
    
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
		//TODO Change how it calculates where to spawn the particle.
			if (!world.isRemote)
			{
				int xDirection = 0;
				int zDirection = 0;	
				switch(player.getAdjustedHorizontalFacing().toString()) {
				  case "east":
					  xDirection = 2;
					  	break;
				  case "west":
					  xDirection = -2;
					    break;
				  case "south":
					  zDirection = 2;
					    break;
				  case "north":
					  zDirection = -2;
					    break;
				  default:
					  zDirection = -2;
				}
				ServerWorld worldServer = (ServerWorld)world;
				worldServer.spawnParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, 
						player.getPosX() + xDirection,
						player.getPosY() + 1.0D + player.world.rand.nextDouble(),
						player.getPosZ() + zDirection,
						50, 0, 0, 0, 0.01F);
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
}
