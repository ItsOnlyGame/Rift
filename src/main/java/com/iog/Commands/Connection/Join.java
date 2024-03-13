package com.iog.Commands.Connection;

import com.iog.Commands.BaseCommand;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class Join extends BaseCommand {
	
	public Join() {
		super(Commands.slash("join", "Join voice channel"));
	}
	
    @Override
	public void run(@NotNull SlashCommandInteractionEvent event) {
		final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

		if (memberVoiceState != null && memberVoiceState.inAudioChannel()) {
            AudioChannelUnion voiceChannel = memberVoiceState.getChannel();            
			event.getJDA().getDirectAudioController().connect(voiceChannel);
			event.reply("Joining your voice channel!").queue();
		} else {
			event.reply("You are not in a voice channel").queue();
		}

	}
}
