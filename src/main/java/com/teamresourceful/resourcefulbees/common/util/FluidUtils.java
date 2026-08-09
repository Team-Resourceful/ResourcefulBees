package com.teamresourceful.resourcefulbees.common.util;

import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import com.teamresourceful.resourcefulbees.api.registry.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.items.honey.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModFluidTags;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModFluids;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class FluidUtils {
    private FluidUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static void fillBottle(ResourceHandler<FluidResource> tank, int index, Player player, InteractionHand hand) {
        FluidResource resource = tank.getResource(index);
        ItemStack bottle = getHoneyBottleFromFluid(resource.getFluid()).getDefaultInstance();

        if (tank.getAmountAsInt(index) < BeeConstants.HONEY_PER_BOTTLE) return;
        if (bottle.isEmpty()) return;

        try (Transaction transaction = Transaction.openRoot()) {
            tank.extract(index, resource, BeeConstants.HONEY_PER_BOTTLE, transaction);
            transaction.commit();
            bottleAction(bottle, SoundEvents.BOTTLE_FILL, player, hand);
        }
    }

    public static void emptyBottle(ResourceHandler<FluidResource> tank, Player player, InteractionHand hand) {
        FluidResource resource = FluidResource.of(getHoneyFluidFromBottle(player.getItemInHand(hand)));
        if (resource.isEmpty()) return;

        try(Transaction transaction = Transaction.openRoot()) {
            int inserted = tank.insert(resource, BeeConstants.HONEY_PER_BOTTLE, transaction);
            if (inserted < BeeConstants.HONEY_PER_BOTTLE) return;
            transaction.commit();
            bottleAction(new ItemStack(Items.GLASS_BOTTLE), SoundEvents.BOTTLE_EMPTY, player, hand);
        }
    }

    private static void bottleAction(ItemStack returnStack, SoundEvent soundEvent, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getCount() > 1) {
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            player.addItem(returnStack);
        } else {
            player.setItemInHand(hand, returnStack);
        }
        player.level().playSound(null, player.blockPosition(), soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public static Fluid getHoneyFluidFromBottle(ItemStack bottleOutput) {
        Item item = bottleOutput.getItem();
        if (item == Items.HONEY_BOTTLE) {
            return ModFluids.HONEY_STILL.get();
        } else if (item instanceof CustomHoneyBottleItem honey) {
            String id = honey.getHoneyData().id();
            if (id.isEmpty()) return Fluids.EMPTY;
            HoneyFluidData fluidData = HoneyRegistry.get().getHoneyData(id).getFluidData();
            return fluidData.stillFluid().get();
        }
        return Fluids.EMPTY;
    }

    public static Item getHoneyBottleFromFluid(Fluid fluid) {
        if (fluid instanceof CustomHoneyFluid.Still honeyFluid) {
            String id = honeyFluid.getHoneyFluidData().id();
            if (id.isEmpty()) return Items.AIR;
            return HoneyRegistry.get().getHoneyData(id).getBottleData().bottle().get();
        } else if (fluid.is(ModFluidTags.HONEY)) {
            return Items.HONEY_BOTTLE;
        }
        return Items.AIR;
    }
//
//    public static void checkBottleAndCapability(FluidContainer tank, BlockEntity entity, Player player, Level level, BlockPos pos, InteractionHand hand) {
//        Item item = player.getItemInHand(hand).getItem();
//        if (item instanceof BottleItem) {
//            fillBottle(tank, player, hand);
//        } else if (item.equals(Items.HONEY_BOTTLE)) {
//            emptyBottle(tank, player, hand);
//        } else if (!player.isShiftKeyDown() && !level.isClientSide() && player instanceof ServerPlayer serverPlayer && entity instanceof ContentMenuProvider<?> provider) {
//            provider.openMenu(serverPlayer);
//        }
//    }
//
//    public static void writeToBuffer(FluidHolder holder, FriendlyByteBuf buffer) {
//        if (holder.isEmpty()) {
//            buffer.writeBoolean(false);
//        } else {
//            buffer.writeBoolean(true);
//            buffer.writeVarInt(BuiltInRegistries.FLUID.getId(holder.getFluid()));
//            buffer.writeVarLong(holder.getFluidAmount());
//            buffer.writeNbt(holder.getCompound());
//        }
//    }
//
//    public static FluidHolder readFromBuffer(FriendlyByteBuf buffer) {
//        if (!buffer.readBoolean()) return FluidHooks.emptyFluid();
//        Fluid fluid = BuiltInRegistries.FLUID.byId(buffer.readVarInt());
//        long amount = buffer.readVarLong();
//        return FluidHolder.of(fluid, amount, buffer.readNbt());
//    }
}
