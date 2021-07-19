import GuildSettings from "../../Guilds/GuildSettings";
import Command from "../../Models/Command";
import { ErelaManager } from "../../Models/LavaplayerManager";
import MessageCtx from "../../Models/MessageCtx";

export default class Join extends Command {
    constructor() {
        super(
            "Join", 
            "Join the voice channel",
            ["join", "connect", "c"],
            null
        )
    }

    public execute(ctx: MessageCtx): void {
        if (!ErelaManager.get(ctx.channel.guild.id)) {
            const player = ErelaManager.create({
                guild: ctx.channel.guild.id,
                voiceChannel: ctx.member.voice.channel.id,
                textChannel: ctx.channel.id,
            });

            player.connect();
            if (ctx.guildSettings.notifyVoiceConnection || ctx.interactionData)
                ctx.send(`Connecting to ${ctx.member.voice.channel.name}`)

        } else {
            ctx.send(`Already connected to a voice channel`)
        }
    }

    public getInteraction() {
        return {
            "name": "join",
            "description": "Join the voice channel"
        }
    }
}
