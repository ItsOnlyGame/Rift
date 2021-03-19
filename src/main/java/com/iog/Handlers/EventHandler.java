package com.iog.Handlers;

import com.iog.Main;
import com.iog.MusicPlayer.GuildAudioManager;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.object.VoiceState;

import java.util.List;

public class EventHandler {

    public static void VoiceStateUpdate(VoiceStateUpdateEvent event) {
        if (event.isLeaveEvent()) {
            if (event.getOld().isPresent()) {
                if (event.getOld().get().getUserId().equals(Main.gateway.getSelfId())) {
                    VoiceState oldVoiceState = event.getOld().get();
                    GuildAudioManager musicManager = GuildAudioManager.of(oldVoiceState.getGuildId());
                    musicManager.getScheduler().getQueue().clear();
                    musicManager.getPlayer().stopTrack();

                    oldVoiceState.getChannel().subscribe(channel -> channel.edit(e -> {
                        if (channel.getUserLimit() == 0) {
                            return;
                        }
                        e.setUserLimit(channel.getUserLimit() - 1);
                    }).subscribe());
                } else {
                    event.getOld().orElseThrow().getChannel().subscribe(voiceChannel -> {
                        List<VoiceState> voiceStates = voiceChannel.getVoiceStates().collectList().block();
                        if (voiceStates == null) return;
                        if (voiceStates.size() == 1) {
                            if (voiceStates.get(0).getUserId().equals(Main.gateway.getSelfId())) {
                                Main.gateway.getVoiceConnectionRegistry().getVoiceConnection(voiceChannel.getGuildId()).subscribe(voice -> voice.disconnect().subscribe());
                            }
                        }
                    });
                }
            }

        } else if (event.isJoinEvent()) {
            if (event.getCurrent().getUserId().equals(Main.gateway.getSelfId())) {
                event.getCurrent().getChannel().subscribe(channel -> channel.edit(e -> {
                    if (channel.getUserLimit() == 0) {
                        return;
                    }
                    e.setUserLimit(channel.getUserLimit() + 1);
                }).subscribe());
            }
        }
    }


    public static void MemberJoin(MemberJoinEvent event) {
        GuildSettings settings = GuildSettings.of(event.getGuildId().asLong());
        if (settings.getDj() != 0L) {
            event.getMember().addRole(Snowflake.of(settings.getDj())).subscribe();
        }
    }
}
