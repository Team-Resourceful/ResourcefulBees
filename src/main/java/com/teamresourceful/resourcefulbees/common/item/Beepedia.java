package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.utils.BeepediaUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.List;
import java.util.stream.Collectors;

public class Beepedia extends Item {

    public static final String COMPLETE_TAG = "Complete";
    public static final String CREATIVE_TAG = "Creative";

    public Beepedia(Properties properties) {
        super(properties);
    }

    public void checkAndAddBees(ItemStack stack, CustomBeeEntity entity) {
        CompoundNBT nbt = stack.hasTag() && stack.getTag() != null ? stack.getTag() : new CompoundNBT();
        ListNBT listNBT;
        if (nbt.getBoolean(CREATIVE_TAG)) {
            if (entity != null) {
                nbt.putUUID(NBTConstants.NBT_ENTITY_ID, entity.getUUID());
                stack.setTag(nbt);
            }
            return;
        }
        if (nbt.contains(NBTConstants.NBT_BEES)) {
            listNBT = nbt.getList(NBTConstants.NBT_BEES, 8).copy();
        } else {
            listNBT = new ListNBT();
        }
        if (entity != null) {
            if (!listNBT.contains(StringNBT.valueOf(entity.getBeeType()))) {
                listNBT.add(StringNBT.valueOf(entity.getBeeType()));
            }
            listNBT.removeIf(b -> BeeRegistry.getRegistry().getBeeData(b.getAsString()) == null);
            listNBT = listNBT.stream().distinct().collect(Collectors.toCollection(ListNBT::new));
            nbt.putBoolean(COMPLETE_TAG, listNBT.size() == BeeRegistry.getRegistry().getBees().size());
            nbt.put(NBTConstants.NBT_BEES, listNBT);
            nbt.putUUID(NBTConstants.NBT_ENTITY_ID, entity.getUUID());
            stack.setTag(nbt);
        }
    }



    @Override
    public @NotNull ActionResult<ItemStack> use(World world, PlayerEntity player, @NotNull Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        ItemStack book = PatchouliAPI.get().getBookStack(ModConstants.SHADES_OF_BEES);
        boolean hasShades = player.inventory.contains(book);
        checkAndAddBees(itemstack, null);
        if (world.isClientSide) {
            BeepediaUtils.loadBeepedia(itemstack, null, hasShades);
        }
        return ActionResult.sidedSuccess(itemstack, world.isClientSide());
    }

    @Override
    public @NotNull ActionResultType interactLivingEntity(@NotNull ItemStack stack, PlayerEntity player, @NotNull LivingEntity entity, @NotNull Hand hand) {
        ItemStack book = PatchouliAPI.get().getBookStack(new ResourceLocation("resourcefulbees:fifty_shades_of_bees"));
        boolean hasShades = player.inventory.contains(book);
        if (entity instanceof CustomBeeEntity) {
            checkAndAddBees(stack, (CustomBeeEntity) entity);
            if (player.level.isClientSide) BeepediaUtils.loadBeepedia(stack, entity, hasShades);
            player.setItemInHand(hand, stack);
            return ActionResultType.SUCCESS;
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @Override
    public @NotNull ITextComponent getName(ItemStack stack) {
        if (stack.hasTag() && stack.getTag() != null && !stack.getTag().isEmpty()) {
            if (stack.getTag().getBoolean(CREATIVE_TAG)) return new TranslationTextComponent("item.resourcefulbees.creative_beepedia").withStyle(TextFormatting.LIGHT_PURPLE);
            if (stack.getTag().getBoolean(COMPLETE_TAG)) return new StringTextComponent("✦ ").withStyle(TextFormatting.GREEN).append(super.getName(stack).copy().withStyle(TextFormatting.WHITE));
        }
        return super.getName(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World world, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(new TranslationTextComponent("item.resourcefulbees.information.beepedia").withStyle(TextFormatting.GREEN));
        if (stack.hasTag() && stack.getTag() != null && !stack.getTag().isEmpty()) {
            boolean complete = stack.getTag().getBoolean(COMPLETE_TAG) || stack.getTag().getBoolean(CREATIVE_TAG);
            int total = BeeRegistry.getRegistry().getBees().size();
            int count = stack.getTag().getList(NBTConstants.NBT_BEES, 8).size();
            tooltip.add(new TranslationTextComponent("gui.resourcefulbees.beepedia.home.progress").withStyle(TextFormatting.GRAY)
                    .append(String.format("%d / %d", complete? total : count, total)).withStyle(TextFormatting.GOLD));
        }

    }

    public static boolean isCreative(ItemStack stack){
        return !stack.isEmpty() && stack.hasTag() && stack.getTag() != null && stack.getTag().getBoolean(CREATIVE_TAG);
    }
}
