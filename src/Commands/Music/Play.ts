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

		let input = (interaction.options.get('link-or-query')?.value as string).split(' ')
		let queries = []
		if (input.length > 1) {
			if (!this.validURL(input[0])) {
				queries.push(input.join(' '))
			} else {
				input.forEach((value) => queries.push(value))
			}
		} else {
            queries.push(input[0])
        }

		if (!queries) {
			interaction.editReply('You need to give me a URL or a search term.')
			return
		}

		logger.info(`Searching with query: ${queries.join(', ')}`)
		interaction.editReply(`Searching for '${queries.join(', ')}'`)
        
		// If there is only one query or url then add it to the queue
		if (queries.length == 1) {
			distube
				.play(member.voice.channel, queries[0], { metadata: { interaction }, textChannel: interaction.channel as TextChannel, member })
				.catch((error) => {
					interaction.editReply('Something went wrong!')
					logger.error(error)
				})
			return
		}

		for (let query of queries) {
            try {
                await distube.play(member.voice.channel, query, { metadata: { interaction }, textChannel: interaction.channel as TextChannel, member })
            } catch (error) {
                interaction.editReply('Something went wrong!')
                logger.error(error)
            }
		}
        
	}

	validURL(str: string) {
		var pattern = new RegExp(
			'^(https?:\\/\\/)?' + // protocol
				'((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|' + // domain name
				'((\\d{1,3}\\.){3}\\d{1,3}))' + // OR ip (v4) address
				'(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*' + // port and path
				'(\\?[;&a-z\\d%_.~+=-]*)?' + // query string
				'(\\#[-a-z\\d_]*)?$',
			'i'
		) // fragment locator
		return !!pattern.test(str)
	}
}
