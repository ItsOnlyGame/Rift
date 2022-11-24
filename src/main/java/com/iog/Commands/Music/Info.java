package com.iog.Commands.Music;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.Utils.CommandExecutionException;
import com.iog.Utils.Format;
import com.iog.Utils.Settings;
import com.sedmelluq.discord.lavaplayer.source.spotify.SpotifyAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommand;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class Info extends BaseCommand {
	
	public Info() {
		super(
			new String[]{"info"},
			ApplicationCommandRequest.builder()
				.type(ApplicationCommand.Type.CHAT_INPUT.getValue())
				.name("info")
				.description("Gives information about the track playing")
				.build()
		);
	}
	
	@Override
	public void run(Message message, String[] args) throws CommandExecutionException {
		GuildAudioManager audioManager = GuildAudioManager.of(message.getGuildId().orElseThrow());
		AudioTrack track = audioManager.getPlayer().getPlayingTrack();
		Member member = message.getAuthorAsMember().blockOptional().orElseThrow();
		
		if (track == null) {
			message.getChannel().subscribe(channel -> channel.createMessage("No tracks playing").subscribe());
			return;
		}
		
		message.getChannel().subscribe(channel -> channel.createMessage(createResponseEmbed(track, member)).subscribe());
	}
	
	@Override
	public void run(ChatInputInteractionEvent interaction) {
		Snowflake guildId = interaction.getInteraction().getGuildId().orElseThrow();
		GuildAudioManager audioManager = GuildAudioManager.of(guildId);
		AudioTrack track = audioManager.getPlayer().getPlayingTrack();
		Member member = interaction.getInteraction().getMember().orElseThrow();
		
		if (track == null) {
			interaction.editReply("No tracks playing").subscribe();
			return;
		}
		
		interaction.editReply().withEmbeds(createResponseEmbed(track, member)).subscribe();
	}
	
	private EmbedCreateSpec createResponseEmbed(AudioTrack track, Member member) {
		EmbedCreateSpec.Builder spec = EmbedCreateSpec.builder()
			.author(track.getInfo().title, null, member.getAvatarUrl())
			.color(Format.hexToColor(Settings.getSettings().defaultColors.get("success")))
			.addField("Author", track.getInfo().author, true)
			.addField("Duration", Format.millisecondsToString(track.getPosition()) + " - " + Format.millisecondsToString(track.getDuration()), true)
			.addField("URL", track.getInfo().uri, false)
			.addField("Author", track.getInfo().author, true);
		
		if (track instanceof SpotifyAudioTrack) {
			spec.addField("Youtube Url", ((SpotifyAudioTrack) track).getYoutubeInfo().uri, false);
			spec.addField("Youtube Author", ((SpotifyAudioTrack) track).getYoutubeInfo().author, false);
		}
		
		return spec.build();
	}
}
