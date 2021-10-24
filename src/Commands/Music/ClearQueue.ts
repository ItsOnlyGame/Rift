import { Message } from "discord.js";
import GuildSettings from "../../Guilds/GuildSettings";
import { distube } from "../../Models/AudioManager";
import Command from "../../Models/Command";

export default class ClearQueue extends Command {
    constructor() {
        super(
            "Clear Queue", 
            "Clears the queue",
            ["clearqueue", "clear"],
            null
        )
    }

    public async execute(message: Message, args: string[]): Promise<void> {
        const guildSettings = GuildSettings.getGuildSettings(message.guildId, message.client)
        const queue = distube.getQueue(message)

        if (guildSettings.dj_role != null) {
            if (message.member.roles.cache.get(guildSettings.dj_role) == undefined) {
                message.channel.send("You are not a dj")
                return;
            }
        }

        if (queue == null) {
            message.channel.send("Queue is empty!");
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

        queue.songs.slice(1)
        message.channel.send("Queue cleared!");
    }
}