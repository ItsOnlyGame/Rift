package com.iog.Commands.Music;

import com.iog.Handlers.Commands.MusicCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.Utils.CommandExecutionException;
import com.iog.Utils.Format;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Message;
import discord4j.voice.VoiceConnection;

public class Skip extends MusicCommand {

    public Skip() {
        super(
                new String[]{"skip", "s"},
                "Skip(s) track"
        );
    }

    @Override
    public void run(Message message, String[] args) throws CommandExecutionException {
        final GuildAudioManager manager = GuildAudioManager.of(message.getGuildId().orElseThrow());

        message.getAuthorAsMember().subscribe(member -> {
            VoiceState voiceState = member.getVoiceState().block();

            if (voiceState == null) {
                message.getChannel().subscribe(channel -> channel.createMessage("You have to be in the same voice channel as I").subscribe());
                return;
            }

            VoiceConnection botVoiceConnection = message.getClient().getVoiceConnectionRegistry().getVoiceConnection(message.getGuildId().orElseThrow()).block();
            if (botVoiceConnection != null) {
                if (!voiceState.getChannelId().orElseThrow().equals(botVoiceConnection.getChannelId().blockOptional().orElseThrow())) {
                    message.getChannel().subscribe(channel -> channel.createMessage("You have to be in the same voice channel as I").subscribe());
                    return;
                }
            }

            if (args.length > 0) {
                Integer index = Format.toInteger(args[0].trim());
                if (index == null) {
                    message.getChannel().subscribe(channel -> channel.createMessage("Please give a index of where in the queue you want to land").subscribe());
                    return;
                }
                manager.getScheduler().skip(index);
            } else {
                manager.getScheduler().skip(0);
                message.getChannel().subscribe(channel -> channel.createMessage("Skipping track...").subscribe());
            }

        });
    }
}


