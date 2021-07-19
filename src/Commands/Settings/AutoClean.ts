import { Message, Permissions } from 'discord.js'
import GuildSettings from '../../Guilds/GuildSettings';
import Command from '../../Models/Command';
import MessageCtx from '../../Models/MessageCtx';

export default class AutoClean extends Command {
    constructor() {
        super(
            "Auto Clean",
            "Automatically clean the command messages",
            ["autoclean"],
            [Permissions.FLAGS.ADMINISTRATOR]
        )
    }

    public async execute(ctx: MessageCtx): Promise<void> {
        if (ctx.args.length == 0) {
            ctx.send("Current Auto Clean mode is ``" + ctx.guildSettings.autoclean + "``")
            return
        }

        var newAutoClean = ctx.args[0].trim().toLowerCase();
        if (newAutoClean == "true") {
            ctx.send("Auto Clean mode set to ``" + newAutoClean + "``")
            ctx.guildSettings.autoclean = true
            GuildSettings.saveGuildSettings(ctx.guildSettings)
            return;

        } else if (newAutoClean == "false") {

            ctx.send("Auto Clean mode set to ``" + newAutoClean + "``")
            ctx.guildSettings.autoclean = false
            GuildSettings.saveGuildSettings(ctx.guildSettings)
            return;

        }

        ctx.send("Auto clean can only be set to true or false")
    }

    public getInteraction() {
        return {
            "name": "volume",
            "description": "Change volume",
            "options": [
                {
                    "type": 5,
                    "name": "bool",
                    "description": "New auto clean mode",
                    "required": false
                }
            ]
        }
    }

}
