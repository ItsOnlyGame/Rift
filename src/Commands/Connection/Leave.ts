import { getVoiceConnection } from '@discordjs/voice'
import { CommandInteraction, Message, SlashCommandBuilder, TextChannel } from 'discord.js'
import { distube } from '../../Utils/AudioManager'
import Command from '../../Models/Command'

export default class Leave extends Command {
	constructor() {
		super(new SlashCommandBuilder().setName('leave').setDescription('Leave the voice channel'))
	}

	public async execute(interaction: CommandInteraction): Promise<void> {
        const member = interaction.guild.members.cache.find((user) => user.id == interaction.member.user.id)
		const memberVoiceConnection = member.voice

		const existingConnection = getVoiceConnection((interaction.channel as TextChannel).guildId)

		if (existingConnection != null) {
			if (!memberVoiceConnection) {
				interaction.editReply('You have to be in the same voice channel as I to do that!')
				return
			}

			if (existingConnection.joinConfig.channelId != memberVoiceConnection.channel.id) {
				interaction.editReply('You have to be in the same voice channel as I to do that!')
				return
			}

			distube.voices.leave(interaction.guildId)
			interaction.editReply(`Leaving voice channel`)
			return
		}

		interaction.editReply('Not connected to a voice channel')
	}
}
