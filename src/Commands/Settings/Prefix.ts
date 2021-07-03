import { Message, Permissions } from 'discord.js'
import GuildSettings from '../../Guilds/GuildSettings';
import Command from '../../Models/Command';
import MessageCtx from '../../Models/MessageCtx';

export default class Prefix extends Command {
    constructor() {
        super(
            "Prefix",
            "Change prefix",
            ["prefix"],
            [Permissions.FLAGS.ADMINISTRATOR]
        )
    }

    public async execute(ctx: MessageCtx): Promise<void> {
        var guildSettings = GuildSettings.getGuildSettings(ctx.channel.guild.id, ctx.channel.client)

        if (ctx.args.length == 0) {
            ctx.send("Current prefix is ``" + guildSettings.prefix + "``")
            return
        }

        var newprefix = ctx.args[0].trim();
        ctx.send("Prefix changed from ``" + guildSettings.prefix + "`` to ``" + newprefix + "``")
        console.log(newprefix)
        guildSettings.prefix = newprefix;
        guildSettings.save()
    }

    public getInteraction() {
        return {
            "name": "volume",
            "description": "Change volume",
            "options": [
                {
                    "type": 3,
                    "name": "prefix",
                    "description": "New prefix",
                    "required": false
                }
            ]
        }
    }

}
