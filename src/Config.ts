import * as fs from 'fs'

interface ConfigInterface {
    token: string,
    defaultColors: {
        success: string,
        error: string
    }
}

const defaultConfig = {
    token: "NULL",
    defaultColors: {
        success: "#4da2b7",
        error: "#bc2727"
    }
}

export default function getConfig(): ConfigInterface {
    if (!fs.existsSync('./config')) {
        fs.mkdirSync('./config')
    }

    if (!fs.existsSync('./config/config.json')) {
        fs.writeFileSync('./config/config.json', JSON.stringify(defaultConfig))
        throw "Please go fill out the config file!"
    }
    return JSON.parse(fs.readFileSync('./config/config.json', 'utf-8'));
}