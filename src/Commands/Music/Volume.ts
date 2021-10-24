import { Message, MessageSelectMenu } from 'discord.js';
import GuildSettings from '../../Guilds/GuildSettings';
import { distube } from '../../Models/AudioManager';
import Command from '../../Models/Command';

export default class Volume extends Command {
    constructor() {
        super(
            "Volume",
            "Change volume",
            ["volume", "vol", "v"],
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

        if (args.length >= 1) {
            var newvolume = Number(args[0]);
            if (isNaN(newvolume)) {
                message.channel.send(`Not a valid volume: ${args[0]}`)
                return;
            }

            if (!queue) {
                if (queue.voiceChannel.id != message.member.voice.channel.id) {
                    message.channel.send("You need to be in the same voice channel as I")
                    return;
                }
            }
            
            queue.setVolume(newvolume)
            guildSettings.volume = newvolume;
            GuildSettings.saveGuildSettings(guildSettings)

            message.channel.send(`Volume set to \`\`${newvolume}%\`\``)
            return;
        }

        message.channel.send(`Volume is \`\`${guildSettings.volume}%\`\``)
        
    }

}