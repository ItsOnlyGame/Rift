import { CommandInteraction, Message, SlashCommandBuilder } from 'discord.js'
import { RepeatMode } from 'distube'
import { distube } from '../../Utils/AudioManager'
import Command from '../../Models/Command'

export default class Loop extends Command {
	constructor() {
		super(
			new SlashCommandBuilder()
				.setName('loop')
				.setDescription('Loop current track')
				.addBooleanOption((option) =>
					option.setName('repeat-mode').setDescription('Whether the player should loop the current track').setRequired(true)
				)
		)
	}
	
	public async execute(interaction: CommandInteraction): Promise<void> {
		const queue = distube.getQueue(interaction.guildId)
		const loop = interaction.options.get('repeat-mode').value as boolean

		if (loop) {
			queue.setRepeatMode(RepeatMode.SONG)
			interaction.editReply('Enabled looping')
			return
		}

		queue.setRepeatMode(RepeatMode.DISABLED)
		interaction.editReply('Disabled looping')
	}
}
