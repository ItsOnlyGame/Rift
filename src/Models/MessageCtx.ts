import { APIMessageContentResolvable, Channel, GuildMember, Message, MessageAdditions, MessageOptions, TextChannel } from "discord.js";

export default class MessageCtx {

    public channel: TextChannel;
    public member: GuildMember;
    public args: string[];

    constructor(args: string[], message?: Message, channel?: Channel, member?: GuildMember) {
        this.args = args;
        
        if (message) {
            this.channel = message.channel as TextChannel;
            this.member = message.member;
            return;
        }

        if (channel) {
            if (channel.type != "text")
                throw "Channel is not a text channel"

            if (member == undefined)
                throw "Member cannot be undefined"
            
            this.channel = channel as TextChannel
            this.member = member;
            return;
        }

    }

    async send(content: APIMessageContentResolvable | (MessageOptions & {split?: false;}) | MessageAdditions): Promise<Message> {
        return this.channel.send(content)
    }

}