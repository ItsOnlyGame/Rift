import { CommandInteraction, SlashCommandBuilder } from 'discord.js'
import Command from '../../Models/Command'

export default class BotInfo extends Command {
	constructor() {
		super(
			new SlashCommandBuilder()
				.setName('delete')
				.setDescription('Delete message(s)')
				.addNumberOption((option) => option.setName('amount').setDescription('Amount of messages to delete').setRequired(false))
		)
	}

	public async execute(interaction: CommandInteraction): Promise<void> {
		let deleteAmount = interaction.options.get('amount')?.value as number
		if (!deleteAmount) {
			deleteAmount = 1
		}

		interaction.deleteReply()

		const times = Math.floor(deleteAmount / 100)
		for (var i = 0; i < times; i++) {
			interaction.channel.bulkDelete(100)
		}
		deleteAmount -= times * 100
		interaction.channel.bulkDelete(deleteAmount)
	}
}
