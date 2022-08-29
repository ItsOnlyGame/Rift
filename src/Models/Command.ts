import { CacheType, ChatInputCommandInteraction, Message, MessageContextMenuCommandInteraction, SlashCommandBuilder, SlashCommandSubcommandsOnlyBuilder, UserContextMenuCommandInteraction } from 'discord.js'

export default abstract class Command {

	public data: SlashCommandBuilder | SlashCommandSubcommandsOnlyBuilder | Omit<SlashCommandBuilder, "addSubcommand" | "addSubcommandGroup">

	constructor(data: SlashCommandBuilder | SlashCommandSubcommandsOnlyBuilder | Omit<SlashCommandBuilder, "addSubcommand" | "addSubcommandGroup">) {
        this.data = data
	}

	/**
	 * Executes the command
	 * @param interaction Message Context Object
	 */
	public abstract execute(
		interaction:
			| ChatInputCommandInteraction<CacheType>
			| MessageContextMenuCommandInteraction<CacheType>
			| UserContextMenuCommandInteraction<CacheType>
	): Promise<void>
}
