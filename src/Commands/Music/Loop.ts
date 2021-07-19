import { Message } from "discord.js";
import GuildSettings from "../../Guilds/GuildSettings";
import Command from "../../Models/Command";
import { ErelaManager } from "../../Models/LavaplayerManager";
import MessageCtx from "../../Models/MessageCtx";

export default class Loop extends Command {
    constructor() {
        super(
            "Loop", 
            "Loop current track",
            ["loop", "l"],
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

        
        if (player.trackRepeat) {
            player.setTrackRepeat(false)
            ctx.send("Not looping current track")

        } else {
            player.setTrackRepeat(true)
            ctx.send("Looping current track")
        }
    }

    public getInteraction() {
        return {
            "name": "loop",
            "description": "Loop current track"
        }
    }
}