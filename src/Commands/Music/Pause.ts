import { CommandInteraction, Message, SlashCommandBuilder } from 'discord.js'
import { distube } from '../../Utils/AudioManager'
import Command from '../../Models/Command'
import GuildSettings from '../../Guilds/GuildSettings'

export default class Pause extends Command {
	constructor() {
		super(new SlashCommandBuilder().setName('pause').setDescription('Pause current track'))
	}

	public async execute(interaction: CommandInteraction): Promise<void> {
		const queue = distube.getQueue(interaction.guildId)
		const guildSettings = GuildSettings.getGuildSettings(interaction.guildId, interaction.client)

		if (guildSettings.dj_role != null) {
            const roles = interaction.member.roles as string[]
            
			if (roles.includes(guildSettings.dj_role) == undefined) {
                interaction.editReply('You are not a dj')
				return
			}
		}

		if (!queue) {
			interaction.editReply('Nothing is currenly playing')
			return
		}

		if (queue.songs.length == 0) {
			interaction.editReply('Queue is empty!')
			return
		}

        const member = interaction.guild.members.cache.find(user => user.id == interaction.member.user.id)
		if (queue.voiceChannel.id != member.voice.channel.id) {
			interaction.editReply('You need to be in the same voice channel as I')
			return
		}

		if (queue.paused) {
			queue.resume()
			interaction.editReply('Resuming track')
		} else {
			queue.pause()
			interaction.editReply('Pausing track')
		}
	}
}
