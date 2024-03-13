package com.iog.Utils;

import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.GuildVoiceState;

public class ConnectionUtils {

    public static boolean botIsInSameVoiceChannel(Member member, Guild guild) {
        GuildVoiceState voiceState = member.getVoiceState();

        if (voiceState == null) {
            return false;
        }
        var connectedChannel = guild.getAudioManager().getConnectedChannel();
        if (connectedChannel == null) {
            // Bot can be considered to be in the same voice channel if it isn't in any.
            return true;
        }

        return connectedChannel.asVoiceChannel().getIdLong() == voiceState.getChannel().getIdLong();
    }

}
