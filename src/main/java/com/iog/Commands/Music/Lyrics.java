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
import java.util.ArrayList;
import java.util.List;

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

        List<String> parts = getParts(lyrics.getLyrics());

        for (int i = 0; i < parts.size(); i++) {
            StringBuilder builder = new StringBuilder();
            builder.append("```");
            String part = parts.get(i);
            if (i == 0) {
                builder.append("Lyrics for ").append(lyricsSearchWord).append("\n\n");
            }
            builder.append(part);
            if (i + 1 == parts.size()) {
                builder.append("Lyrics URL: ").append(lyrics.getUrl());
            }
            builder.append("```");

            message.getChannel().subscribe(channel -> channel.createMessage(builder.toString()).subscribe());
        }
    }


    private List<String> getParts(String lyricsString) {
        int partitionSize = 2000;

        List<String> parts = new ArrayList<>();

        String[] array = lyricsString.split("\n\n");
        if (array.length == 1)
            array = lyricsString.split("\n");

        StringBuilder temp = new StringBuilder();
        int len = 0;

        for (String str : array) {
            len += str.length() + 4;

            if (len >= partitionSize) {
                parts.add(temp.toString());
                temp = new StringBuilder();
                temp.append(str);
                len = 0;
            } else {
                temp.append(str).append("\n\n");
            }
        }
        if (len != 0) {
            parts.add(temp.toString());
        }

        return parts;
    }

}