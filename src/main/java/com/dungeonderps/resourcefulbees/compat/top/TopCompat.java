package com.dungeonderps.resourcefulbees.compat.top;

import com.dungeonderps.resourcefulbees.RegistryHandler;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.tileentity.HoneycombBlockEntity;
import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.function.Function;

public class TopCompat implements Function<ITheOneProbe, Void>
{
    private final String formatting = TextFormatting.BLUE.toString() + TextFormatting.ITALIC.toString();

    @Nullable
    @Override
    public Void apply(ITheOneProbe theOneProbe)
    {
        theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
            if(world.getTileEntity(data.getPos()) instanceof HoneycombBlockEntity)
            {
                TileEntity honeyBlock = world.getTileEntity(data.getPos());
                final ItemStack honeyCombBlock = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());
                honeyCombBlock.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_BEE_TYPE, honeyBlock.serializeNBT().getString(BeeConst.NBT_BEE_TYPE));
                honeyCombBlock.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_COLOR, honeyBlock.serializeNBT().getString(BeeConst.NBT_COLOR));

                probeInfo.horizontal()
                        .item(honeyCombBlock)
                        .vertical()
                        .itemLabel(honeyCombBlock)
                        .text(formatting + "Resourceful Bees");
                return true;
            }
            if (blockState.getBlock().getRegistryName().getNamespace().equals("resourcefulbees")){
                probeInfo.horizontal()
                        .item(blockState.getBlock().asItem().getDefaultInstance())
                        .vertical()
                        .itemLabel(blockState.getBlock().asItem().getDefaultInstance())
                        .text(formatting + "Resourceful Bees");
                return true;
            }
            return false;
        });

        theOneProbe.registerEntityDisplayOverride((mode, probeInfo, player, world, entity, data) -> {
            if (entity instanceof CustomBeeEntity){
                probeInfo.horizontal()
                        .vertical()
                        .text(entity.getDisplayName().getString())
                        .text(formatting + "Resourceful Bees");
                return true;
            }
            return false;
        });
        return null;
    }
}