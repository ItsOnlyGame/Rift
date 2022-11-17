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
import discord4j.voice.VoiceConnection;

public class Play extends BaseCommand {
	
	public Play() {
		super(
			new String[]{"play", "p"},
			ApplicationCommandRequest.builder()
				.name("play")
				.description("Plays music")
				.addOption(ApplicationCommandOptionData.builder()
					.name("link-or-query")
					.description("A query or a link to play")
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
				String query;
				
				if (Format.isUrl(args[0].trim())) {
					query = args[0].trim();
				} else {
					query = "ytsearch:" + String.join(" ", args);
				}
				
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
			String query = interaction.getOption("link-or-query").orElseThrow().getValue().orElseThrow().asString();
			
			if (Format.isUrl(query.trim())) {
				query = query.trim();
			} else {
				query = "ytsearch:" + query;
			}
			
			playerManager.loadAndPlay(null, interaction, query);
		}));
	}
}
