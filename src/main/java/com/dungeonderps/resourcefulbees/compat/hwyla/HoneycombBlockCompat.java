package com.dungeonderps.resourcefulbees.compat.hwyla;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.HoneycombTileEntity;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class HoneycombBlockCompat implements IComponentProvider, IServerDataProvider<TileEntity> {

    @Override
    public void appendHead(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        tooltip.clear();
        CompoundNBT data = accessor.getServerData();
        if(accessor.getTileEntity() instanceof HoneycombTileEntity){
            TranslationTextComponent text = new TranslationTextComponent("block" + '.' + ResourcefulBees.MOD_ID + '.' + data.getString(BeeConstants.NBT_BEE_TYPE) + "_honeycomb_block");
            tooltip.add(text);
        }
    }

    @Override
    public ItemStack getStack(IDataAccessor accessor, IPluginConfig config) {
        CompoundNBT nbt = accessor.getServerData();
        ItemStack stack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());
        stack.getOrCreateChildTag(BeeConstants.NBT_ROOT).putString(BeeConstants.NBT_BEE_TYPE, nbt.getString(BeeConstants.NBT_BEE_TYPE));
        stack.getOrCreateChildTag(BeeConstants.NBT_ROOT).putString(BeeConstants.NBT_COLOR, nbt.getString(BeeConstants.NBT_COLOR));

        return stack;
    }

    @Override
    public void appendServerData(CompoundNBT compoundNBT, ServerPlayerEntity serverPlayerEntity, World world, TileEntity tileEntity) {
        if (tileEntity instanceof HoneycombTileEntity){
            HoneycombTileEntity blockEntity = (HoneycombTileEntity) tileEntity;

            if (blockEntity.beeType !=null)
                compoundNBT.putString(BeeConstants.NBT_BEE_TYPE, blockEntity.beeType);
            if (blockEntity.blockColor !=null)
                compoundNBT.putString(BeeConstants.NBT_COLOR, blockEntity.blockColor);
        }
    }
}
