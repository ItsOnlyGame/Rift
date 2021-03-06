import { APIMessageContentResolvable, Channel, Client, GuildMember, Message, MessageAdditions, MessageEmbed, MessageOptions, TextChannel } from "discord.js";
import GuildSettings from "../Guilds/GuildSettings";

export default class MessageCtx {

    public args: string[];
    public channel: TextChannel;
    public member: GuildMember;
    public interactionData: any;
    public guildSettings: GuildSettings;

    constructor(args: string[], channel: Channel, member: GuildMember, interactionData?: any, guildSettings?: GuildSettings) {
        this.args = args;

        this.channel = channel as TextChannel
        this.member = member;
        this.interactionData = interactionData;

        if (guildSettings == undefined) {
            this.guildSettings = GuildSettings.getGuildSettings(this.channel.guild.id, this.channel.client)
        } else {
            this.guildSettings = guildSettings
        }
    }

    async send(content: APIMessageContentResolvable | (MessageOptions & {split?: false;}) | MessageAdditions): Promise<Message|any> {
        if (this.interactionData) {
            if (content instanceof MessageEmbed) {
                // @ts-ignore
                return this.channel.client.api.interactions(this.interactionData.id, this.interactionData.token).callback.post({data: {
                    type: 4,
                    data: {
                        embeds: [content.toJSON()]
                    }
                }})
             } else {
                // @ts-ignore
                return this.channel.client.api.interactions(this.interactionData.id, this.interactionData.token).callback.post({data: {
                    type: 4,
                    data: {
                        content: content
                    }
                }})
            }
        }

        return this.channel.send(content)
    }

}