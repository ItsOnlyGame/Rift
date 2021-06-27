import { Client, Snowflake } from "discord.js";
import * as fs from 'fs'

export default class GuildSettings {
    /**
     * 
     * @param {string|number} id 
     * @param {Client} client 
     */

    public name: string
    public id: Snowflake
    public prefix: string
    public dj_role: string
    public volume: number
    public autoclean: boolean

    constructor(id: Snowflake, client: Client) {
        const guild = client.guilds.cache.get(id);
        if (guild == undefined) throw "Something went wrong while fetching guild data"

        this.name = guild.name;
        this.id = id;
        this.prefix = "r!";
        this.dj_role = null;
        this.volume = 50;
        this.autoclean = true;
    }

    save() {
        GuildSettings.saveGuildSettings(this);
    }

    /**
     * 
     * @param {Snowflake} id 
     * @param {Client} client 
     * @returns GuildSetting for the specified guild
     */
    static getGuildSettings(id: Snowflake, client: Client): GuildSettings {
        var settings: GuildSettings;
        try {
            var file = fs.readFileSync(`./config/guilds/${id}.json`, 'utf8');
            settings = JSON.parse(file);
        } catch (err) {
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
    static saveGuildSettings(settings: GuildSettings) {
        if (!fs.existsSync(`./config`)){
            fs.mkdirSync(`./config`)
        }

        fs.writeFile(`./config/guilds/${settings.id}.json`, JSON.stringify(settings), 'utf8', (err) => {
            if (err) {
                console.log(`Error writing file: ${err}`);
            } else {
                console.log(`File is written successfully!: ${settings.id}.json`);
            }
        });
    }
}