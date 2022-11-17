package com.iog.MusicPlayer;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Message;

public class TrackUserData {
	
	private final Message message;
	private final ChatInputInteractionEvent interaction;
	private final boolean isInteraction;
	
	
	public TrackUserData(Message message) {
		this.message = message;
		this.interaction = null;
		this.isInteraction = false;
	}
	
	public TrackUserData(ChatInputInteractionEvent interaction) {
		this.message = null;
		this.interaction = interaction;
		this.isInteraction = false;
	}
	
	public Message getMessage() {
		return message;
	}
	
	public ChatInputInteractionEvent getInteraction() {
		return interaction;
	}
	
	public boolean isInteraction() {
		return isInteraction;
	}
}
