import { Message } from "discord.js";
import { distube } from "../../Utils/AudioManager";
import Command from "../../Models/Command";

export default class Pause extends Command {
    constructor() {
        super(
            "Pause", 
            "Pause track",
            ["pause"],
            null
        )
    }

    public async execute(message: Message, args: string[]): Promise<void> {
        const queue = distube.getQueue(message);

        if (!queue) {
            message.channel.send("Nothing is currenly playing");
            return;
        }

        if (queue.songs.length == 0) {
            message.channel.send("Queue is empty!");
            return;
        }

        if (queue.voiceChannel.id != message.member.voice.channel.id) {
            message.channel.send("You need to be in the same voice channel as I")
            return;
        }

        if (queue.paused) {
            queue.resume()
            message.channel.send("Resuming track")
        } else {
            queue.pause()
            message.channel.send("Pausing track")
        }
        
    }
}