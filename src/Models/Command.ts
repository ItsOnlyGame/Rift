import { Message, Permissions } from "discord.js";
import MessageCtx from "./MessageCtx";

export default abstract class Command {

    public name: string;
    public description: string;
    public aliases: string[];
    public permissionRequired: number[];

    /**
     * 
     * @param name Name of the command
     * @param description Description of the command
     * @param aliases Aliases that the command uses, first one fill be used for discord interactions
     * @param permissionRequired Permissions required to execute the command
     */
    constructor(name: string, description: string, aliases: string[], permissionRequired: number[]) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
        this.permissionRequired = permissionRequired;
    }

    /**
     * Executes the command
     * @param {Message} message 
     * @param {Array<string>} args
     */
    public abstract execute(ctx: MessageCtx): void | Promise<void>;


    /**
     * Created with https://rauf.wtf/slash/
     * @returns Discord interaction json
     */
    public abstract getInteraction(): any;
}