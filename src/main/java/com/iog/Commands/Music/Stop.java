package com.iog.Commands.Music;

import com.iog.Utils.ConnectionUtils;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Stop extends BaseCommand {
	
	public Stop() {
		super(
            Commands.slash("stop", "Stop playback")
		);
	}

	
	@Override
	public void run(@NotNull SlashCommandInteractionEvent event) {
		final Member member = event.getMember();
		final Guild guild = event.getGuild();
		final GuildAudioManager manager = GuildAudioManager.of(guild);

		if (!ConnectionUtils.botIsInSameVoiceChannel(member, guild)) {
			event.reply("You have to be in the same voice channel as I").queue();
			return;
		}
		
		manager.getScheduler().getQueue().clear();
		manager.getPlayer().stopTrack();
		event.reply("Queue cleared and playback stopped").queue();
	}
}
