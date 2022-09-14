import { CommandInteraction, Message, SlashCommandBuilder } from 'discord.js'
import lyricsFinder from 'lyrics-finder'
import { distube } from '../../Utils/AudioManager'
import Command from '../../Models/Command'

export default class Lyrics extends Command {
	constructor() {
		super(
			new SlashCommandBuilder()
				.setName('lyrics')
				.setDescription('Fetches track lyrics from google')
				.addStringOption((option) =>
					option.setName('track-name').setDescription('Whether the player should loop the current track').setRequired(false)
				)
		)
	}

	public async execute(interaction: CommandInteraction): Promise<void> {
		const queue = distube.getQueue(interaction.guildId)

        let trackName = interaction.options.get('track-name')?.value
        if (trackName) {
			const lyrics: string = (await lyricsFinder(trackName)) || 'No lyrics found!'
            interaction.editReply( '```' + 'Lyrics for ' + trackName + '```')

			var splits = this.splitLyrics(lyrics)
			for (var i = 0; i < splits.length; i++) {
				var text = '```' + splits[i] + '```\n'
				interaction.channel.send(text)
			}

            return
        }

		if (!queue || queue.songs.length == 0) {
			interaction.editReply('Nothing is playing!')
			return
		}

		trackName = `${queue.songs[0].name}`
		const lyrics: string = (await lyricsFinder(trackName)) || 'No lyrics found!'

        interaction.editReply( '```' + 'Lyrics for ' + trackName + '```')

		var splits = this.splitLyrics(lyrics)
		for (var i = 0; i < splits.length; i++) {
			var text = '```' + splits[i] + '```\n'
			interaction.channel.send(text)
		}
	}

	splitLyrics(lyrics: string) {
		var partitionSize = 1500
		const parts = []

		var array = lyrics.split('\n\n')
		if (array.length == 1) array = lyrics.split('\n')

		var temp = ''
		var len = 0

		for (var str of array) {
			len += str.length + 4

			if (len >= partitionSize) {
				parts.push(temp)
				temp = ''
				temp += str
				len = 0
			} else {
				temp += `${str}\n\n`
			}
		}
		if (len != 0) {
			parts.push(temp)
		}

		return parts
	}
}
