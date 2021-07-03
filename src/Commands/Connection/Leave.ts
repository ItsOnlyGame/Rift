import { Message } from "discord.js";
import GuildSettings from "../../Guilds/GuildSettings";
import Command from "../../Models/Command";
import { ErelaManager } from "../../Models/LavaplayerManager";
import MessageCtx from "../../Models/MessageCtx";

export default class Leave extends Command {
    constructor() {
        super(
            "Leave", 
            "Leave the voice channel",
            ["leave", "disconnect", "dc", "dis"],
            null
        )
    }

    public execute(ctx: MessageCtx): void {
        var guildSettings = GuildSettings.getGuildSettings(ctx.channel.guild.id, ctx.channel.client)

        const player = ErelaManager.get(ctx.channel.guild.id)
        if (player) {
            player.disconnect();
            player.destroy();

            if (guildSettings.notifyVoiceConnection)
            ctx.send(`Disconnecting from ${ctx.member.voice.channel.name}`)

        } else {
            ctx.send(`I'm not connected to any voice channel`)
        }
    }

    public getInteraction() {
        return {
            "name": "leave",
            "description": "Leave the voice channel"
          }
    }
}