package com.iog.Commands.Music;

import com.iog.Utils.ConnectionUtils;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.MusicPlayer.TrackQueue;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@SuppressWarnings("unused")
public class Remove extends BaseCommand {
	
	public Remove() {
		super(
            Commands.slash("remove", "Removes the track at that the given index")
					.addOption(OptionType.INTEGER, "index", "The index of a track in queue", true)
		);
	}
	
	@Override
	public void run(@NotNull SlashCommandInteractionEvent event) {
		final Member member = event.getMember();
		final Guild guild = event.getGuild();

		if (!ConnectionUtils.botIsInSameVoiceChannel(member, guild)) {
			event.reply("You have to be in the same voice channel as I").queue();
			return;
		}
		
		GuildAudioManager musicManager = GuildAudioManager.of(event.getGuild());
		
		if (musicManager.getPlayer().getPlayingTrack() == null) {
			event.reply("Queue is empty").queue();
			return;
		}
		
		TrackQueue queue = new TrackQueue(musicManager.getScheduler().getQueue());
		
		int index = event.getOption("index").getAsInt();
		if (index < 0) {
			event.reply("Give a valid position in the queue to remove it").queue();
			return;
		}		
		
		AudioTrackInfo info = queue.get(index - 1).getInfo();
		musicManager.getScheduler().getQueue().remove(index - 1);
		
		event.reply("Removed ``" + info.title + "`` from the queue").queue();
	}
}
