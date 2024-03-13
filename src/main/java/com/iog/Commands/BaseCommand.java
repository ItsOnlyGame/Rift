package com.iog.Commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

public abstract class BaseCommand {

	private final SlashCommandData slashCommandData;

	public BaseCommand(SlashCommandData slashCommandData) {
		this.slashCommandData = slashCommandData;
	}

	public String getName() {
		return this.slashCommandData.getName();
	}

	public SlashCommandData getSlashCommandData() {
		return this.slashCommandData;
	}

	public abstract void run(@NotNull SlashCommandInteractionEvent event);
}
