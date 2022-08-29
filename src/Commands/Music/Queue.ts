import { CommandInteraction, Message, SlashCommandBuilder } from 'discord.js'
import { distube } from '../../Utils/AudioManager'
import Command from '../../Models/Command'

export default class Queue extends Command {
	constructor() {
		super(new SlashCommandBuilder().setName('queue').setDescription('Get a list of the queue'))
	}

	public async execute(interaction: CommandInteraction): Promise<void> {
		const queue = distube.getQueue(interaction.guildId)

		if (queue == undefined) {
			interaction.editReply('Queue is empty!')
			return
		}

		if (queue.songs.length == 0) {
			interaction.editReply('Queue is empty!')
			return
		}

		const cap = 25
        var queueMessage = '```' + `Now playing: ${queue.songs[0].name}\n`

        for (var i = 1; i < cap; i++) {
            if (queue.songs.length <= i) break
            var track = queue.songs[i]
            queueMessage += `${i + 1}: ${track.name}\n`
        }
        queueMessage += '```'
        
        interaction.editReply(queueMessage)
	}
}
