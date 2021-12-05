package com.resourcefulbees.resourcefulbees.capabilities;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IBeepediaData;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.BeepediaSyncMessage;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class BeepediaData implements IBeepediaData {

    public static final ResourceLocation CAPABILITY_ID = new ResourceLocation(ResourcefulBees.MOD_ID, "beepedia_data");

    private Set<String> bees = new HashSet<>();

    public static void sync(ServerPlayerEntity player, IBeepediaData data) {
        NetPacketHandler.sendToPlayer(new BeepediaSyncMessage(data.serializeNBT(), new PacketBuffer(Unpooled.buffer())), player);
    }

    @Override
    public Set<String> getBeeList() {
        return bees;
    }

    @Override
    public void setBeeList(Set<String> bees) {
        this.bees = bees;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT list = new ListNBT();
        int i = 0;
        for (String bee : bees) {
            list.add(i++, StringNBT.valueOf(bee));
        }
        nbt.put(NBTConstants.NBT_BEEPEDIA_DATA, list);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        bees = new HashSet<>();
        nbt.getList(NBTConstants.NBT_BEEPEDIA_DATA, Constants.NBT.TAG_STRING).forEach(i -> bees.add(i.getAsString()));
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

    public static class Provider implements ICapabilitySerializable<CompoundNBT>, ICapabilityProvider {
        @CapabilityInject(IBeepediaData.class)
        public static Capability<IBeepediaData> BEEPEDIA_DATA = null;

        private LazyOptional<IBeepediaData> instance = LazyOptional.of(BEEPEDIA_DATA::getDefaultInstance);

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return BEEPEDIA_DATA.orEmpty(cap, instance);
        }

        @Override
        public CompoundNBT serializeNBT() {
            return (CompoundNBT) BEEPEDIA_DATA.getStorage().writeNBT(BEEPEDIA_DATA, instance.orElseThrow(() -> new IllegalArgumentException("Could not Serialize BeepediaData.")), null);
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            BEEPEDIA_DATA.getStorage().readNBT(BEEPEDIA_DATA, instance.orElseThrow(() -> new IllegalArgumentException("Could not Deserialize BeepediaData.")), null, nbt);
        }

        public void invalidate() {
            instance.invalidate();
        }
    }
}