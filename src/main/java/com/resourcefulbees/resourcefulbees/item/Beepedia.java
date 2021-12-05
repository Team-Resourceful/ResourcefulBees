package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.api.IBeepediaData;
import com.resourcefulbees.resourcefulbees.capabilities.BeepediaData;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeepediaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Beepedia extends Item {

    public static final String COMPLETE_TAG = "Complete";
    public static final String CREATIVE_TAG = "Creative";

    public Beepedia(Properties properties) {
        super(properties);
    }

    /***
     * Converts beepedia nbt to capability
     *
     * also adds new bees to the player's capability
     * @param stack beepedia item stack
     * @param entity the bee that was interacted with
     * @param data
     */
    public void checkAndAddBees(ItemStack stack, CustomBeeEntity entity, IBeepediaData data) {
        CompoundNBT nbt = stack.hasTag() && stack.getTag() != null ? stack.getTag() : new CompoundNBT();
        ListNBT listNBT;
        if (nbt.getBoolean(CREATIVE_TAG)) return;
        // get the nbt push it to the capability then remove the nbt
        if (nbt.contains(NBTConstants.NBT_BEES)) {
            listNBT = nbt.getList(NBTConstants.NBT_BEES, Constants.NBT.TAG_STRING).copy();
            listNBT.forEach(i -> data.getBeeList().add(i.getAsString()));
            nbt.remove(NBTConstants.NBT_BEES);
            stack.setTag(nbt);
        }


        if (entity != null) {
            data.getBeeList().add(entity.getBeeType());
            data.getBeeList().removeIf(b -> BeeRegistry.getRegistry().getBeeData(b) == null);
            nbt.putBoolean(COMPLETE_TAG, data.getBeeList().size() == BeeRegistry.getRegistry().getBees().size());
            stack.setTag(nbt);
        }
    }

    @Override
    public @NotNull ActionResult<ItemStack> use(World world, PlayerEntity player, @NotNull Hand hand) {
        IBeepediaData data = player.getCapability(BeepediaData.Provider.BEEPEDIA_DATA).orElseGet(BeepediaData::new);
        ItemStack itemstack = player.getItemInHand(hand);
        checkAndAddBees(itemstack, null, data);

        if (world.isClientSide) {
            BeepediaUtils.loadBeepedia(itemstack, null, data);
        }
        return ActionResult.sidedSuccess(itemstack, world.isClientSide());
    }

    @Override
    public @NotNull ActionResultType interactLivingEntity(@NotNull ItemStack stack, PlayerEntity player, @NotNull LivingEntity entity, @NotNull Hand hand) {
        IBeepediaData data = player.getCapability(BeepediaData.Provider.BEEPEDIA_DATA).orElseGet(BeepediaData::new);
        if (entity instanceof CustomBeeEntity) {
            checkAndAddBees(stack, (CustomBeeEntity) entity, data);
            if (player.level.isClientSide) BeepediaUtils.loadBeepedia(stack, entity, data);
            player.setItemInHand(hand, stack);
            return ActionResultType.SUCCESS;
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
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
            assert Minecraft.getInstance().player != null;
            int count = Minecraft.getInstance().player.getCapability(BeepediaData.Provider.BEEPEDIA_DATA).orElseGet(BeepediaData::new).getBeeList().size();
            tooltip.add(new TranslationTextComponent("gui.resourcefulbees.beepedia.home.progress").withStyle(TextFormatting.GRAY)
                    .append(String.format("%d / %d", complete? total : count, total)).withStyle(TextFormatting.GOLD));
        }

    }

    public static boolean isCreative(ItemStack stack){
        return !stack.isEmpty() && stack.hasTag() && stack.getTag() != null && stack.getTag().getBoolean(CREATIVE_TAG);
    }
}
