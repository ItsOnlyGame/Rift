import { Message } from "discord.js";
import GuildSettings from "../../Guilds/GuildSettings";
import Command from "../../Models/Command";
import { ErelaManager } from "../../Models/LavaplayerManager";
import MessageCtx from "../../Models/MessageCtx";

export default class Resume extends Command {
    constructor() {
        super(
            "Resume", 
            "Resumes track playback",
            ["resume", "res", "r"],
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

        const player = ErelaManager.get(ctx.channel.guild.id);

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

        } else {
            ctx.send("Track is already playing!")
        }
    }

    public getInteraction() {
        return {
            "name": "resume",
            "description": "Resumes track playback"
        }
    }
    
}