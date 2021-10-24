import { Message } from "discord.js";
import { distube } from "../../Models/AudioManager";
import Command from "../../Models/Command";

export default class Queue extends Command {
    constructor() {
        super(
            "Queue", 
            "Get queue",
            ["queue", "q"],
            null
        )
    }

    public async execute(message: Message, args: string[]): Promise<void> {
        const queue = distube.getQueue(message)

        if (queue == undefined) {
            message.channel.send("Queue is empty!");
            return;
        }

        if (queue.songs.length == 0) {
            message.channel.send("Queue is empty!");
            return;
        }

        const size = 40
        for (var times = 0; times < Math.ceil(queue.songs.length / size); times++) {
            var queueMessage = ""

            queueMessage += "```";

            for (var i = times * size; i < (times * size) + size; i++) {
                if (times == 0 && i == 0) {
                    queueMessage += `Now playing: ${queue.songs[0].name}\n`
                    continue;
                }
                if (queue.songs.length <= i) break;
                var track = queue.songs[i];
                queueMessage += `${i+1}: ${track.name}\n`
            }

            queueMessage += "```";
            message.channel.send(queueMessage)
        }
    }
}
