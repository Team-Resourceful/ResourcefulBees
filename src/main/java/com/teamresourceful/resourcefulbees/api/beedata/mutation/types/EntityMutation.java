package com.teamresourceful.resourcefulbees.api.beedata.mutation.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.display.IEntityRender;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import com.teamresourceful.resourcefulbees.common.utils.predicates.RestrictedEntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record EntityMutation(RestrictedEntityPredicate predicate, double chance, double weight) implements IMutation, IEntityRender {

    public static final Codec<EntityMutation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RestrictedEntityPredicate.CODEC.fieldOf("entity").forGetter(EntityMutation::predicate),
            Codec.doubleRange(0D, 1D).fieldOf("chance").orElse(1D).forGetter(EntityMutation::chance),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").orElse(10D).forGetter(EntityMutation::weight)
    ).apply(instance, EntityMutation::new));

    @Nullable
    @Override
    public BlockPos check(ServerLevel level, BlockPos pos) {
        AABB box = new AABB(pos).expandTowards(new Vec3(0, -2, 0));
        List<Entity> entityList = level.getEntities((Entity) null, box, entity -> predicate().matches(level, entity));
        if (entityList.isEmpty()) return null;
        BlockPos entityPos = entityList.get(0).blockPosition();
        entityList.get(0).discard();
        return entityPos;
    }

    @Override
    public boolean activate(ServerLevel level, BlockPos pos) {
        CompoundTag entityTag = predicate().getTag().map(nbt -> ModUtils.nbtWithData("EntityTag", nbt)).orElse(new CompoundTag());
        Entity entity = predicate().entityType().spawn(level, entityTag, null, null, pos, MobSpawnType.CONVERSION, false, false);
        if (entity != null) {
            level.levelEvent(2005, pos.below(1), 0);
        }
        return true;
    }

    @Override
    public Optional<CompoundTag> tag() {
        return predicate().getTag();
    }

    @Override
    public JsonElement toJson() {
        Optional<JsonElement> json = CODEC.encodeStart(JsonOps.INSTANCE, this).result();
        if (json.isEmpty() || !(json.get() instanceof JsonObject jsonObject)) return JsonNull.INSTANCE;
        jsonObject.addProperty("type", "entity");
        return jsonObject;
    }

    @Override
    public EntityType<?> entityRender() {
        return predicate().entityType();
    }
}