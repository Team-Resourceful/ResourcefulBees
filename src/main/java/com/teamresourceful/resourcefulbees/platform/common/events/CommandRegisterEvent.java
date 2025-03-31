package com.teamresourceful.resourcefulbees.platform.common.events;

import com.mojang.brigadier.CommandDispatcher;
import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public record CommandRegisterEvent(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection environment, CommandBuildContext context) {

    public static final EventHelper<CommandRegisterEvent> EVENT = new EventHelper<>();

}
