import SpotifyPlugin from '@distube/spotify'
import { Client, EmbedBuilder, HexColorString } from 'discord.js'
import DisTube from 'distube'
import { logger } from '../index'
import Config from '../Config'
import GuildSettings from '../Guilds/GuildSettings'
import { YtDlpPlugin } from '@distube/yt-dlp'

const spotify = Config.getConfig().spotify

export var distube: DisTube = null

export function initDisTube(client: Client) {
	distube = new DisTube(client, {

		searchSongs: 1,
		plugins: [
            new YtDlpPlugin({ update: false }),
			new SpotifyPlugin({
				api: {
					clientId: spotify.clientId,
					clientSecret: spotify.clientSecret
				},
				emitEventsAfterFetching: true
			})
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

	distube.on('addList', (queue, playlist) => {
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

		embed.setColor(Config.getConfig().defaultColors.success as HexColorString)
		embed.setThumbnail(song.thumbnail)

		queue.textChannel.send({ embeds: [embed] })
	})

	distube.on('addSong', (queue, song) => {
		const embed = new EmbedBuilder()

		embed.setAuthor({ name: queue.songs.length == 1 ? 'Playing' : 'Added to queue', iconURL: song.user.avatarURL(), url: song.url })
		embed.addFields([
			{ name: 'Title', value: song.name, inline: false },
			{ name: 'Duration', value: song.formattedDuration, inline: false }
		])
		embed.setColor(Config.getConfig().defaultColors.success as HexColorString)
		embed.setThumbnail(song.thumbnail)

		queue.textChannel.send({ embeds: [embed] })
	})
}
