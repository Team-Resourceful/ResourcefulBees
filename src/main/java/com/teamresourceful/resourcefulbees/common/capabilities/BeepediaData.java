package com.teamresourceful.resourcefulbees.common.capabilities;

import com.teamresourceful.resourcefulbees.api.capabilities.IBeepediaData;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class BeepediaData implements IBeepediaData {

    private Set<ResourceLocation> bees = new HashSet<>();

    @Override
    public Set<ResourceLocation> getBeeList() {
        return bees;
    }

    @Override
    public CompoundTag serializeNBT() {
        ListTag list = new ListTag();
        bees.stream()
                .map(ResourceLocation::toString)
                .map(StringTag::valueOf)
                .forEachOrdered(list::add);
        return ModUtils.nbtWithData(NBTConstants.NBT_BEEPEDIA_DATA, list);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        bees = new HashSet<>();
        nbt.getList(NBTConstants.NBT_BEEPEDIA_DATA, 10).forEach(i -> bees.add(new ResourceLocation(i.getAsString())));
    }

    public static void register() {
//        CapabilityManager.INSTANCE.register(IBeepediaData.class, new Capability.IStorage<IBeepediaData>() {
//
//            @Nullable
//            @Override
//            public Tag writeNBT(Capability<IBeepediaData> capability, IBeepediaData instance, Direction side) {
//                return instance.serializeNBT();
//            }
//
//            @Override
//            public void readNBT(Capability<IBeepediaData> capability, IBeepediaData instance, Direction side, Tag nbt) {
//                if (nbt instanceof CompoundTag) {
//                    instance.deserializeNBT((CompoundTag) nbt);
//                }
//            }
//        }, BeepediaData::new);
    }
}
