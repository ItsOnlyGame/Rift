package com.iog.Commands.Music;

import com.iog.Handlers.Commands.MusicCommand;
import com.iog.Handlers.GuildSettings;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.Utils.CommandExecutionException;
import com.iog.Utils.Format;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Message;
import discord4j.voice.VoiceConnection;

public class Volume extends MusicCommand {

    public Volume() {
        super(
                new String[]{"volume", "vol", "v"},
                "Changes the volume"
        );
    }

    @Override
    public void run(Message message, String[] args) throws CommandExecutionException {
        final GuildAudioManager manager = GuildAudioManager.of(message.getGuildId().orElseThrow());

        message.getAuthorAsMember().subscribe(member -> {

            if (args.length == 0) {
                message.getChannel().subscribe(channel -> channel.createMessage("Volume is "+ manager.getPlayer().getVolume() +"%").subscribe());
                return;
            }


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


            Integer vol = Format.toInteger(args[0]);
            if (vol == null) {
                message.getChannel().subscribe(channel -> channel.createMessage("Given argument wasn't a valid number").subscribe());
                return;
            }

            message.getChannel().subscribe(channel -> {
                channel.createMessage("Volume set to "+ vol +"%").subscribe();
                manager.getPlayer().setVolume(vol);
                GuildSettings.of(message.getGuildId().orElseThrow().asLong()).setVolume(vol).save();
            });
        });
    }
}
