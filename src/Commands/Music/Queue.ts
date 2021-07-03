import { Message } from "discord.js";
import Command from "../../Models/Command";
import { ErelaManager } from "../../Models/LavaplayerManager";
import MessageCtx from "../../Models/MessageCtx";

var moment = require("moment");
var momentDurationFormatSetup = require("moment-duration-format");
momentDurationFormatSetup(moment);

export default class Queue extends Command {
    constructor() {
        super(
            "Queue", 
            "Get queue",
            ["queue", "q"],
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

        for (var times = 0; times < Math.ceil(player.queue.length / 50.0); times++) {
            var queueMessage = ""

            queueMessage += "```";
            if (times == 0) {
                queueMessage += `Now playing: ${player.queue.current.title} | ${moment.duration(player.queue.current.duration, "ms").format("h:*mm:ss")} \n`
            }

            for (var i = times * 50; i < (times * 50) + 50; i++) {
                if (player.queue.length <= i) break;
                var track = player.queue[i];
                queueMessage += `${i+1}: ${track.title} | ${moment.duration(track.duration, "ms").format("h:*mm:ss")} \n`
            }

            queueMessage += "```";
            ctx.send(queueMessage)
        }
    }

    public getInteraction() {
        return {
            "name": "queue",
            "description": "Get queue"
        }
    }
}
