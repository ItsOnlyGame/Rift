package com.iog.Commands.Connection;

import com.iog.Handlers.Commands.BasicCommand;
import com.iog.MusicPlayer.GuildAudioManager;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;

public class Join extends BasicCommand {

    public Join() {
        super(
                new String[]{"join", "connect", "c", "j"},
                "Join a voice channel"
        );
    }

    @Override
    public void run(Message message, String[] args) {
        final GuildAudioManager manager = GuildAudioManager.of(message.getGuildId().orElseThrow());
        final AudioProvider provider = manager.getProvider();

        message.getAuthorAsMember().subscribe(member -> {
            VoiceState voiceState = member.getVoiceState().block();

            if (voiceState == null) {
                message.getChannel().subscribe(channel -> channel.createMessage("You have to be in a voice channel").subscribe());
                return;
            }

            VoiceConnection botVoiceConnection = message.getClient().getVoiceConnectionRegistry().getVoiceConnection(message.getGuildId().orElseThrow()).block();
            if (botVoiceConnection != null) {
                if (!voiceState.getChannelId().orElseThrow().equals(botVoiceConnection.getChannelId().blockOptional().orElseThrow())) {
                    message.getChannel().subscribe(channel -> channel.createMessage("I'm already in another voice channel").subscribe());
                } else {
                    message.getChannel().subscribe(channel -> channel.createMessage("We are already in the same voice channel").subscribe());
                }
                return;
            }

            voiceState.getChannel().subscribe(voiceChannel -> voiceChannel.join(spec -> spec.setProvider(provider))
                        .subscribe(ignored -> message.getChannel()
                                .subscribe(channel -> channel.createMessage("Joined " + voiceChannel.getName()).subscribe())));
        });
    }


    public boolean hasAdminPermissions(Member member) {
        PermissionSet set = member.getBasePermissions().blockOptional().orElseThrow();
        return set.contains(Permission.ADMINISTRATOR) || set.contains(Permission.MOVE_MEMBERS);
    }
}
