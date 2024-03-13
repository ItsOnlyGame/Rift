package com.iog.Commands.Music;

import com.iog.Utils.ConnectionUtils;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import com.iog.Commands.BaseCommand;
import com.iog.Handlers.GuildSettings;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.Utils.CommandExecutionException;
import com.iog.Utils.Format;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@SuppressWarnings("unused")
public class Volume extends BaseCommand {
	
	public Volume() {
		super(
            Commands.slash("volume", "Show the current volume")
					.addOption(OptionType.INTEGER, "new-volume", "Change volume")
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
		
		OptionMapping newVolumeExists = event.getOption("new-volume");
		if (newVolumeExists == null) {
			event.reply("Volume is " + manager.getPlayer().getVolume() + "%").queue();
			return;
		}
		
		int newVolume = event.getOption("new-volume").getAsInt();
		manager.getPlayer().setVolume(newVolume);
		event.reply("Volume set to " + newVolume + "%").queue();
		
	}
}
