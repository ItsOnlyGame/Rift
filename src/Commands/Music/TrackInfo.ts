import { Message, MessageEmbed } from "discord.js";
import { ErelaManager } from "../../Models/LavaplayerManager";
import getConfig from "../../Config";
const defaultColors = getConfig().defaultColors

var moment = require("moment");
import momentDurationFormatSetup from "moment-duration-format";
import Command from "../../Models/Command";
import MessageCtx from "../../Models/MessageCtx";
momentDurationFormatSetup(moment);

export default class TrackInfo extends Command {
    constructor() {
        super(
            "Track Info",
            "Get the info of the track thats currently playing",
            ["trackinfo", "tf", "info"],
            null
        )
    }

    public async execute(ctx: MessageCtx): Promise<void> {
        const player = ErelaManager.get(ctx.channel.guild.id);

        if (player == undefined) {
            ctx.send("Queue is empty!");
            return;
        }

        if (player.queue.length == 0 && player.queue.current == undefined) {
            ctx.send("Queue is empty!");
            return;
        }

        if (player.voiceChannel != ctx.member.voice.channel.id) {
            ctx.send("You need to be in the same voice channel as I")
            return;
        }

        const track = player.queue.current;

        const pos = moment.duration(player.position, "ms").format("h:*mm:ss")
        const dur = moment.duration(player.queue.current.duration, "ms").format("h:*mm:ss")

        const embed = new MessageEmbed()
            .setColor(defaultColors.success)
            .setAuthor('Info', ctx.member.user.avatarURL(), null)
            .addField('Title', track.title,  false)
            .addField('Author', track.author,  false)
            .addField('Duration', `${pos} - ${dur}`,  false)
            .addField('Url', track.uri,  false)

        if (track.thumbnail)
            embed.setThumbnail(track.thumbnail)
    
        ctx.send(embed);
    }

    public getInteraction() {
        return {
            "name": "trackinfo",
            "description": "Get the info of the track thats currently playing"
        }
    }

}