# Rift

This is a Discord bot mostly focused on music, with support for Youtube, SoundCloud and Spotify (and others).
Feature suggestions and bug reports are appreciated

## Installation

### Requirement

- Java 21
- Discord Developer application token
- (Optional) Spotify Developer tokens

### Get started

- You can download the latest release [here](https://github.com/ItsOnlyGame/Rift/releases/latest).
- And just run it in a terminal `java -jar Rift-x.x.x.jar`
- When ran the first time it should quit automatically and create a Settings file in the same directory, open it and put in your discord token and your spotify token, now it should run normally

This bot should work on Windows, Linux and Mac system

## Application Commands

This bot uses slash commands.  
Use '/' and the desired command, for example: `/play https://youtu.be/dQw4w9WgXcQ`  
All commands and what they do can be found after using typing `/` in a discord text channel

### Music Commands

#### Play

- **Alias:** `play`
- **Usage:** `/play [track url / search word]`
- Searches tracks from Youtube, SoundCloud and Spotify
- Play track

#### Clear Queue

- **Alias:** `clearqueue`
- Leaves the current voice channel

#### Resume

- **Alias:** `resume`
- Resumes the current track paused

#### Pause

- **Alias:** `pause`
- Pauses the current track

#### Track Info

- **Alias:** `info`
- Gives info about the track playing

#### Queue

- **Alias:** `queue`
- Gets the queue

#### Remove

- **Alias:** `remove`
- **Usage:** `/remove [queue index]`. You can get the queue index with the queue command
- Remove item from queue

#### Stop

- **Alias:** `stop`
- Stops playing music and clears the queue

#### Skip

- **Alias:** `skip`
- **Usage:** `/skip [index]`. Amount of tracks to skip
- Skip(s) track

### Voice Connection Commands

#### Join

- **Alias:** `join`
- Joins a voice channel

#### Leave

- **Alias:** `leave`
- Leaves the current voice channel

### Other

#### Bot Info

- **Alias:** `botinfo`
- Gives some small information about the bot

## License

Released under the MIT license.
