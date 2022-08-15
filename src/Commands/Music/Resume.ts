import { Message } from "discord.js";
import GuildSettings from "../../Guilds/GuildSettings";
import { distube } from "../../Utils/AudioManager";
import Command from "../../Models/Command";

export default class Resume extends Command {
    constructor() {
        super(
            "Resume", 
            "Resumes track playback",
            ["resume", "res", "r"],
            null
        )
    }

    public async execute(message: Message, args: string[]): Promise<void> {
        
        const guildSettings = GuildSettings.getGuildSettings(message.guildId, message.client)

        if (guildSettings.dj_role != null) {
            if (message.member.roles.cache.get(guildSettings.dj_role) == undefined) {
                message.channel.send("You are not a dj")
                return 
            }
        }

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

        } else {
            message.channel.send("Track is already playing!")
        }
        
    }
    
}