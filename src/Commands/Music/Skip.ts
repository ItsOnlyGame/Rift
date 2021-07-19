import { Message } from "discord.js";
import GuildSettings from "../../Guilds/GuildSettings";
import Command from "../../Models/Command";
import { ErelaManager } from "../../Models/LavaplayerManager";
import MessageCtx from "../../Models/MessageCtx";

export default class Skip extends Command {
    constructor() {
        super(
            "Skip",
            "Skips tracks",
            ["skip", "s"],
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


        var skipAmount = 1;
        if (ctx.args.length >= 1) {
            skipAmount = Number(ctx.args[0]);
            if (isNaN(skipAmount)) {
                ctx.send(`Not a valid skip amount: ${ctx.args[0]}`)
                return;
            }
        }

        player.stop(skipAmount)
    }

    public getInteraction() {
        return {
            "name": "skip",
            "description": "Skips tracks",
            "options": [
                {
                    "type": 4,
                    "name": "index",
                    "description": "Amount to skip",
                    "required": false
                }
            ]
        }
    }

}