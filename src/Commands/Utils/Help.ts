import { HexColorString, EmbedBuilder, SlashCommandBuilder, CommandInteraction } from 'discord.js'
import { getConfig } from '../../Config'
import Command from '../../Models/Command'

export default class Help extends Command {
	constructor() {
		super(new SlashCommandBuilder().setName('help').setDescription('Gives help about this bots functions'))
	}

	public async execute(interaction: CommandInteraction): Promise<void> {
		const embed = new EmbedBuilder()
			.setAuthor({ name: 'Help' })
			.setColor(getConfig().defaultColors.success as HexColorString)
			.setDescription(`Every command is listed under this link: \nhttps://github.com/ItsOnlyGame/Rift#commands`)

		interaction.editReply({ embeds: [embed] })
	}
}
