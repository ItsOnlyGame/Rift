package com.iog.Commands.Music;

import com.iog.Handlers.Commands.MusicCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Message;
import discord4j.voice.VoiceConnection;

public class ClearQueue extends MusicCommand {

    public ClearQueue() {
        super(
                new String[]{"clearqueue", "clear"}, "Clears the queue"
        );
    }

    @Override
    public void run(Message message, String[] args) {
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
            GuildAudioManager musicManager = GuildAudioManager.of(message.getGuildId().orElseThrow());

            if (musicManager.getPlayer().getPlayingTrack() == null) {
                message.getChannel().subscribe(channel -> channel.createMessage("Queue is already empty").subscribe());
                return;
            }

            musicManager.getScheduler().getQueue().clear();
            message.getChannel().subscribe(channel -> channel.createMessage("Queue cleared").subscribe());
        });

    }
}
