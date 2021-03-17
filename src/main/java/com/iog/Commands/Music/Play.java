package com.iog.Commands.Music;

import com.iog.Handlers.Commands.MusicCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import com.iog.MusicPlayer.PlayerManager;
import com.iog.Utils.CommandExecutionException;
import com.iog.Utils.Format;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Message;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;

public class Play extends MusicCommand {

    public Play() {
        super(
                new String[]{"play", "p"},
                "Play track"
        );
    }

    @Override
    public void run(Message message, String[] args) throws CommandExecutionException {
        PlayerManager playerManager = PlayerManager.getInstance();

        final GuildAudioManager manager = GuildAudioManager.of(message.getGuildId().orElseThrow());
        final AudioProvider provider = manager.getProvider();

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

            if (args.length == 0) {
                message.getChannel().subscribe(channel -> channel.createMessage("No arguments given to search anything").subscribe());
                return;
            }

            voiceState.getChannel().subscribe(voiceChannel -> voiceChannel.join(spec -> spec.setProvider(provider)).subscribe(voiceConnection -> {
                String query;

                if (Format.isUrl(args[0].trim())) {
                    query = args[0].trim();
                } else {
                    query = "ytsearch:"+String.join(" ", args);
                }

                playerManager.loadAndPlay(message, query);
            }));
        });
    }
}
