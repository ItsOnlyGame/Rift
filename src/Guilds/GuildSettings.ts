import { Client, Snowflake } from "discord.js";
import * as fs from 'fs'
import { logger } from '../index';

class GuildSettings {
    /**
     * 
     * @param {string|number} id 
     * @param {Client} client 
     */

    public name: string;
    public id: Snowflake;
    public dj_role: string;
    public volume: number;

    constructor(id: Snowflake, client: Client) {
        const guild = client.guilds.cache.get(id);
        if (guild == undefined) throw "Something went wrong while fetching guild data"

        this.name = guild.name;
        this.id = id;
        this.dj_role = null;
        this.volume = 50;
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

        fs.writeFile(`./config/guilds/${settings.id}.json`, JSON.stringify(settings, null, "\t"), 'utf-8', (err) => {
            if (err) {
                logger.error(`Error writing file: ${err}`);
            } else {
                logger.info(`File is written successfully!: '${settings.id}.json'`);
            }
        });
    }
}



export default GuildSettings;