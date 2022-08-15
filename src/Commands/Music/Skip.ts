import { Message } from "discord.js";
import GuildSettings from "../../Guilds/GuildSettings";
import { distube } from "../../Utils/AudioManager";
import Command from "../../Models/Command";

export default class Skip extends Command {
    constructor() {
        super(
            "Skip",
            "Skips tracks",
            ["skip", "s"],
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


        var skipAmount = 1;
        if (args.length >= 1) {
            skipAmount = Number(args[0]);
            if (isNaN(skipAmount)) {
                message.channel.send(`Not a valid skip amount: ${args[0]}`)
                return;
            }
        }

        for (var i = 0; i < skipAmount; i++) {
            queue.skip()
        }
        
    }

}