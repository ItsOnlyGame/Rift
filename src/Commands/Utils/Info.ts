import { CommandInteraction, Message, SlashCommandBuilder } from 'discord.js'
import Command from '../../Models/Command'

export default class Info extends Command {
	constructor() {
		super(
			new SlashCommandBuilder()
				.setName('info')
				.setDescription('Information about some the bot or yourself')
				.addSubcommand((subcommand) => subcommand.setName('bot').setDescription('Information about the bot'))
		)
	}

	public async execute(interaction: CommandInteraction): Promise<void> {
		const str =
			'```' +
			'Rift: ' +
			'\n' +
			'Version: ' +
			process.env.npm_package_version +
			'\n' +
			'ID: ' +
			interaction.client.user.id +
			'\n' +
			'Github: ' +
			'https://github.com/ItsOnlyGame/Rift' +
			'\n\n' +
			'You can report you issues to github\n```'

		interaction.editReply(str)
	}
}
