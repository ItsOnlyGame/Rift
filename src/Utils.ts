import * as fs from 'fs'

function getVersion(): string {
    const data = fs.readFileSync('./package.json', 'utf-8');
    var parsedData = JSON.parse(data);
    return parsedData.version;
}

export default {
    "Version": getVersion()
}