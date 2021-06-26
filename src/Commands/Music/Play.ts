import { Message, MessageEmbed } from "discord.js";
import { ErelaManager } from "../../Models/LavaplayerManager";
import { SearchResult } from "erela.js";
import GuildSettings from "../../Guilds/GuildSettings";
import getConfig from "../../Config";
const defaultColors = getConfig().defaultColors

var moment = require("moment");
var momentDurationFormatSetup = require("moment-duration-format");
momentDurationFormatSetup(moment);

export default {
    name: "Play",
    description: "Play tracks",
    aliases: ["play", "p"],
    permissionReq: null,

    /**
     * Executes the command
     * @param {Message} message 
     * @param {Array<string>} args 
     */
    execute: async function(message: Message, args: Array<string>) {
        const guildSettings = GuildSettings.getGuildSettings(message.guild.id, message.client)

        if (guildSettings.dj_role != null) {
            if (message.member.roles.cache.get(guildSettings.dj_role) == undefined) {
                return message.channel.send("You are not a dj")
            }
        }
        if (!message.member.voice.channel) return message.channel.send("you need to join a voice channel.");
        if (!args.length) return message.channel.send("you need to give me a URL or a search term.");

        // Setup GuildManager

        const player = ErelaManager.create({
            guild: message.guild.id,
            voiceChannel: message.member.voice.channel.id,
            textChannel: message.channel.id,
        });

        if (player.voiceChannel != message.member.voice.channel.id) {
            message.channel.send("You need to be in the same voice channel as I")
            return;
        }

        if (player.state == "DISCONNECTED") {
            player.connect();
        }

        player.setVolume(guildSettings.volume)

        var query = args.join(" ").trim();
        var searchResult: SearchResult;
        try {
            searchResult = await player.search(query, message.author);

            // Search for tracks using a query or url, using a query searches youtube automatically and the track requester object
            switch(searchResult.loadType) {
                case "SEARCH_RESULT":
                    player.queue.add(searchResult.tracks[0])

                    if (!player.playing && !player.paused && !player.queue.size) {
                        player.play()
            
                        const embed = new MessageEmbed()
                            .setColor(defaultColors.success)
                            .setThumbnail(searchResult.tracks[0].thumbnail)
                            .setAuthor('Playing', message.author.avatarURL(), searchResult.tracks[0].uri)
                            .addFields(
                                { name: 'Title', value: searchResult.tracks[0].title, inline: true }, 
                                { name: 'Duration', value: moment.duration(searchResult.tracks[0].duration, "ms").format("h:*mm:ss"), inline: true }
                            )
                        message.channel.send(embed);
                    } else {
                        const embed = new MessageEmbed()
                            .setColor(defaultColors.success)
                            .setThumbnail(searchResult.tracks[0].thumbnail)
                            .setAuthor('Added to Queue', message.author.avatarURL(), searchResult.tracks[0].uri)
                            .addFields(
                                { name: 'Title', value: searchResult.tracks[0].title, inline: true }, 
                                { name: 'Duration', value: moment.duration(searchResult.tracks[0].duration, "ms").format("h:*mm:ss"), inline: true }
                            )
                        message.channel.send(embed);
                    }

                    break;

                case "TRACK_LOADED":
                    player.queue.add(searchResult.tracks[0])

                    if (!player.playing && !player.paused && !player.queue.size) {
                        player.play()
            
                        const embed = new MessageEmbed()
                            .setColor(defaultColors.success)
                            .setThumbnail(searchResult.tracks[0].thumbnail)
                            .setAuthor('Playing', message.author.avatarURL(), searchResult.tracks[0].uri)
                            .addFields(
                                { name: 'Title', value: searchResult.tracks[0].title, inline: true }, 
                                { name: 'Duration', value: moment.duration(searchResult.tracks[0].duration, "ms").format("h:*mm:ss"), inline: true }
                            )
                        message.channel.send(embed);
                    } else {
                        const embed = new MessageEmbed()
                            .setColor(defaultColors.success)
                            .setThumbnail(searchResult.tracks[0].thumbnail)
                            .setAuthor('Added to Queue', message.author.avatarURL(), searchResult.tracks[0].uri)
                            .addFields(
                                { name: 'Title', value: searchResult.tracks[0].title, inline: true }, 
                                { name: 'Duration', value: moment.duration(searchResult.tracks[0].duration, "ms").format("h:*mm:ss"), inline: true }
                            )
                        message.channel.send(embed);
                    }

                    break;

                case "PLAYLIST_LOADED":
                    player.queue.add(searchResult.tracks[0])

                    if (!player.playing && !player.paused && !player.queue.size) {
                        player.play()
            
                        const embed = new MessageEmbed()
                            .setColor(defaultColors.success)
                            .setAuthor('Playing playlist', message.author.avatarURL(), query)
                            .addField('Title', searchResult.playlist.name,  false)
                            .addField('First in queue', searchResult.tracks[0].title, false)
                        
                        if (searchResult.tracks[0].thumbnail)
                            embed.setThumbnail(searchResult.tracks[0].thumbnail)

                        const duration = moment.duration(searchResult.playlist.duration, "ms").format("h:*mm:ss");
                        if (duration != '0') {
                            embed.addField('Duration', duration, false);
                        }
                        message.channel.send(embed);

                    } else {
                        const embed = new MessageEmbed()
                            .setColor(defaultColors.success)
                            .setAuthor('Added playlist to Queue', message.author.avatarURL(), query)
                            .addField('Title', searchResult.playlist.name,  false)
                            .addField('First in queue', searchResult.tracks[0].title, false)

                        if (searchResult.tracks[0].thumbnail)
                            embed.setThumbnail(searchResult.tracks[0].thumbnail)


                        const duration = moment.duration(searchResult.playlist.duration, "ms").format("h:*mm:ss");
                        if (duration != '0') {
                            embed.addField('Duration', duration, false);
                        }
                        message.channel.send(embed);
                    }
                
                    for (var i = 1; i < searchResult.tracks.length; i++) {
                        player.queue.add(searchResult.tracks[i])
                    }

                    break;

                case "LOAD_FAILED":
                    throw searchResult.exception

                case "NO_MATCHES":
                    message.channel.send("No matches found!")
                    break;
            }

        } catch (err) {
            return message.reply(`there was an error while searching: ${err.message}`);
        }
    },
};