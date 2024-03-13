package com.iog.Commands.Music;

import com.iog.Utils.ConnectionUtils;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@SuppressWarnings("unused")
public class Pause extends BaseCommand {
	
	public Pause() {
		super(
            Commands.slash("pause", "Pauses the current track")
		);
	}
	
	@Override
	public void run(@NotNull SlashCommandInteractionEvent event) {
		final Guild guild = event.getGuild();
		final Member member = event.getMember();
		final GuildAudioManager manager = GuildAudioManager.of(guild);

		if (!ConnectionUtils.botIsInSameVoiceChannel(member, guild)) {
			event.reply("You have to be in the same voice channel as I").queue();
			return;
		}
		
		if (!manager.getPlayer().isPaused()) {
			manager.getPlayer().setPaused(true);
			event.reply("Paused track").queue();
		} else {
			event.reply("Tracks has already been paused").queue();
		}
		
	}
}

