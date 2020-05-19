package com.dungeonderps.resourcefulbees.item;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.ItemGroupResourcefulBees;
import com.dungeonderps.resourcefulbees.utils.Color;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BeeJar extends Item {
    public BeeJar() {
        super(new Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES).maxStackSize(1));
        this.addPropertyOverride(new ResourceLocation("filled"), (stack, world, entity) -> {
            if(isFilled(stack)) {
                return 1.0F;
            }
            return 0.0F;
        });
    }

    public boolean isFilled(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag().contains(BeeConst.NBT_ENTITY);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World playerWorld = context.getPlayer().getEntityWorld();
        ItemStack stack = context.getItem();
        if (playerWorld.isRemote() || !isFilled(stack)) return ActionResultType.FAIL;
        World worldIn = context.getWorld();
        BlockPos pos = context.getPos();
        Entity entity = getEntityFromStack(stack, worldIn, true);
        BlockPos blockPos = pos.offset(context.getFace());
        entity.setPositionAndRotation(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        worldIn.addEntity(entity);
        stack.setTag(new CompoundNBT());
        return ActionResultType.SUCCESS;
    }

    @Nullable
    public Entity getEntityFromStack(ItemStack stack, World world, boolean withInfo) {
        EntityType type = EntityType.byKey(stack.getTag().getString(BeeConst.NBT_ENTITY)).orElse(null);
        if (type != null) {
            Entity entity = type.create(world);
            if (withInfo) entity.read(stack.getTag());
            return entity;
        }
        return null;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity targetIn, Hand hand) {
        if (targetIn.getEntityWorld().isRemote() || (!(targetIn instanceof BeeEntity) || !targetIn.isAlive()) || (isFilled(stack))) {
            return false;
        }

        BeeEntity target = (BeeEntity)targetIn;
        String type = EntityType.getKey(target.getType()).toString();
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString(BeeConst.NBT_ENTITY, type);
        target.writeWithoutTypeId(nbt);
        stack.setTag(nbt);
        player.swingArm(hand);
        player.setHeldItem(hand, stack);
        target.remove(true);
        return true;
    }


    public static int getColor(ItemStack stack, int tintIndex){
        if (tintIndex == 1){
            CompoundNBT honeycombNBT = stack.getChildTag(BeeConst.NBT_ROOT);
            return honeycombNBT != null && !honeycombNBT.getString(BeeConst.NBT_COLOR).isEmpty() ? Color.parseInt(honeycombNBT.getString(BeeConst.NBT_COLOR)) : 0x000000;

        }
        return BeeConst.DEFAULT_COLOR;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        String name;
        if(isFilled(stack)){
            name = I18n.format("item." + ResourcefulBees.MOD_ID + '.' + "bee_jar_filled");
        }
        else
            name = I18n.format("item." + ResourcefulBees.MOD_ID + '.' + "bee_jar_empty");
        return name;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        if (isFilled(stack)){
            CompoundNBT resourcefulTag = stack.getOrCreateChildTag(ResourcefulBees.MOD_ID);
            if (resourcefulTag.contains(BeeConst.NBT_BEE_TYPE)) {
                String type = resourcefulTag.getString(BeeConst.NBT_BEE_TYPE);
                tooltip.add(new TranslationTextComponent(ResourcefulBees.MOD_ID + ".information.bee_type." + type.toLowerCase())
                        .applyTextStyle(TextFormatting.valueOf(BeeInfo.getInfo(type).getColor())));

            }
            else
                tooltip.add(new TranslationTextComponent(ResourcefulBees.MOD_ID + ".information.bee_type.vanilla"));
        }
    }
}
