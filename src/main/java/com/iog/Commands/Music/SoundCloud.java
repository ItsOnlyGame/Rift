package com.iog.Commands.Music;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.MusicPlayer.PlayerManager;
import com.iog.Utils.CommandExecutionException;
import com.iog.Utils.ConnectionUtils;
import com.iog.Utils.Format;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.voice.AudioProvider;

public class SoundCloud extends BaseCommand {
	
	public SoundCloud() {
		super(
			new String[]{"soundcloud", "sc"},
			ApplicationCommandRequest.builder()
				.name("play-soundcloud")
				.description("Plays music from SoundCloud")
				.addOption(ApplicationCommandOptionData.builder()
					.name("query")
					.description("Search music from SoundCloud")
					.type(ApplicationCommandOption.Type.STRING.getValue())
					.required(true)
					.build()
				).build()
		);
	}
	
	@Override
	public void run(Message message, String[] args) throws CommandExecutionException {
		PlayerManager playerManager = PlayerManager.getInstance();
		
		final GuildAudioManager manager = GuildAudioManager.of(message.getGuildId().orElseThrow());
		final AudioProvider provider = manager.getProvider();
		
		message.getAuthorAsMember().subscribe(member -> {
			VoiceState voiceState = member.getVoiceState().block();
			
			if (!ConnectionUtils.botIsInSameVoiceChannel(member, message.getGuildId().orElseThrow())) {
				message.getChannel().subscribe(channel -> channel.createMessage("You have to be in the same voice channel as I").subscribe());
				return;
			}
			assert voiceState != null;
			
			if (args.length == 0) {
				message.getChannel().subscribe(channel -> channel.createMessage("No arguments given to search anything").subscribe());
				return;
			}
			
			voiceState.getChannel().subscribe(voiceChannel -> voiceChannel.join(spec -> spec.setProvider(provider)).subscribe(voiceConnection -> {
				String query = "scsearch:" + String.join(" ", args);
				playerManager.loadAndPlay(message, null, query);
			}));
		});
	}
	
	@Override
	public void run(ChatInputInteractionEvent interaction) {
		PlayerManager playerManager = PlayerManager.getInstance();
		Snowflake guildId = interaction.getInteraction().getGuildId().orElseThrow();
		final GuildAudioManager manager = GuildAudioManager.of(guildId);
		final AudioProvider provider = manager.getProvider();
		Member member = interaction.getInteraction().getMember().orElseThrow();
		
		VoiceState voiceState = member.getVoiceState().block();
		
		if (!ConnectionUtils.botIsInSameVoiceChannel(member, guildId)) {
			interaction.editReply("You have to be in the same voice channel as I").subscribe();
			return;
		}
		assert voiceState != null;
		
		boolean queryExists = interaction.getOption("link-or-query").orElseThrow().getValue().isPresent();
		if (!queryExists) {
			interaction.editReply("No arguments given to search anything").subscribe();
			return;
		}
		
		voiceState.getChannel().subscribe(voiceChannel -> voiceChannel.join(spec -> spec.setProvider(provider)).subscribe(voiceConnection -> {
			String query = "scsearch:" + interaction.getOption("query").orElseThrow().getValue().orElseThrow().asString();
			playerManager.loadAndPlay(null, interaction, query);
		}));
	}
}
