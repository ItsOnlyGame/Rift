package com.iog.Commands.Music;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.Utils.Format;
import com.iog.Utils.Settings;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Info extends BaseCommand {
	
	public Info() {
		super(
            Commands.slash("info", "Gives information about the track playing")
		);
	}
	
	@Override
	public void run(@NotNull SlashCommandInteractionEvent event) {
		GuildAudioManager audioManager = GuildAudioManager.of(event.getGuild());
		AudioTrack track = audioManager.getPlayer().getPlayingTrack();
		Member member = event.getMember();
		
		if (track == null) {
			event.reply("No tracks playing").queue();
			return;
		}
		
		event.replyEmbeds(createResponseEmbed(track, member)).queue();
	}
	
	private MessageEmbed createResponseEmbed(AudioTrack track, Member member) {
		EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(track.getInfo().title, null, member.getAvatarUrl());
        embedBuilder.setColor(Color.decode(Settings.getSettings().defaultColors.get("success")));
        embedBuilder.addField("Author", track.getInfo().author, true);
        embedBuilder.addField("Duration", Format.millisecondsToString(track.getPosition()) + " - " + Format.millisecondsToString(track.getDuration()), true);
        embedBuilder.addField("URL", track.getInfo().uri, false);
        embedBuilder.addField("Author", track.getInfo().author, true);
		

        /*
		if (track instanceof SpotifyAudioTrack) {
			spec.addField("Youtube Url", ((SpotifyAudioTrack) track).getYoutubeInfo().uri, false);
			spec.addField("Youtube Author", ((SpotifyAudioTrack) track).getYoutubeInfo().author, false);
		}
        */
		
		return embedBuilder.build();
	}
}
