import SpotifyPlugin from '@distube/spotify'
import { Client, EmbedBuilder, HexColorString } from 'discord.js'
import DisTube from 'distube'
import { logger } from '../index'
import { getConfig } from '../Config'
import GuildSettings from '../Guilds/GuildSettings'
import TrackMetadata from 'src/Models/TrackMetadata'
import { YtDlpPlugin } from '@distube/yt-dlp'

const spotify = getConfig().spotify

export var distube: DisTube = null

export function initDisTube(client: Client) {
	distube = new DisTube(client, {
		searchSongs: 1,
		plugins: [
			new SpotifyPlugin({
				api: {
					clientId: spotify.clientId,
					clientSecret: spotify.clientSecret
				},
				emitEventsAfterFetching: true
			}),
            new YtDlpPlugin()
		],
		nsfw: true,
	})

	distube.on('error', (channel, error) => {
		logger.error(error)
		channel.send(`An error encoutered: ${error.message.slice(0, 1979)}`)
	})

	distube.on('playSong', (queue, song) => {
		queue.setVolume(GuildSettings.getGuildSettings(queue.voiceChannel.guildId, queue.client).volume)
	})

	distube.on('addList', async (queue, playlist) => {
        const interaction = (playlist.metadata as TrackMetadata).interaction

		const embed = new EmbedBuilder()
		const song = playlist.songs[0]

		embed.setAuthor({ name: queue.songs.length == 1 ? 'Playing' : 'Added to queue', iconURL: song.user.avatarURL(), url: song.playlist.url })

		embed.addFields([
			{ name: 'Playlist Name', value: playlist.name, inline: true },
			{ name: 'Playlist Length', value: String(playlist.songs.length), inline: true },
			{ name: 'Playlist Duration', value: playlist.formattedDuration, inline: false },

			{ name: 'First track', value: song.name, inline: false },
			{ name: 'Track duration', value: song.formattedDuration, inline: false }
		])

		embed.setColor(getConfig().defaultColors.success as HexColorString)
		embed.setThumbnail(song.thumbnail)

        interaction.fetchReply().then(message => {
            const oldEmbeds = message.embeds.filter(item => item.data.type == 'rich')
            interaction.editReply({ embeds: [...oldEmbeds, embed], content: '' })    
        })
	})

	distube.on('addSong', async (queue, song) => {
        const interaction = (song.metadata as TrackMetadata).interaction
		const embed = new EmbedBuilder()

		embed.setAuthor({ name: queue.songs.length == 1 ? 'Playing' : 'Added to queue', iconURL: song.user.avatarURL(), url: song.url })
		embed.addFields([
			{ name: 'Title', value: song.name, inline: false },
			{ name: 'Duration', value: song.formattedDuration, inline: false }
		])
		embed.setColor(getConfig().defaultColors.success as HexColorString)
		embed.setThumbnail(song.thumbnail)
        
        interaction.fetchReply().then(message => {
            const oldEmbeds = message.embeds.filter(item => item.data.type == 'rich')
            interaction.editReply({ embeds: [...oldEmbeds, embed], content: '' })    
        })	
    })
}
