import { HexColorString, Message, MessageEmbed } from 'discord.js'
import Config from '../../Config';
import Command from '../../Models/Command';

export default class Help extends Command {
    constructor() {
        super(
            "Help",
            "Help",
            ["help"],
            null
        )
    }

    public async execute(message: Message, args: string[]): Promise<void> {
        const embed = new MessageEmbed()
            .setAuthor({ name: 'Help', iconURL: message.member.user.avatarURL()})
            .setColor(Config.getConfig().defaultColors.success as HexColorString)
            .setDescription(`Every command is listed under this link: \nhttps://github.com/ItsOnlyGame/Rift#commands`)

        message.channel.send({embeds: [embed]});
    }

}