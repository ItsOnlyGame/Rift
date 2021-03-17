package com.iog.Commands.Music;

import com.iog.Handlers.Commands.MusicCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.MusicPlayer.PlayerManager;
import com.iog.Utils.CommandExecutionException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.lyrics.LyricsInfo;
import com.sedmelluq.discord.lavaplayer.track.lyrics.LyricsManager;
import discord4j.core.object.entity.Message;

import java.io.IOException;

public class Lyrics extends MusicCommand {

    public Lyrics() {
        super(
                new String[]{"lyrics"},
                "Gives lyrics to a song"
        );
    }

    @Override
    public void run(Message message, String[] args) throws CommandExecutionException {
        PlayerManager manager = PlayerManager.getInstance();
        AudioTrack audioTrack = GuildAudioManager.of(message.getGuildId().orElseThrow()).getPlayer().getPlayingTrack();

        String lyricsSearchWord;
        if (args.length > 0) {
            lyricsSearchWord = String.join(" ", args);

        } else {
            if (audioTrack == null) {
                message.getChannel().subscribe(channel -> channel.createMessage("Nothing playing, so no lyrics to fetch").subscribe());
                return;

            } else if (audioTrack.getSourceManager().getSourceName().equalsIgnoreCase("Spotify")) {
                lyricsSearchWord = audioTrack.getInfo().author + " " + audioTrack.getInfo().title;
            } else {
                lyricsSearchWord = audioTrack.getInfo().title;
            }
        }

        LyricsInfo lyrics;
        try {
            lyrics = LyricsManager.getLyrics(lyricsSearchWord);
        } catch (IOException e) {
            throw new CommandExecutionException(e.getMessage(), CommandExecutionException.Severity.COMMON, e.getCause(), message);
        }

        if (lyrics.getLyrics().equals("")) {
            message.getChannel().subscribe(channel -> channel.createMessage("No lyrics found").subscribe());
            return;
        }

        String messageContent =
                "```" + "Lyrics for " + lyricsSearchWord + "\n\n" +
                lyrics.getLyrics().trim() +
                "\n\n" + lyrics.getUrl() +
                "```";


        message.getChannel().subscribe(channel -> channel.createMessage(messageContent).subscribe());
    }

}