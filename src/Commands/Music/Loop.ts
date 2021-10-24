import { Message } from "discord.js";
import { RepeatMode } from "distube";
import GuildSettings from "../../Guilds/GuildSettings";
import { distube } from "../../Models/AudioManager";
import Command from "../../Models/Command";

export default class Loop extends Command {
    constructor() {
        super(
            "Loop", 
            "Loop current track",
            ["loop", "l"],
            null
        )
    }

    public async execute(message: Message, args: string[]): Promise<void> {
        const queue = distube.getQueue(message)
        
        if (queue.repeatMode == 0) {
            queue.setRepeatMode(RepeatMode.DISABLED)
            message.channel.send("Looping: disabled")

        } else {
            queue.setRepeatMode(RepeatMode.SONG)
            message.channel.send("Looping: enabled")
        }
        
    }
}