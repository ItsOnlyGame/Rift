package com.iog.Commands;

import com.iog.Utils.CommandExecutionException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.tinylog.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SlashCommandListener extends ListenerAdapter {
	public List<BaseCommand> commands = new ArrayList<>();

	public SlashCommandListener() {
		Reflections reflections = new Reflections("com.iog.Commands");
		Set<Class<? extends BaseCommand>> classes = reflections.getSubTypesOf(BaseCommand.class);
		
		for (Class<?> c : classes) {
			try {
				Constructor<?> constructor = c.getConstructor();
				BaseCommand command = (BaseCommand) constructor.newInstance();
				
				commands.add(command);
			} catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
			         InvocationTargetException e) {
				Logger.error("Something went wrong while initializing commands", e);
			}
		}
	}

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Logger.info(event.getJDA().getSelfUser().getName() + " is ready!");

//        event.getJDA().getGuildById("470663939097886720").updateCommands()
//                .addCommands(commands.stream().map(BaseCommand::getSlashCommandData).toArray(SlashCommandData[]::new))
//                .queue();

		event.getJDA().updateCommands()
				.addCommands(commands.stream().map(BaseCommand::getSlashCommandData).toArray(SlashCommandData[]::new))
				.queue();

    }

	public BaseCommand getCommand(String name) {
		for (BaseCommand command : commands) {
			if (command.getName().equals(name)) {
				return command;
			}
		}
		return null;
	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) throws CommandExecutionException {
		Guild guild = event.getGuild();
		Member member  = event.getMember();

		if (guild == null || member == null) {
			return;
		}

		BaseCommand command = getCommand(event.getFullCommandName());
		if (command == null) return;

		try {
			command.run(event);
		} catch (Exception e) {
			Logger.error(e, e.getMessage());
		}
	}
}
