import { Message } from "discord.js";
import { ErelaManager } from "../../Models/LavaplayerManager";

var moment = require("moment");
var momentDurationFormatSetup = require("moment-duration-format");
momentDurationFormatSetup(moment);

export default {
    name: "Queue",
    description: "Get queue",
    aliases: ["queue", "q"],
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

        for (var times = 0; times < Math.ceil(player.queue.length + 1 / 50.0); times++) {
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
            message.channel.send(queueMessage)
        }
    },
};