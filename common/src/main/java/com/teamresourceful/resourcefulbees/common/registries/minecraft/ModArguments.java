package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.commands.arguments.BeeArgument;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;

public final class ModArguments {

    private ModArguments() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<ArgumentTypeInfo<?, ?>> ARGUMENTS = ResourcefulRegistries.create(Registry.COMMAND_ARGUMENT_TYPE, ModConstants.MOD_ID);

    public static final RegistryEntry<ArgumentTypeInfo<BeeArgument, SingletonArgumentInfo<BeeArgument>.Template>> BEE_TYPE = ARGUMENTS.register("bee", () -> SingletonArgumentInfo.contextFree(BeeArgument::new));
}
