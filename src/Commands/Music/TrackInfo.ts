import { Message, MessageEmbed } from "discord.js";
import { ErelaManager } from "../../Models/LavaplayerManager";
import getConfig from "../../Config";
const defaultColors = getConfig().defaultColors

var moment = require("moment");
import momentDurationFormatSetup from "moment-duration-format";
momentDurationFormatSetup(moment);

export default {
    name: "Track Info",
    description: "Get the info of the track thats currently playing",
    aliases: ["trackinfo", "tf", "info"],
    permissionReq: null,
    /**
     * Executes the command
     * @param {Message} message 
     * @param {Array<string>} args
     */
    execute: async function(message: Message, args: Array<string>) {
        const player = ErelaManager.get(message.guild.id);

        if (player == undefined) {
            message.channel.send("Queue is empty!");
            return;
        }

        if (player.queue.length == 0 && player.queue.current == undefined) {
            message.channel.send("Queue is empty!");
            return;
        }

        if (player.voiceChannel != message.member.voice.channel.id) {
            message.channel.send("You need to be in the same voice channel as I")
            return;
        }

        const track = player.queue.current;

        const pos = moment.duration(player.position, "ms").format("h:*mm:ss")
        const dur = moment.duration(player.queue.current.duration, "ms").format("h:*mm:ss")

        const embed = new MessageEmbed()
            .setColor(defaultColors.success)
            .setAuthor('Info', message.author.avatarURL(), null)
            .addField('Title', track.title,  false)
            .addField('Author', track.author,  false)
            .addField('Duration', `${pos} - ${dur}`,  false)
            .addField('Url', track.uri,  false)

        if (track.thumbnail)
            embed.setThumbnail(track.thumbnail)
    
        message.channel.send(embed);
    },
};