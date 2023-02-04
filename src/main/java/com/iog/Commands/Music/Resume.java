package com.iog.Commands.Music;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.Utils.CommandExecutionException;
import com.iog.Utils.ConnectionUtils;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommand;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class Resume extends BaseCommand {
	
	public Resume() {
		super(
			new String[]{"resume"},
			ApplicationCommandRequest.builder()
				.type(ApplicationCommand.Type.CHAT_INPUT.getValue())
				.name("resume")
				.description("Resumes the current track")
				.build()
		);
	}
	
	@Override
	public void run(Message message, String[] args) throws CommandExecutionException {
		final GuildAudioManager manager = GuildAudioManager.of(message.getGuildId().orElseThrow());
		
		message.getAuthorAsMember().subscribe(member -> {
			
			if (!ConnectionUtils.botIsInSameVoiceChannel(member, message.getGuildId().orElseThrow())) {
				message.getChannel().subscribe(channel -> channel.createMessage("You have to be in the same voice channel as I").subscribe());
				return;
			}
			
			if (manager.getPlayer().isPaused()) {
				manager.getPlayer().setPaused(false);
				message.getChannel().subscribe(channel -> channel.createMessage("Resuming track").subscribe());
			} else {
				message.getChannel().subscribe(channel -> channel.createMessage("Track is already resuming").subscribe());
			}
		});
	}
	
	@Override
	public void run(ChatInputInteractionEvent interaction) {
		Member member = interaction.getInteraction().getMember().orElseThrow();
		Snowflake guildId = interaction.getInteraction().getGuildId().orElseThrow();
		final GuildAudioManager manager = GuildAudioManager.of(guildId);
		
		if (!ConnectionUtils.botIsInSameVoiceChannel(member, guildId)) {
			interaction.editReply("You have to be in the same voice channel as I").subscribe();
			return;
		}
		
		if (manager.getPlayer().isPaused()) {
			manager.getPlayer().setPaused(false);
			interaction.editReply("Resuming track").subscribe();
		} else {
			interaction.editReply("Track is already resuming").subscribe();
		}
	}
}

