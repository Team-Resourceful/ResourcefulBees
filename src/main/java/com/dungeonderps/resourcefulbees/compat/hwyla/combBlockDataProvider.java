package com.dungeonderps.resourcefulbees.compat.hwyla;

import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.tileentity.HoneycombBlockEntity;
import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class combBlockDataProvider implements IServerDataProvider<TileEntity> {

    private final List<String> info = new ArrayList<>();

    @Override
    public void appendServerData(CompoundNBT compoundNBT, ServerPlayerEntity serverPlayerEntity, World world, TileEntity tileEntity) {
        if (tileEntity instanceof HoneycombBlockEntity){
            HoneycombBlockEntity blockEntity = (HoneycombBlockEntity) tileEntity;

            compoundNBT.putString(BeeConst.NBT_BEE_TYPE, blockEntity.beeType);
            compoundNBT.putString(BeeConst.NBT_COLOR, blockEntity.blockColor);
            //compoundNBT.putString(BeeConst.);

            //blockEntity.addWailaInfo(info);

            //compoundNBT.keySet().addAll(info);
            //info.clear();
        }
    }
}
