package com.iog.Commands.Music;

import com.iog.Handlers.Commands.MusicCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.MusicPlayer.TrackQueue;
import com.iog.Utils.CommandExecutionException;
import com.iog.Utils.Format;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Message;
import discord4j.voice.VoiceConnection;

public class Remove extends MusicCommand {

    public Remove() {
        super(new String[]{"remove", "rm"}, "Remove item from queue");
    }

    @Override
    public void run(Message message, String[] args) throws CommandExecutionException {
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
                message.getChannel().subscribe(channel -> channel.createMessage("Queue is empty").subscribe());
                return;
            }

            TrackQueue queue = new TrackQueue(musicManager.getScheduler().getQueue());

            Integer index = Format.toInteger(args[0]);

            if (index == null) {
                message.getChannel().subscribe(channel -> channel.createMessage("Give a valid position in the queue to remove it").subscribe());
                return;
            }

            if (index > queue.size() || index <= 0) {
                message.getChannel().subscribe(channel -> channel.createMessage("Give a valid position in the queue to remove it").subscribe());
                return;
            }

            AudioTrackInfo info = queue.get(index - 1).getInfo();
            musicManager.getScheduler().getQueue().remove(index - 1);


            message.getChannel().subscribe(channel -> channel.createMessage("Removed ``"+info.title+"`` from the queue").subscribe());
        });
    }
}
