import { CommandInteraction, Message, SlashCommandBuilder } from 'discord.js'
import GuildSettings from '../../Guilds/GuildSettings'
import { distube } from '../../Utils/AudioManager'
import Command from '../../Models/Command'

export default class Stop extends Command {
	constructor() {
		super(new SlashCommandBuilder().setName('stop').setDescription('Stop playback'))
	}

	public async execute(interaction: CommandInteraction): Promise<void> {
		const guildSettings = GuildSettings.getGuildSettings(interaction.guildId, interaction.client)

		if (guildSettings.dj_role != null) {
            const roles = interaction.member.roles as string[]
            
			if (roles.includes(guildSettings.dj_role) == undefined) {
                interaction.editReply('You are not a dj')
				return
			}
		}

		const queue = distube.getQueue(interaction.guildId)

		if (!queue) {
			interaction.editReply('Queue is empty!')
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

		queue.stop()

		interaction.editReply('Queue cleared and playback stopped!')
	}
}
