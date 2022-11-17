package com.iog.Commands.Music;

import com.iog.Commands.BaseCommand;
import com.iog.Handlers.GuildSettings;
import com.iog.MusicPlayer.GuildAudioManager;
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
import discord4j.voice.VoiceConnection;

public class Volume extends BaseCommand {
	
	public Volume() {
		super(
			new String[]{"volume", "vol", "v"},
			ApplicationCommandRequest.builder()
				.name("volume")
				.description("Show the current volume")
				.addOption(ApplicationCommandOptionData.builder()
					.name("new-volume")
					.description("Change volume")
					.type(ApplicationCommandOption.Type.INTEGER.getValue())
					.required(false)
					.build())
				.build()
        );
	}
	
	@Override
	public void run(Message message, String[] args) throws CommandExecutionException {
		final GuildAudioManager manager = GuildAudioManager.of(message.getGuildId().orElseThrow());
		
		message.getAuthorAsMember().subscribe(member -> {
			
			if (args.length == 0) {
				message.getChannel().subscribe(channel -> channel.createMessage("Volume is " + manager.getPlayer().getVolume() + "%").subscribe());
				return;
			}
			
			if (!ConnectionUtils.botIsInSameVoiceChannel(member, message.getGuildId().orElseThrow())) {
				message.getChannel().subscribe(channel -> channel.createMessage("You have to be in the same voice channel as I").subscribe());
				return;
			}
			
			
			Integer vol = Format.toInteger(args[0]);
			if (vol == null) {
				message.getChannel().subscribe(channel -> channel.createMessage("Given argument wasn't a valid number").subscribe());
				return;
			}
			
			message.getChannel().subscribe(channel -> {
				channel.createMessage("Volume set to " + vol + "%").subscribe();
				manager.getPlayer().setVolume(vol);
				GuildSettings.of(message.getGuildId().orElseThrow().asLong()).setVolume(vol).save();
			});
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
		
		boolean newVolumeExists = interaction.getOption("new-volume").orElseThrow().getValue().isPresent();
		if (!newVolumeExists) {
			interaction.editReply("Volume is " + manager.getPlayer().getVolume() + "%").subscribe();
			return;
		}
		
		int newVolume = (int)interaction.getOption("new-volume").orElseThrow().getValue().get().asLong();
		manager.getPlayer().setVolume(newVolume);
		interaction.editReply("Volume set to " + newVolume + "%").subscribe();
		
	}
}
