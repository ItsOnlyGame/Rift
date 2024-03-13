package com.iog.Commands.Connection;

import com.iog.Commands.BaseCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.managers.DirectAudioController;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Leave extends BaseCommand {
	
	public Leave() {
		super(
			Commands.slash("leave", "Leave voice channel")
		);
	}
	
    @Override
	public void run(@NotNull SlashCommandInteractionEvent event) {
		Guild guild = event.getGuild();

		DirectAudioController audioController = event.getJDA().getDirectAudioController();

        audioController.disconnect(guild);

		event.reply("Leaving your channel!").queue();
	}
}
