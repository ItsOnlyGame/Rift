import * as fs from 'fs'

interface ConfigInterface {
    token: string,
    bot_id: string,
    defaultColors: {
        success: string,
        error: string
    },
    spotify: {
        clientId: string,
        clientSecret: string
    },
    enableSlashCommands: string
}

const defaultConfig = {
    token: null,
    bot_id: null,
    defaultColors: {
        success: "#4da2b7",
        error: "#bc2727"
    },
    spotify: {
        clientId: null,
        clientSecret: null
    },
    enableSlashCommands: false
}

export default function getConfig(): ConfigInterface {
    if (!fs.existsSync('./config')) {
        fs.mkdirSync('./config')
    }

    if (!fs.existsSync('./config/config.json')) {
        fs.writeFileSync('./config/config.json', JSON.stringify(defaultConfig))
        throw "Please go fill out the config file!"
    }

    var config = JSON.parse(fs.readFileSync('./config/config.json', 'utf-8'));

    for (var c in defaultConfig) {
        if (config[c] == undefined) {
            config[c] = null;
            fs.writeFileSync('./config/config.json', JSON.stringify(config))
            throw "Config file requires attention! There are new variables that were not there before"
        }
    }

    return config;
}