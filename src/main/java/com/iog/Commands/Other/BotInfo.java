package com.iog.Commands.Other;

import com.iog.Commands.BaseCommand;
import com.iog.Main;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class BotInfo extends BaseCommand {

	public BotInfo() {
		super(
			Commands.slash("botinfo", "Tells information about the bot")
		);
	}
	
	@Override
	public void run(@NotNull SlashCommandInteractionEvent event) {
		String selfId = event.getJDA().getSelfUser().getId();

		String text = "```" +
				"Rift: " + "\n" +
				"Version: " + Main.Version + "\n" +
				"ID: " + selfId + "\n" +
				"Github: " + "**https://github.com/ItsOnlyGame/Rift**" + "\n\n" +
				"You can report you issues to github\n```";

		event.reply(text).queue();
	}
}
