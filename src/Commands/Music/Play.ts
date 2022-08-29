import Command from '../../Models/Command'
import { logger } from '../../index'
import { CommandInteraction, Message, SlashCommandBuilder, TextChannel } from 'discord.js'
import GuildSettings from '../../Guilds/GuildSettings'
import { distube } from '../../Utils/AudioManager'

export default class Play extends Command {
	constructor() {
		super(
			new SlashCommandBuilder()
				.setName('play')
				.setDescription('Play track(s)')
				.addStringOption((option) => option.setName('link-or-query').setDescription('Link or search query to play a track').setRequired(true))
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

		const member = interaction.guild.members.cache.find((user) => user.id == interaction.member.user.id)

		if (!member.voice.channel) {
			interaction.editReply('you need to join a voice channel.')
			return
		}

		const query = interaction.options.get('link-or-query')?.value as string

		if (!query) {
			interaction.editReply('you need to give me a URL or a search term.')
			return
		}

		logger.info(`Searching with query: ${query}`)
        
        interaction.editReply(`Searching for '${query}'`)
		distube
			.play(member.voice.channel, query, { metadata: { interaction }, textChannel: interaction.channel as TextChannel, member })
			.then((data) => console.log(data))
            .catch(error => interaction.editReply('Something went wrong!'))

	}
}
