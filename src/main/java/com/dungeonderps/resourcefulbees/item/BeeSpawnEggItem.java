package com.dungeonderps.resourcefulbees.item;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;

public class BeeSpawnEggItem extends SpawnEggItem {

	private final Lazy<? extends EntityType<?>> entityType;

	public BeeSpawnEggItem(final RegistryObject<? extends EntityType<?>> entityTypeSupplier, final int firstColor, final int SecondColor, final Properties properties) {
		super(null, firstColor, SecondColor, properties);
		this.entityType = Lazy.of(entityTypeSupplier);
	}

	@Override
    public String getTranslationKey(ItemStack stack) {
        CompoundNBT beeType = stack.getChildTag("ResourcefulBees");
        String name;
        if ((beeType != null && beeType.contains("beeType"))) {
            name = "item" + '.' + ResourcefulBees.MOD_ID + '.' + beeType.getString("beeType").toLowerCase() + "_spawn_egg";
        } else {
            name = "item" + '.' + ResourcefulBees.MOD_ID + '.' + "bee_spawn_egg";
        }
        return name;
    }

	@Override
	public EntityType<?> getType(@Nullable final CompoundNBT p_208076_1_) {
		return entityType.get();
	}

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack itemstack = context.getItem();
        PlayerEntity player = context.getPlayer();
        if (itemstack.getChildTag("ResourcefulBees") != null) {
            String stackNbt = itemstack.getChildTag("ResourcefulBees").getString("beeType");
            if (CustomBeeEntity.BEE_INFO.get(stackNbt) == null && !itemstack.isEmpty()) {
                for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                    if (player.inventory.getStackInSlot(i) == itemstack) {
                        player.inventory.removeStackFromSlot(i);
                    }
                }
                return ActionResultType.FAIL;
            } else
                return super.onItemUse(context);
        }
        else
            return super.onItemUse(context);
    }
}