import * as fs from 'fs'

interface Config {
    token: string,
    bot_id: string,
    defaultColors: {
        success: string,
        error: string
    },
    spotify: {
        clientId: string,
        clientSecret: string,
        countryCode: string
    },
    REFRESH_SLASH_COMMANDS: boolean
}

const defaultConfig: Config = {
    token: null,
    bot_id: null,
    defaultColors: {
        success: "#4da2b7",
        error: "#bc2727"
    },
    spotify: {
        clientId: null,
        clientSecret: null,
        countryCode: null,
    },
    REFRESH_SLASH_COMMANDS: true
}

export function getConfig(): Config {
    if (!fs.existsSync('./config')) {
        fs.mkdirSync('./config')
    }

    if (!fs.existsSync('./config/config.json')) {
        fs.writeFileSync('./config/config.json', JSON.stringify(defaultConfig, null, "\t"))
        throw "Please go fill out the config file!"
    }

    var config = JSON.parse(fs.readFileSync('./config/config.json', 'utf-8'));

    for (var c in defaultConfig) {
        if (config[c] == undefined) {
            config[c] = null;
            fs.writeFileSync('./config/config.json', JSON.stringify(config, null, "\t"))
            throw "Config file requires attention! There are new variables that were not there before"
        }
    }

    return config;
}

export function saveConfig(config: Config): void {
    if (!fs.existsSync('./config')) {
        fs.mkdirSync('./config')
    }

    fs.writeFileSync('./config/config.json', JSON.stringify(config, null, "\t"))
}