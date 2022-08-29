import { getVoiceConnection } from '@discordjs/voice'
import { CommandInteraction, Message, SlashCommandBuilder, TextChannel } from 'discord.js'
import { distube } from '../../Utils/AudioManager'
import Command from '../../Models/Command'

export default class Join extends Command {
	constructor() {
		super(new SlashCommandBuilder().setName('join').setDescription('Join the voice channel'))
	}

	public async execute(interaction: CommandInteraction): Promise<void> {
        const member = interaction.guild.members.cache.find((user) => user.id == interaction.member.user.id)
		const memberVoiceConnection = member.voice

		if (!memberVoiceConnection) {
			interaction.editReply('You have to be in a voice channel!')
			return
		}

		if (!memberVoiceConnection.channel) {
			interaction.editReply('You have to be in a voice channel!')
			return
		}

		var connection = getVoiceConnection((interaction.channel as TextChannel).guildId)

		if (connection != null) {
			if (connection.joinConfig.channelId != memberVoiceConnection.channel.id) {
				interaction.editReply('Currently connected to another voice channel!')
				return
			}
			return null
		}

		distube.voices.join(memberVoiceConnection.channel).then(() => {
			interaction.editReply(`Joined ${memberVoiceConnection.channel.name}!`)
		})
	}
}
