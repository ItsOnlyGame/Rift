package com.iog.Commands.Settings;

import com.iog.Commands.BaseCommand;
import com.iog.Handlers.GuildSettings;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class Prefix extends BaseCommand {
	
	public Prefix() {
		super(new String[]{"prefix"}, null);
	}
	
	@Override
	public void run(Message message, String[] args) {

		
		GuildSettings settings = GuildSettings.of(message.getGuildId().orElseThrow().asLong());
		if (args.length == 0) {
			message.getChannel().subscribe(channel -> channel.createMessage("Server prefix is " + settings.getPrefix()).subscribe());
			return;
		}
		
		if (!hasAdminPermissions(message.getAuthorAsMember().blockOptional().orElseThrow())) {
			message.getChannel().subscribe(channel -> channel.createMessage("You don't have permissions to change the prefix").subscribe());
			return;
		}
		
		String prefix = args[0].trim();
		message.getChannel().subscribe(channel -> channel.createMessage("New server prefix is " + prefix).subscribe());
		settings.setPrefix(prefix).save();
	}
	
	@Override
	public void run(ChatInputInteractionEvent interaction) {
		return;
	}
	
	private boolean hasAdminPermissions(Member member) {
		PermissionSet set = member.getBasePermissions().blockOptional().orElseThrow();
		return set.contains(Permission.ADMINISTRATOR);
	}
}
