package com.iog.MusicPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import discord4j.core.object.entity.Message;
import org.apache.log4j.Logger;

public final class TrackScheduler extends AudioEventAdapter {
    private static final transient Logger logger = Logger.getLogger(TrackScheduler.class);

    private final TrackQueue queue;
    private final AudioPlayer player;

    public TrackScheduler(final AudioPlayer player) {
        // The queue may be modified by different threads so guarantee memory safety
        // This does not, however, remove several race conditions currently present
        queue = new TrackQueue();
        this.player = player;
    }

    public TrackQueue getQueue() {
        return queue;
    }


    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {

        Message message = track.getUserData(Message.class);
        message.getChannel().subscribe(channel -> {
            channel.createMessage("Track Exception: " + exception.getMessage()).subscribe();
        });

        logger.error(exception);
        exception.printStackTrace();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {

        Message message = track.getUserData(Message.class);
        message.getChannel().subscribe(channel -> {
            channel.createMessage("Track got stuck ( "+thresholdMs+"ms )").subscribe();
        });
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace) {

        Message message = track.getUserData(Message.class);
        message.getChannel().subscribe(channel -> {
            channel.createMessage("Track got stuck ( "+thresholdMs+"ms ) with ``StackTraceElement[] stackTrace``").subscribe();
        });

        logger.error(stackTrace);
    }

    /**
     * Plays a track, or puts it to queue
     * @param track AudioTrack to play
     */
    public void queue(AudioTrack track) {
        if (this.player.getPlayingTrack() == null) {
            player.startTrack(track, false);
        } else {
            queue.add(track);
        }
    }

    /**
     * Skips song(s)
     * @param index how many tracks to skip
     */
    public void skip(int index) {
        this.queue.skip(index);
        this.nextTrack();
    }

    /**
     * Starts playing the next song on queue
     */
    public void nextTrack() {
        AudioTrack t = queue.pull();
        player.startTrack(t, false);
    }

}