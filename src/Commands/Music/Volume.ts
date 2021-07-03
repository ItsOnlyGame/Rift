import { Message } from 'discord.js'
import GuildSettings from '../../Guilds/GuildSettings';
import Command from '../../Models/Command';
import { ErelaManager } from '../../Models/LavaplayerManager';
import MessageCtx from '../../Models/MessageCtx';

export default class Volume extends Command {
    constructor() {
        super(
            "Volume",
            "Change volume",
            ["volume", "vol", "v"],
            null
        )
    }

    public async execute(ctx: MessageCtx): Promise<void> {
        const guildSettings = GuildSettings.getGuildSettings(ctx.channel.guild.id, ctx.channel.client)

        if (guildSettings.dj_role != null) {
            if (ctx.member.roles.cache.get(guildSettings.dj_role) == undefined) {
                ctx.send("You are not a dj")
                return
            }
        }

        const player = ErelaManager.get(ctx.channel.guild.id);

        if (ctx.args.length >= 1) {
            var newvolume = Number(ctx.args[0]);
            if (isNaN(newvolume)) {
                ctx.send(`Not a valid volume: ${ctx.args[0]}`)
                return;
            }

            if (player != undefined) {
                player.setVolume(newvolume)
                if (player.voiceChannel != ctx.member.voice.channel.id) {
                    ctx.send("You need to be in the same voice channel as I")
                    return;
                }
            }

            guildSettings.volume = newvolume;
            guildSettings.save();

            ctx.send(`Volume set to ${newvolume}`)
            return;
        }

        ctx.send(`Volume is \`\`${guildSettings.volume}\`\``)
    }

    public getInteraction() {
        return {
            "name": "volume",
            "description": "Change volume",
            "options": [
                {
                    "type": 4,
                    "name": "new_volume",
                    "description": "New volume",
                    "required": false
                }
            ]
        }

    }

}