import { CommandInteraction, EmbedBuilder, HexColorString, Message, SlashCommandBuilder, TextChannel } from 'discord.js'
import Command from '../../Models/Command'
import { distube } from '../../Utils/AudioManager'
import { getConfig } from '../../Config'

export default class TrackInfo extends Command {
	constructor() {
		super(new SlashCommandBuilder().setName('track').setDescription('Get the info of the track thats currently playing'))
	}

	public async execute(interaction: CommandInteraction): Promise<void> {
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

		const track = queue.songs[0]

		const embed = new EmbedBuilder()
			.setColor(getConfig().defaultColors.success as HexColorString)
			.setAuthor({ name: 'Info' })
			.addFields([
				{ name: 'Title', value: track.name, inline: false },
				{ name: 'Duration', value: `${track.duration}`, inline: false },
				{ name: 'Url', value: track.url, inline: false }
			])

		if (track.thumbnail) embed.setThumbnail(track.thumbnail)

		interaction.editReply({ embeds: [embed] })
	}
}
