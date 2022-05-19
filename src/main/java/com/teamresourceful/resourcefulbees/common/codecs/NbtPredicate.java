package com.teamresourceful.resourcefulbees.common.codecs;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record NbtPredicate(CompoundTag tag) {

    public static final NbtPredicate ANY = new NbtPredicate(null);
    public static final Codec<NbtPredicate> CODEC = CompoundTag.CODEC.xmap(NbtPredicate::new, NbtPredicate::tag);

    public boolean matches(ItemStack pStack) {
        return this == ANY || this.matches(pStack.getTag());
    }

    public boolean matches(Entity pEntity) {
        return this == ANY || this.matches(getEntityTagToCompare(pEntity));
    }

    public boolean matches(@Nullable Tag tag) {
        return tag == null ? this == ANY : this.tag == null || NbtUtils.compareNbt(this.tag, tag, true);
    }

    public static CompoundTag getEntityTagToCompare(Entity entity) {
        CompoundTag compoundtag = entity.saveWithoutId(new CompoundTag());
        if (entity instanceof Player player) {
            ItemStack itemstack = player.getInventory().getSelected();
            if (!itemstack.isEmpty()) {
                compoundtag.put("SelectedItem", itemstack.save(new CompoundTag()));
            }
        }

        return compoundtag;
    }

}
