import { CommandInteraction, Message, MessageReaction, SlashCommandBuilder } from 'discord.js'
import GuildSettings from '../../Guilds/GuildSettings'
import { distube } from '../../Utils/AudioManager'
import Command from '../../Models/Command'

export default class Remove extends Command {
	constructor() {
		super(
			new SlashCommandBuilder()
				.setName('remove')
				.setDescription('Remove track from queue')
				.addIntegerOption((option) => option.setName('queue-index').setDescription('Track index from queue').setRequired(true))
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

		let deleteIndex = interaction.options.get('queue-index')?.value as number
        if (!deleteIndex || (deleteIndex < 0 || deleteIndex >= queue.songs.length)) {
            interaction.editReply(`Not a valid index in queue (${deleteIndex})`)
			return
        }

        deleteIndex -= 1
		const track = queue.songs[deleteIndex]

		interaction.editReply(`Removed ${track.name} from queue!`)
		queue.songs.splice(deleteIndex, 1)
	}
}
