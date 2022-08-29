import { CommandInteraction, Message, PermissionsBitField, SlashCommandBuilder } from 'discord.js'
import GuildSettings from '../../Guilds/GuildSettings'
import { distube } from '../../Utils/AudioManager'
import Command from '../../Models/Command'

export default class Volume extends Command {
	constructor() {
		super(
			new SlashCommandBuilder()
				.setName('volume')
				.setDescription('Change volume')
				.addIntegerOption((option) => option.setName('new-volume').setDescription('New volume').setRequired(false))
		)
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
		const newvolume = interaction.options.get('new-volume')?.value as number
		if (!newvolume) {
			interaction.editReply(`Volume is \`\`${guildSettings.volume}%\`\``)
			return
		}

		if (!(interaction.member.permissions as Readonly<PermissionsBitField>).has(PermissionsBitField.Flags.Administrator)) {
			if (!queue) {
				interaction.editReply('You need to be in the same voice channel as I')
				return
			}

			const member = interaction.guild.members.cache.find((user) => user.id == interaction.member.user.id)
			if (queue.voiceChannel.id != member.voice.channel.id) {
				interaction.editReply('You need to be in the same voice channel as I')
				return
			}
		}

		if (queue) queue.setVolume(newvolume)
		guildSettings.volume = newvolume
		GuildSettings.saveGuildSettings(guildSettings)

		interaction.editReply(`Volume set to \`\`${newvolume}%\`\``)
	}
}
