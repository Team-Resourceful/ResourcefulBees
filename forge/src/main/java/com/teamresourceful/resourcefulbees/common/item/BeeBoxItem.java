package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BeeBoxItem extends BlockItem implements ExpandableTooltip {

    private final boolean temp;

    protected BeeBoxItem(Block block, Properties properties, boolean temp) {
        super(block, properties);
        this.temp = temp;
    }

    public static BeeBoxItem temp(Block block, Properties properties) {
        return new BeeBoxItem(block, properties, true);
    }

    public static BeeBoxItem of(Block block, Properties properties) {
        return new BeeBoxItem(block, properties, false);
    }

    @NotNull
    @Override
    public InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        if (temp || entity.level.isClientSide() || !(entity instanceof Bee target) || !entity.isAlive()) {
            return InteractionResult.FAIL;
        }

        CompoundTag stackTag = stack.getOrCreateTag();
        CompoundTag blockTag = stackTag.getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG);
        ListTag bees = blockTag.getList(NBTConstants.NBT_BEES, Tag.TAG_COMPOUND);
        ListTag displayNames = stackTag.getList(NBTConstants.NBT_DISPLAYNAMES, Tag.TAG_STRING);

        if (bees.size() == BeeConstants.MAX_BEES_BEE_BOX) return InteractionResult.FAIL;

        bees.add(BeeInfoUtils.createJarBeeTag(target));
        displayNames.add(StringTag.valueOf(Component.Serializer.toJson(target.getType().getDescription())));

        blockTag.put(NBTConstants.NBT_BEES, bees);
        blockTag.put(NBTConstants.NBT_DISPLAYNAMES, displayNames);
        stackTag.put(NBTConstants.NBT_BLOCK_ENTITY_TAG, blockTag);

        stack.setTag(stackTag);
        player.setItemInHand(hand, stack);
        player.swing(hand);
        target.discard();
        return InteractionResult.SUCCESS;
    }

    public static boolean isFilled(ItemStack stack) {
        //noinspection ConstantConditions
        return !stack.isEmpty() && stack.hasTag() && stack.getTag().contains(NBTConstants.NBT_BLOCK_ENTITY_TAG) && stack.getTag().getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG).contains(NBTConstants.NBT_DISPLAYNAMES);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, world, tooltip, tooltipFlag);
        setupTooltip(stack, world, tooltip, tooltipFlag);
    }

    @Override
    public Component getShiftingDisplay() {
        return TranslationConstants.Items.FOR_MORE_INFO;
    }

    @Override
    public void appendShiftTooltip(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        if (this.temp) {
            components.add(TranslationConstants.Items.BEE_BOX_TOOLTIP_TEMP.withStyle(ChatFormatting.GOLD));
        } else {
            components.add(Component.translatable(TranslationConstants.Items.BEE_BOX_TOOLTIP, BeeConstants.MAX_BEES_BEE_BOX).withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public Component getControlDisplay() {
        return TranslationConstants.Items.TOOLTIP_STATS;
    }

    @Override
    public void appendControlTooltip(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        components.add(TranslationConstants.Items.BEES.withStyle(ChatFormatting.YELLOW));

        //noinspection ConstantConditions
        ListTag bees = isFilled(stack) ? stack.getTag().getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG).getList(NBTConstants.NBT_DISPLAYNAMES, Tag.TAG_STRING) : new ListTag();

        if (bees.isEmpty()) {
            components.add(TranslationConstants.Items.NO_BEES.withStyle(ChatFormatting.GOLD));
        } else {
            bees.stream()
                .map(StringTag.class::cast)
                .forEach(displayJson -> {
                    Component display = Component.Serializer.fromJson(displayJson.getAsString());
                    components.add(Component.translatable(TranslationConstants.Items.BEE_BOX_ENTITY_NAME, display).withStyle(ChatFormatting.GRAY));
                });
        }
    }
}
