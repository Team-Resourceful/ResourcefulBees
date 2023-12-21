package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.commands.arguments.BeeArgument;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;

public final class ModArguments {

    private ModArguments() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final ResourcefulRegistry<ArgumentTypeInfo<?, ?>> ARGUMENTS = RegistryHelper.create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, ModConstants.MOD_ID);

    public static final RegistryEntry<ArgumentTypeInfo<BeeArgument, SingletonArgumentInfo<BeeArgument>.Template>> BEE_TYPE = ARGUMENTS.register("bee", () -> SingletonArgumentInfo.contextFree(BeeArgument::new));
}
