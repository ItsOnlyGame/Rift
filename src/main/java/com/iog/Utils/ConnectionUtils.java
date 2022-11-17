package com.iog.Utils;

import discord4j.common.util.Snowflake;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.voice.VoiceConnection;

public class ConnectionUtils {
	
	public static boolean botIsInSameVoiceChannel(Member member, Snowflake guildId) {
		VoiceState voiceState = member.getVoiceState().block();
		
		if (voiceState == null) {
			return false;
		}
		
		VoiceConnection botVoiceConnection = member.getClient().getVoiceConnectionRegistry().getVoiceConnection(guildId).block();
		if (botVoiceConnection != null) {
			return voiceState.getChannelId().orElseThrow().equals(botVoiceConnection.getChannelId().blockOptional().orElseThrow());
		}
		
		return true;
	}
	
}
