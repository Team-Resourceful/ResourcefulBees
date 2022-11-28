package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BeeJar extends Item {
    public BeeJar(Properties properties) {
        super(properties);
    }

    private static Color getColor(ItemStack stack) {
        if (!stack.hasTag()) return null;
        String color = stack.getOrCreateTag().getCompound(NBTConstants.BeeJar.ENTITY).getString(NBTConstants.BeeJar.COLOR);
        return color.isBlank() || color.equals(BeeConstants.STRING_DEFAULT_ITEM_COLOR) ? null : Color.parse(color);
    }

    @OnlyIn(Dist.CLIENT)
    public static int getColor(ItemStack stack, int tintIndex) {
        CompoundTag tag = stack.getTag();
        if (tintIndex == 1 && tag != null) {
            Color color = getColor(stack);
            return color == null ? BeeConstants.VANILLA_BEE_INT_COLOR : color.getValue();
        }
        return BeeConstants.DEFAULT_ITEM_COLOR;
    }

    public static boolean isFilled(ItemStack stack) {
        return stack.getItem() instanceof BeeJar && !stack.isEmpty() && stack.hasTag() && stack.getOrCreateTag().contains(NBTConstants.BeeJar.ENTITY)
                && stack.getOrCreateTag().getCompound(NBTConstants.BeeJar.ENTITY).contains(NBTConstants.NBT_ID);
    }

    public static ItemStack createFilledJar(ResourceLocation id, int color) {
        ItemStack stack = ModItems.BEE_JAR.get().getDefaultInstance();

        CompoundTag stackTag = new CompoundTag();
        CompoundTag entityTag = new CompoundTag();

        entityTag.putString(NBTConstants.NBT_ID, id.toString());
        entityTag.putString(NBTConstants.BeeJar.COLOR, new Color(color).toString());
        stackTag.putString(NBTConstants.BeeJar.DISPLAY_NAME, Component.Serializer.toJson(Component.translatable("entity." + id.getNamespace() + "." + id.getPath())));

        stackTag.put(NBTConstants.BeeJar.ENTITY, entityTag);
        stack.setTag(stackTag);
        return stack;
    }

    public static boolean hasEntityDisplay(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getOrCreateTag().contains(NBTConstants.BeeJar.DISPLAY_NAME);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            ItemStack stack = context.getItemInHand();
            Level level = context.getLevel();
            if (level.isClientSide() || !isFilled(stack)) return InteractionResult.FAIL;

            ModUtils.summonEntity(stack.getOrCreateTag().getCompound(NBTConstants.BeeJar.ENTITY), level, player, context.getClickedPos().relative(context.getClickedFace()));

            if (!player.isCreative()) {
                if (stack.getCount() > 1) {
                    if (!player.addItem(new ItemStack(ModItems.BEE_JAR.get()))) {
                        player.drop(new ItemStack(ModItems.BEE_JAR.get()), false);
                    }
                    stack.shrink(1);
                } else {
                    stack.setTag(null);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @NotNull
    @Override
    public InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, LivingEntity entity, @NotNull InteractionHand hand) {
        if (entity.getLevel().isClientSide() || !(entity instanceof Bee target) || !entity.isAlive() || isFilled(stack)) {
            return InteractionResult.FAIL;
        }

        CompoundTag stackTag = stack.getOrCreateTag().copy();

        stackTag.put(NBTConstants.BeeJar.ENTITY, BeeInfoUtils.createJarBeeTag(target));
        stackTag.putString(NBTConstants.BeeJar.DISPLAY_NAME, Component.Serializer.toJson(target.getType().getDescription()));

        if (stack.getCount() > 1) {
            ItemStack newJar = new ItemStack(ModItems.BEE_JAR.get());
            newJar.setTag(stackTag);
            stack.shrink(1);
            if (!player.addItem(newJar)) {
                player.drop(newJar, false);
            }
        } else {
            stack.setTag(stackTag);
        }

        player.setItemInHand(hand, stack);
        player.swing(hand);
        target.discard();
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        MutableComponent component = super.getName(stack).copy();
        if (BeeJar.hasEntityDisplay(stack)) {
            MutableComponent display = Component.Serializer.fromJson(stack.getOrCreateTag().getString(NBTConstants.BeeJar.DISPLAY_NAME));
            if (display != null) {
                Color color = getColor(stack);
                display = color != null ? display.withStyle(Style.EMPTY.withColor(color.getValue())) : display.withStyle(ChatFormatting.GRAY);
                component.append(Component.translatable(TranslationConstants.Items.BEE_BOX_ENTITY_NAME, display));
            }
        }
        return component;
    }

    @NotNull
    @Override
    public String getDescriptionId(@NotNull ItemStack stack) {
        return isFilled(stack) ? TranslationConstants.Items.BEE_JAR_FILLED : TranslationConstants.Items.BEE_JAR_EMPTY;
    }
}