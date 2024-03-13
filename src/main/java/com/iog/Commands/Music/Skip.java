package com.iog.Commands.Music;

import com.iog.Utils.ConnectionUtils;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.Utils.CommandExecutionException;
import com.iog.Utils.Format;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Skip extends BaseCommand {

    public Skip() {
        super(
                Commands.slash("skip", "Skips track")
                        .addOption(OptionType.INTEGER, "amount", "Skips the amount of tracks given"));
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

        OptionMapping amountExists = event.getOption("amount");
        if (amountExists == null) {
            manager.getScheduler().skip(0);
            event.reply("Skipping").queue();
            return;
        }

        int skips = event.getOption("amount").getAsInt();
        if (skips > 0) {
            manager.getScheduler().skip(skips);
            event.reply("Skipping").queue();
            return;
        }

        event.reply("Please give a index of where in the queue you want to land").queue();
    }
}
