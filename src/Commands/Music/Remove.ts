import { Message } from "discord.js";
import GuildSettings from "../../Guilds/GuildSettings";
import Command from "../../Models/Command";
import { ErelaManager } from "../../Models/LavaplayerManager";
import MessageCtx from "../../Models/MessageCtx";

export default class Remove extends Command {
    constructor() {
        super(
            "Remove", 
            "Remove track from queue",
            ["remove", "rm"],
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

        var deleteIndex: number = null;
        if (ctx.args.length >= 1) {
            deleteIndex = Number(ctx.args[0]);
            if (isNaN(deleteIndex)) {
                ctx.send(`Not a valid index in queue (${ctx.args[0]})`)
                return;
            }
            deleteIndex -= 1;
        }
        if (deleteIndex < 0 || deleteIndex >= player.queue.length) {
            ctx.send("Invalid queue position")
            return;
        }

        const track = player.queue[deleteIndex];
        ctx.send(`Removed ${track.title} from queue!`)
        player.queue.remove(deleteIndex)
    }

    public getInteraction() {
        return {
            "name": "remove",
            "description": "Remove track from queue",
            "options": [
              {
                "type": 4,
                "name": "index",
                "description": "Remove from the queue on the index",
                "required": true
              }
            ]
          }
    }
    
}