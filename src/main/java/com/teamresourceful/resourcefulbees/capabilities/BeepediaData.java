package com.teamresourceful.resourcefulbees.capabilities;

import com.teamresourceful.resourcefulbees.api.capabilities.IBeepediaData;
import com.teamresourceful.resourcefulbees.lib.constants.NBTConstants;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class BeepediaData implements IBeepediaData {

    private Set<ResourceLocation> bees;

    @Override
    public Set<ResourceLocation> getBeeList() {
        return bees;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT list = new ListNBT();
        int i = 0;
        for (ResourceLocation bee : bees) {
            list.add(i++, StringNBT.valueOf(bee.toString()));
        }
        nbt.put(NBTConstants.NBT_BEEPEDIA_DATA, list);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        bees = new HashSet<>();
        nbt.getList(NBTConstants.NBT_BEEPEDIA_DATA, 10).forEach(i -> bees.add(new ResourceLocation(i.getAsString())));
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IBeepediaData.class, new Capability.IStorage<IBeepediaData>() {

            @Nullable
            @Override
            public INBT writeNBT(Capability<IBeepediaData> capability, IBeepediaData instance, Direction side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IBeepediaData> capability, IBeepediaData instance, Direction side, INBT nbt) {
                if (nbt instanceof CompoundNBT) {
                    instance.deserializeNBT((CompoundNBT) nbt);
                }
            }
        }, BeepediaData::new);
    }
}
