package com.iog.Commands.Music;

import com.iog.Utils.ConnectionUtils;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import com.iog.Commands.BaseCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.MusicPlayer.PlayerManager;
import com.iog.Utils.Format;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Play extends BaseCommand {

    public Play() {
        super(
                Commands.slash("play", "Plays music")
                        .addOption(OptionType.STRING, "link-or-query", "A query or a link to play", true)
        );
    }

    @Override
    public void run(@NotNull SlashCommandInteractionEvent event) {
        final Guild guild = event.getGuild();
        final Member member = event.getMember();

        if (!ConnectionUtils.botIsInSameVoiceChannel(member, guild)) {
            event.reply("You have to be in the same voice channel as I").queue();
            return;
        }

        String query = event.getOption("link-or-query").getAsString();

        event.deferReply().queue();

        if (!guild.getSelfMember().getVoiceState().inAudioChannel()) {
            final GuildVoiceState memberVoiceState = member.getVoiceState();

            if (memberVoiceState.inAudioChannel()) {
                event.getJDA().getDirectAudioController().connect(memberVoiceState.getChannel());
            }
        }

        if (Format.isUrl(query.trim())) {
            query = query.trim();
        } else {
            query = "ytsearch:" + query;
        }

        PlayerManager playerManager = PlayerManager.getInstance();
        playerManager.loadAndPlay(event, query);
    }
}
