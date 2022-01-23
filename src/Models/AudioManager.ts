import SpotifyPlugin from "@distube/spotify";
import { Client, HexColorString, MessageEmbed } from "discord.js";
import DisTube from "distube";
import { logger } from '../index';
import Config from "../Config";
import GuildSettings from "../Guilds/GuildSettings";

const spotify = Config.getConfig().spotify

export var distube: DisTube = null;

export function initDisTube(client: Client) {
    distube = new DisTube(client,{
        searchSongs: 1,
        plugins: [
            new SpotifyPlugin({api: {
                clientId: spotify.clientId,
                clientSecret: spotify.clientSecret
            }})
        ],
        nsfw: true
    });

    distube.on('error', (channel, error) => {
        logger.error(error);
        channel.send(`An error encoutered: ${error.message.slice(0, 1979)}`)
    })

    distube.on('playSong', (queue, song) => {
        queue.setVolume(GuildSettings.getGuildSettings(queue.voiceChannel.guildId, queue.client).volume)
    })

    distube.on('addSong', (queue, song) => {
        const embed = new MessageEmbed();
        
        if (song.playlist) { // First playing song
            embed.setAuthor({ name: (queue.songs.length == 1 ? 'Playing' : 'Added to queue'), iconURL: song.user.avatarURL(), url: song.playlist.url })

            embed.addField('Playlist Name', song.playlist.name, true)
            embed.addField('Playlist Length', String(song.playlist.songs.length), true)
            embed.addField('Playlist Duration', song.playlist.formattedDuration, false)

            embed.addField('First track', song.name, false)
            embed.addField('Track duration', song.formattedDuration, false)

            embed.setColor(Config.getConfig().defaultColors.success as HexColorString)
            embed.setThumbnail(song.thumbnail)
        } else {
            embed.setAuthor({ name: (queue.songs.length == 1 ? 'Playing' : 'Added to queue'), iconURL: song.user.avatarURL(), url: song.url })
            embed.addField('Title', song.name, false)
            embed.addField('Duration', song.formattedDuration, false)
            embed.setColor(Config.getConfig().defaultColors.success as HexColorString)
            embed.setThumbnail(song.thumbnail)
        }
        queue.textChannel.send({embeds: [embed]});
    })
}