package com.iog.Commands.Music;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.Utils.ConnectionUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Resume extends BaseCommand {
	
	public Resume() {
		super(
            Commands.slash("resume", "Resumes the current track")
		);
	}
	
	@Override
	public void run(@NotNull SlashCommandInteractionEvent event) {
		final Member member = event.getMember();
		final Guild guild = event.getGuild();

		final GuildAudioManager manager = GuildAudioManager.of(guild);

		if (!ConnectionUtils.botIsInSameVoiceChannel(member, guild)) {
			event.reply("You have to be in the same voice channel as I").queue();
			return;
		}
		
		if (manager.getPlayer().isPaused()) {
			manager.getPlayer().setPaused(false);
			event.reply("Resuming track").queue();
		} else {
			event.reply("Track is already resuming").queue();
		}
	}
}

