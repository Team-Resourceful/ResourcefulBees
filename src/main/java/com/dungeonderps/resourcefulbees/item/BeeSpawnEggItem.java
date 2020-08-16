package com.dungeonderps.resourcefulbees.item;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.lib.NBTConstants;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BeeSpawnEggItem extends SpawnEggItem {

	private final Lazy<? extends EntityType<?>> entityType;

	public BeeSpawnEggItem(final RegistryObject<? extends EntityType<?>> entityTypeSupplier, final int firstColor, final int SecondColor, final Properties properties) {
		super(null, firstColor, SecondColor, properties);
		this.entityType = Lazy.of(entityTypeSupplier);
	}

    @Nonnull
	@Override
    public String getTranslationKey(ItemStack stack) {
        CompoundNBT nbt = stack.getChildTag(NBTConstants.NBT_ROOT);
        String name;
        if ((nbt != null && nbt.contains(NBTConstants.NBT_BEE_TYPE))) {
            name = "item" + '.' + ResourcefulBees.MOD_ID + '.' + nbt.getString(NBTConstants.NBT_BEE_TYPE) + "_spawn_egg";
        } else {
            name = "item" + '.' + ResourcefulBees.MOD_ID + '.' + "bee_spawn_egg";
        }
        return name;
    }

    @Nonnull
	@Override
	public EntityType<?> getType(@Nullable final CompoundNBT p_208076_1_) {
		return entityType.get();
	}

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack itemstack = context.getItem();
        PlayerEntity player = context.getPlayer();
        CompoundNBT tag = itemstack.getChildTag(NBTConstants.NBT_ROOT);
        if (tag != null && player != null) {
            String bee = tag.getString(NBTConstants.NBT_BEE_TYPE);
            if (BeeInfo.getInfo(bee) == null && !itemstack.isEmpty()) {
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