import { Client, Snowflake } from "discord.js";
import * as fs from 'fs'
import { getLogger } from 'log4js';
const logger = getLogger();

class GuildSettings {
    /**
     * 
     * @param {string|number} id 
     * @param {Client} client 
     */

    public name: string;
    public id: Snowflake;
    public prefix: string;
    public dj_role: string;
    public volume: number;
    public autoclean: boolean;
    public notifyVoiceConnection: boolean;

    constructor(id: Snowflake, client: Client) {
        const guild = client.guilds.cache.get(id);
        if (guild == undefined) throw "Something went wrong while fetching guild data"

        this.name = guild.name;
        this.id = id;
        this.prefix = "r!";
        this.dj_role = null;
        this.volume = 50;
        this.autoclean = true;
        this.notifyVoiceConnection = false;
    }

    /**
     * 
     * @param {Snowflake} id 
     * @param {Client} client 
     * @returns GuildSetting for the specified guild
     */
    public static getGuildSettings(id: Snowflake, client: Client): GuildSettings {
        var settings: any;
        try {
            var file = fs.readFileSync(`./config/guilds/${id}.json`, 'utf-8');
            if (file == "") throw "ERROR: File content is empty"
            settings = JSON.parse(file);
        } catch (err) {
            logger.error(err)
            settings = new GuildSettings(id, client)
            GuildSettings.saveGuildSettings(settings);
        }

        Object.setPrototypeOf(settings, GuildSettings.prototype)
        return settings;
    }


    /**
     * GuildSettings to be saved
     * @param {GuildSettings} settings 
     */
     public static saveGuildSettings(settings: GuildSettings) : void {
        if (!fs.existsSync(`./config/guilds`)){
            fs.mkdirSync(`./config/guilds`)
        }
        console.log(settings)

        fs.writeFile(`./config/guilds/${settings.id}.json`, JSON.stringify(settings), 'utf-8', (err) => {
            if (err) {
                logger.error(`Error writing file: ${err}`);
            } else {
                logger.info(`File is written successfully!: ${settings.id}.json`);
            }
        });
    }
}



export default GuildSettings;