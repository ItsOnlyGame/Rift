package com.iog.Commands.Music;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;

import com.iog.Utils.ConnectionUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class ClearQueue extends BaseCommand {
	
	public ClearQueue() {
		super(
			Commands.slash("clearqueue", "Clear music queue")
		);
	}
	
	@Override
	public void run(@NotNull SlashCommandInteractionEvent event) {
		Guild guild = event.getGuild();
		Member member = event.getMember();

		if (!ConnectionUtils.botIsInSameVoiceChannel(member, guild)) {
			event.reply("You have to be in the same voice channel as I").queue();
			return;
		}
		
		GuildAudioManager musicManager = GuildAudioManager.of(guild);
		
		if (musicManager.getPlayer().getPlayingTrack() == null) {
			event.reply("Queue is already empty").queue();
			return;
		}
		
		musicManager.getScheduler().getQueue().clear();
		event.reply("Queue cleared").queue();
	}
}
