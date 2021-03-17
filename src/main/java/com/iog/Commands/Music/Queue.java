package com.iog.Commands.Music;

import com.iog.Handlers.Commands.MusicCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.MusicPlayer.TrackQueue;
import com.iog.Utils.CommandExecutionException;
import com.iog.Utils.Format;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.entity.Message;

public class Queue extends MusicCommand {

    public Queue() {
        super(
                new String[]{"queue", "q"},
                "Gets the queue"
        );
    }

    @Override
    public void run(Message message, String[] args) throws CommandExecutionException {
        final GuildAudioManager manager = GuildAudioManager.of(message.getGuildId().orElseThrow());

        message.getAuthorAsMember().subscribe(member -> {
            TrackQueue queue = manager.getScheduler().getQueue();
            AudioTrack playing = manager.getPlayer().getPlayingTrack();

            if (playing == null) {
                message.getChannel().subscribe(channel -> channel.createMessage("Queue is empty").subscribe());
                return;
            }


            StringBuilder builder = new StringBuilder();

            builder.append("```");
            builder.append("Now playing: ").append(playing.getInfo().title).append(" ").append(Format.millisecondsToString(playing.getDuration())).append("\n");

            int i = 0;
            for (AudioTrack t : queue.getList()) {
                builder.append(i).append(": ").append(t.getInfo().title).append(" | ").append(Format.millisecondsToString(t.getDuration())).append("\n");
                i++;
            }
            builder.append("```");

            message.getChannel().subscribe(channel -> channel.createMessage(builder.toString()).subscribe());
        });
    }
}

