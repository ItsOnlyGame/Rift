import { Message, Permissions } from "discord.js";
import GuildSettings from "../../Guilds/GuildSettings";
import Command from "../../Models/Command";
import { ErelaManager } from "../../Models/LavaplayerManager";
import MessageCtx from "../../Models/MessageCtx";

export default class Pause extends Command {
    constructor() {
        super(
            "Pause", 
            "Pause track",
            ["pause"],
            null
        )
    }

    public async execute(ctx: MessageCtx): Promise<void> {
        if (ctx.guildSettings.dj_role != null) {
            if (ctx.member.roles.cache.get(ctx.guildSettings.dj_role) == undefined) {
                ctx.send("You are not a dj")
                return
            }
        }

        const player = ErelaManager.get(ctx.channel.guild.id)   
        if (player == undefined) {
            ctx.send("Nothing is currenly playing");
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

        if (player.paused) {
            player.pause(false);
            ctx.send("Resuming track")
        } else {
            player.pause(true)
            ctx.send("Pausing track")
        }
    }

    public getInteraction() {
        return {
            "name": "pause",
            "description": "Pause track"
        }
    }
}