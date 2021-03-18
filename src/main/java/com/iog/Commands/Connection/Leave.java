package com.iog.Commands.Connection;

import com.iog.Handlers.Commands.BasicCommand;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import discord4j.voice.VoiceConnection;

public class Leave extends BasicCommand {


    public Leave() {
        super(
                new String[]{"leave", "disconnect", "dc", "dis"},
                "Leaves voice channel"
        );
    }

    @Override
    public void run(Message message, String[] args) {
        message.getAuthorAsMember().subscribe(member -> {
            VoiceState voiceState = member.getVoiceState().block();

            if (voiceState == null) {
                message.getChannel().subscribe(channel -> channel.createMessage("You have to be in a voice channel").subscribe());
                return;
            }

            VoiceConnection botVoiceConnection = message.getClient().getVoiceConnectionRegistry().getVoiceConnection(message.getGuildId().orElseThrow()).block();
            if (botVoiceConnection != null) {
                if (!voiceState.getChannelId().orElseThrow().equals(botVoiceConnection.getChannelId().blockOptional().orElseThrow())) {
                    if (!hasAdminPermissions(message.getAuthorAsMember().blockOptional().orElseThrow())) {
                        message.getChannel().subscribe(channel -> channel.createMessage("You have to be in the same voice channel as I").subscribe());
                        return;
                    }
                }
                 message.getGuild().subscribe(guild -> guild.getSelfMember().subscribe(self -> self.getVoiceState().subscribe(selfVoiceState -> {
                    VoiceChannel voiceChannel = selfVoiceState.getChannel().block();
                    assert voiceChannel != null;
                    botVoiceConnection.disconnect().subscribe(ignored ->
                            message.getChannel().subscribe(channel -> channel.createMessage("Disconnected from " + voiceChannel.getName()).subscribe()));
                 })));

            } else {
                message.getChannel().subscribe(channel -> channel.createMessage("I'm not in a voice channel").subscribe());
            }
        });
    }


    public boolean hasAdminPermissions(Member member) {
        PermissionSet set = member.getBasePermissions().blockOptional().orElseThrow();
        return set.contains(Permission.ADMINISTRATOR) || set.contains(Permission.MOVE_MEMBERS);
    }
}
