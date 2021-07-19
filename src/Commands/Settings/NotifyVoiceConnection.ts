import { Message, Permissions } from 'discord.js'
import GuildSettings from '../../Guilds/GuildSettings';
import Command from '../../Models/Command';
import MessageCtx from '../../Models/MessageCtx';

export default class NotifyVoiceConnection extends Command {
    constructor() {
        super(
            "Notify Voice Connection",
            "Whether to tell if bot is (dis)connecting from/to a voice channel",
            ["notifyvc"],
            [Permissions.FLAGS.ADMINISTRATOR]
        )
    }

    public async execute(ctx: MessageCtx): Promise<void> {
        if (ctx.args.length == 0) {
            ctx.send("Notify voice connection mode is ``"+ctx.guildSettings.notifyVoiceConnection+"``")
            return
        }

        var newNotifyVoiceConnection = ctx.args[0].trim().toLowerCase();
        if (newNotifyVoiceConnection == "true") {
            ctx.send("Notify voice connection mode set to ``"+newNotifyVoiceConnection+"``")
            ctx.guildSettings.notifyVoiceConnection = true
            GuildSettings.saveGuildSettings(ctx.guildSettings)
            return;

        } else if (newNotifyVoiceConnection == "false") {

            ctx.send("Notify voice connection mode set to ``"+newNotifyVoiceConnection+"``")
            ctx.guildSettings.notifyVoiceConnection = false
            GuildSettings.saveGuildSettings(ctx.guildSettings)
            return;

        }

        ctx.send("Notify voice connection can only be set to true or false")
    }

    public getInteraction() {
        return {
            "name": "notifyvc",
            "description": "Whether to tell if bot is (dis)connecting from/to a voice channel",
            "options": [
                {
                    "type": 5,
                    "name": "bool",
                    "description": "New Notify Voice Connection mode",
                    "required": false
                }
            ]
        }
    }

}
