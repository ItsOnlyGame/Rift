import { Message, MessageEmbed } from 'discord.js'
import getConfig from "../../Config";
import Command from '../../Models/Command';
import MessageCtx from '../../Models/MessageCtx';
const defaultColors = getConfig().defaultColors

export default class Help extends Command {
    constructor() {
        super(
            "Help",
            "Help",
            ["help"],
            null
        )
    }

    public async execute(ctx: MessageCtx): Promise<void> {
        const embed = new MessageEmbed()
            .setAuthor('Help', ctx.member.user.avatarURL())
            .setColor(defaultColors.success)
            .setDescription(`Every command is listed under this link: \nhttps://github.com/ItsOnlyGame/Rift#commands`)

        ctx.send(embed);
    }

    public getInteraction() {
        return {
            "name": "help",
            "description": "Help"
        }
    }

}