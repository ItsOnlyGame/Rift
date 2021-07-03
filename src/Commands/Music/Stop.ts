import { Message } from "discord.js";
import GuildSettings from "../../Guilds/GuildSettings";
import Command from "../../Models/Command";
import { ErelaManager } from "../../Models/LavaplayerManager";
import MessageCtx from "../../Models/MessageCtx";

export default class Stop extends Command {
    constructor() {
        super(
            "Stop",
            "Stop playback",
            ["stop"],
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

        player.disconnect();
        player.destroy();

        ctx.send("Queue cleared and playback stopped!");
    }

    public getInteraction() {
        return {
            "name": "stop",
            "description": "Stop playback"
        }
    }

}