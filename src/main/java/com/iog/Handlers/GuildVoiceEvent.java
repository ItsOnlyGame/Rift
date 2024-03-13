package com.iog.Handlers;

import com.iog.MusicPlayer.GuildAudioManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildVoiceEvent extends ListenerAdapter {

	@Override
	public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
		Member selfMember = event.getGuild().getSelfMember();

		if (event.getMember().equals(selfMember)) {
			if (event.getChannelLeft() != null && event.getChannelJoined() == null) {
				Guild guild = event.getGuild();
				GuildAudioManager.remove(guild);
			}
		}

		if (event.getChannelLeft() != null) {
			var voiceChannel = event.getChannelLeft();

			if (event.getMember().equals(selfMember)) {
				int memberCap = voiceChannel.getUserLimit();
				if (memberCap > 0) {
					voiceChannel.getManager().setUserLimit(memberCap - 1).queue();
				}
				return;
			}

			if (voiceChannel.getMembers().size() == 1 && voiceChannel.getMembers().contains(selfMember)) {
				voiceChannel.getGuild().getAudioManager().closeAudioConnection();
			}
		}

		if (event.getChannelJoined() != null) {
			var voiceChannel = event.getChannelJoined();

			if (event.getMember().equals(selfMember)) {
				int memberCap = voiceChannel.getUserLimit();
				if (memberCap > 0) {
					voiceChannel.getManager().setUserLimit(memberCap + 1).queue();
				}
			}
		}
	}

}
