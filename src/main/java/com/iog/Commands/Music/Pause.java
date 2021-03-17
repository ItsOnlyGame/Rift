package com.iog.Commands.Music;

import com.iog.Handlers.Commands.MusicCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.Utils.CommandExecutionException;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Message;
import discord4j.voice.VoiceConnection;

public class Pause extends MusicCommand {

    public Pause() {
        super(
                new String[]{"pause"},
                "Pauses track"
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

            if (!manager.getPlayer().isPaused()) {
                manager.getPlayer().setPaused(true);
                message.getChannel().subscribe(channel -> channel.createMessage("Paused track").subscribe());
            } else {
                message.getChannel().subscribe(channel -> channel.createMessage("Tracks has already been paused").subscribe());
            }
        });
    }
}

