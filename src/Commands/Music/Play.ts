import { Message, MessageEmbed } from "discord.js";
import { ErelaManager } from "../../Models/LavaplayerManager";
import { SearchResult } from "erela.js";
import GuildSettings from "../../Guilds/GuildSettings";
import getConfig from "../../Config";
import Command from "../../Models/Command";
import MessageCtx from "../../Models/MessageCtx";
const defaultColors = getConfig().defaultColors

var moment = require("moment");
var momentDurationFormatSetup = require("moment-duration-format");
momentDurationFormatSetup(moment);

export default class Play extends Command {
    constructor() {
        super(
            "Play", 
            "Play track(s)",
            ["play", "p"],
            null
        )
    }

    public async execute(ctx: MessageCtx): Promise<void> {
        const guildSettings = GuildSettings.getGuildSettings(ctx.channel.guild.id, ctx.channel.client)

        if (guildSettings.dj_role != null) {
            if (ctx.member.roles.cache.get(guildSettings.dj_role) == undefined) {
                ctx.send("You are not a dj")
                return
            }
        }
        if (!ctx.member.voice.channel) {
            ctx.send("you need to join a voice channel.");
            return
        } 
        if (!ctx.args.length) {
            ctx.send("you need to give me a URL or a search term.");
            return 
        }

        // Setup GuildManager

        const player = ErelaManager.create({
            guild: ctx.channel.guild.id,
            voiceChannel: ctx.member.voice.channel.id,
            textChannel: ctx.channel.id,
        });

        if (player.voiceChannel != ctx.member.voice.channel.id) {
            ctx.send("You need to be in the same voice channel as I")
            return;
        }

        if (player.state == "DISCONNECTED") {
            player.connect();
        }

        player.setVolume(guildSettings.volume)

        var query = ctx.args.join(" ").trim();
        var searchResult: SearchResult;
        try {
            searchResult = await player.search(query, ctx.member);

            // Search for tracks using a query or url, using a query searches youtube automatically and the track requester object
            switch(searchResult.loadType) {
                case "SEARCH_RESULT":
                    player.queue.add(searchResult.tracks[0])

                    if (!player.playing && !player.paused && !player.queue.size) {
                        player.play()
            
                        const embed = new MessageEmbed()
                            .setColor(defaultColors.success)
                            .setThumbnail(searchResult.tracks[0].thumbnail)
                            .setAuthor('Playing', ctx.member.user.avatarURL(), searchResult.tracks[0].uri)
                            .addFields(
                                { name: 'Title', value: searchResult.tracks[0].title, inline: true }, 
                                { name: 'Duration', value: moment.duration(searchResult.tracks[0].duration, "ms").format("h:*mm:ss"), inline: true }
                            )
                        ctx.send(embed);
                    } else {
                        const embed = new MessageEmbed()
                            .setColor(defaultColors.success)
                            .setThumbnail(searchResult.tracks[0].thumbnail)
                            .setAuthor('Added to Queue', ctx.member.user.avatarURL(), searchResult.tracks[0].uri)
                            .addFields(
                                { name: 'Title', value: searchResult.tracks[0].title, inline: true }, 
                                { name: 'Duration', value: moment.duration(searchResult.tracks[0].duration, "ms").format("h:*mm:ss"), inline: true }
                            )
                        ctx.send(embed);
                    }

                    break;

                case "TRACK_LOADED":
                    player.queue.add(searchResult.tracks[0])

                    if (!player.playing && !player.paused && !player.queue.size) {
                        player.play()
            
                        const embed = new MessageEmbed()
                            .setColor(defaultColors.success)
                            .setThumbnail(searchResult.tracks[0].thumbnail)
                            .setAuthor('Playing', ctx.member.user.avatarURL(), searchResult.tracks[0].uri)
                            .addFields(
                                { name: 'Title', value: searchResult.tracks[0].title, inline: true }, 
                                { name: 'Duration', value: moment.duration(searchResult.tracks[0].duration, "ms").format("h:*mm:ss"), inline: true }
                            )
                        ctx.send(embed);
                    } else {
                        const embed = new MessageEmbed()
                            .setColor(defaultColors.success)
                            .setThumbnail(searchResult.tracks[0].thumbnail)
                            .setAuthor('Added to Queue', ctx.member.user.avatarURL(), searchResult.tracks[0].uri)
                            .addFields(
                                { name: 'Title', value: searchResult.tracks[0].title, inline: true }, 
                                { name: 'Duration', value: moment.duration(searchResult.tracks[0].duration, "ms").format("h:*mm:ss"), inline: true }
                            )
                        ctx.send(embed);
                    }

                    break;

                case "PLAYLIST_LOADED":
                    player.queue.add(searchResult.tracks[0])

                    if (!player.playing && !player.paused && !player.queue.size) {
                        player.play()
            
                        const embed = new MessageEmbed()
                            .setColor(defaultColors.success)
                            .setAuthor('Playing playlist', ctx.member.user.avatarURL(), query)
                            .addField('Title', searchResult.playlist.name,  false)
                            .addField('First in queue', searchResult.tracks[0].title, false)
                        
                        if (searchResult.tracks[0].thumbnail)
                            embed.setThumbnail(searchResult.tracks[0].thumbnail)

                        const duration = moment.duration(searchResult.playlist.duration, "ms").format("h:*mm:ss");
                        if (duration != '0') {
                            embed.addField('Duration', duration, false);
                        }
                        ctx.send(embed);

                    } else {
                        const embed = new MessageEmbed()
                            .setColor(defaultColors.success)
                            .setAuthor('Added playlist to Queue', ctx.member.user.avatarURL(), query)
                            .addField('Title', searchResult.playlist.name,  false)
                            .addField('First in queue', searchResult.tracks[0].title, false)

                        if (searchResult.tracks[0].thumbnail)
                            embed.setThumbnail(searchResult.tracks[0].thumbnail)


                        const duration = moment.duration(searchResult.playlist.duration, "ms").format("h:*mm:ss");
                        if (duration != '0') {
                            embed.addField('Duration', duration, false);
                        }
                        ctx.send(embed);
                    }
                
                    for (var i = 1; i < searchResult.tracks.length; i++) {
                        player.queue.add(searchResult.tracks[i])
                    }

                    break;

                case "LOAD_FAILED":
                    throw searchResult.exception

                case "NO_MATCHES":
                    ctx.send("No matches found!")
                    break;
            }

        } catch (err) {
            ctx.send(`There was an error while searching: ${err.message}`);
            return 
        }
    }

    public getInteraction() {
        return {
            "name": "play",
            "description": "Play track(s)",
            "options": [
              {
                "type": 3,
                "name": "query",
                "description": "Url or a search query",
                "required": true
              }
            ]
          }
          
    }
}