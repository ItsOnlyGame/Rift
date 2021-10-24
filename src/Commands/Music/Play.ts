import Command from "../../Models/Command";
import { getLogger } from 'log4js';
import { Message } from "discord.js";
import GuildSettings from "../../Guilds/GuildSettings";
const logger = getLogger();
import { distube } from "../../Models/AudioManager";

export default class Play extends Command {
    constructor() {
        super(
            "Play", 
            "Play track(s)",
            ["play", "p"],
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
        if (!message.member.voice.channel) {
            message.channel.send("you need to join a voice channel.");
            return
        } 
        if (!args.length) {
            message.channel.send("you need to give me a URL or a search term.");
            return 
        }

        var query = args.join(' ').trim();
        logger.info(`Searching with query: ${query}`)
        distube.play(message, query)
    }

    private isURL(str: string) {
        try {
            new URL(str);
        } catch (_) {
            return false;  
        }
        return true
    }
}