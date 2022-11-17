package com.iog.Commands.Connection;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;

public class Join extends BaseCommand {
	
	public Join() {
		super(
			new String[]{"join", "connect", "c", "j"},
			ApplicationCommandRequest.builder()
				.name("join")
				.description("Join voice channel")
				.build()
		);
	}
	
	@Override
	public void run(Message message, String[] args) {
		final GuildAudioManager manager = GuildAudioManager.of(message.getGuildId().orElseThrow());
		final AudioProvider provider = manager.getProvider();
		
		message.getAuthorAsMember().subscribe(member -> {
			VoiceState voiceState = member.getVoiceState().block();
			
			if (voiceState == null) {
				message.getChannel().subscribe(channel -> channel.createMessage("You have to be in a voice channel").subscribe());
				return;
			}
			
			VoiceConnection botVoiceConnection = message.getClient().getVoiceConnectionRegistry().getVoiceConnection(message.getGuildId().orElseThrow()).block();
			if (botVoiceConnection != null) {
				// Check if member is not in the same voice channel and is not an admin
				if (!sameVoiceChannel(voiceState.getChannelId().orElseThrow(), botVoiceConnection.getChannelId().block()) && !hasAdminPermissions(message.getAuthorAsMember().blockOptional().orElseThrow())) {
					message.getChannel().subscribe(channel -> channel.createMessage("I'm already in another voice channel").subscribe());
					return;
					
				} else if (sameVoiceChannel(voiceState.getChannelId().orElseThrow(), botVoiceConnection.getChannelId().block())) {
					message.getChannel().subscribe(channel -> channel.createMessage("We are already in the same voice channel").subscribe());
					return;
					
				}
			}
			
			voiceState.getChannel().subscribe(voiceChannel -> voiceChannel.join(spec -> spec.setProvider(provider))
				.subscribe(ignored -> message.getChannel()
					.subscribe(channel -> channel.createMessage("Joined " + voiceChannel.getName()).subscribe())));
		});
	}
	
	@Override
	public void run(ChatInputInteractionEvent interaction) {
		Snowflake guildId = interaction.getInteraction().getGuildId().orElseThrow();
		final GuildAudioManager manager = GuildAudioManager.of(guildId);
		final AudioProvider provider = manager.getProvider();
		
		Member member = interaction.getInteraction().getMember().orElseThrow();
		VoiceState voiceState = member.getVoiceState().block();
		
		if (voiceState == null) {
			interaction.editReply("You have to be in a voice channel").subscribe();
			return;
		}
		
		GatewayDiscordClient client = interaction.getInteraction().getClient();
		VoiceConnection botVoiceConnection = client.getVoiceConnectionRegistry().getVoiceConnection(guildId).block();
		
		if (botVoiceConnection != null) {
			// Check if member is not in the same voice channel and is not an admin
			if (!sameVoiceChannel(voiceState.getChannelId().orElseThrow(), botVoiceConnection.getChannelId().block()) && !hasAdminPermissions(member)) {
				interaction.editReply("I'm already in another voice channel").subscribe();
				return;
				
			} else if (sameVoiceChannel(voiceState.getChannelId().orElseThrow(), botVoiceConnection.getChannelId().block())) {
				interaction.editReply("We are already in the same voice channel").subscribe();
				return;
				
			}
		}
		
		voiceState.getChannel().subscribe(voiceChannel -> voiceChannel.join(spec -> spec.setProvider(provider)).subscribe(test -> {
			interaction.editReply("Joined " + voiceChannel.getName()).subscribe();
		}));
	}
	
	private boolean sameVoiceChannel(Snowflake voiceChannel, Snowflake otherVoiceChannel) {
		return voiceChannel.equals(otherVoiceChannel);
	}
	
	private boolean hasAdminPermissions(Member member) {
		PermissionSet set = member.getBasePermissions().blockOptional().orElseThrow();
		return set.contains(Permission.ADMINISTRATOR) || set.contains(Permission.MOVE_MEMBERS);
	}
}
