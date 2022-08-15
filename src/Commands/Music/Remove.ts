import { Message, MessageReaction } from "discord.js";
import GuildSettings from "../../Guilds/GuildSettings";
import { distube } from "../../Utils/AudioManager";
import Command from "../../Models/Command";

export default class Remove extends Command {
    constructor() {
        super(
            "Remove", 
            "Remove track from queue",
            ["remove", "rm"],
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
        
        const queue = distube.getQueue(message)

        if (!queue) {
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

        var deleteIndex: number = null;
        if (args.length >= 1) {
            deleteIndex = Number(args[0]);
            if (isNaN(deleteIndex)) {
                message.channel.send(`Not a valid index in queue (${args[0]})`)
                return;
            }
            deleteIndex -= 1;
        }
        if (deleteIndex < 0 || deleteIndex >= queue.songs.length) {
            message.channel.send("Invalid queue position")
            return;
        }

        const track = queue.songs[deleteIndex];
        message.channel.send(`Removed ${track.name} from queue!`)
        queue.songs.splice(deleteIndex, 1)
    }
    
}