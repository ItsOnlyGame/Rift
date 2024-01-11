# Rift

This is a Discord bot mostly focused on music, with support for Youtube, SoundCloud and Spotify (and others).
Feature suggestions and bug reports are appreciated

## Installation

### Requirement

- Java 11
- Discord Developer application token
- (Optional) Spotify Developer tokens

### Get started

- You can download the latest release [here](https://github.com/ItsOnlyGame/Rift/releases/latest).
- And just run it in a terminal `java -jar Rift-x.x.x.jar`
- When ran the first time it should quit automatically and create a Settings file in the same directory, open it and put in your discord token and your spotify token, now it should run normally
- Default prefix for the bot is `r!`

This bot should work on Windows, Linux and Mac system

# Application Commands

This bot uses slash commands.  
Use '/' and the desired command, for example: `/play https://youtu.be/dQw4w9WgXcQ`  
All commands and what they do can be found after using typing `/` in a discord text channel

## Old school Commands

I'm just assuming the prefix is default: `r!`

### Music Commands

#### Play

- **Aliases:** `play`, `p`
- **Usage:** `r!play [track url / search word]`
- Searches tracks from Youtube, SoundCloud and Spotify
- Play track

#### Spotify

- **Aliases:** `spotify`, `sp`
- Searches tracks from spotify

#### SoundCloud

- **Aliases:** `soundcloud`, `sc`
- Searches tracks from soundcloud

#### Lyrics

- **Aliases:** `lyrics`
- **Usage:** `r!lyrics [song title]` searches lyrics depending on the title given or just `r!lyrics` that will get the lyrics for the current playing track
- Finds lyrics to a song

#### Clear Queue

- **Aliases:** `clearqueue`, `clear`
- Leaves the current voice channel

#### Resume

- **Aliases:** `resume`
- Resumes the current track paused

#### Pause

- **Aliases:** `pause`
- Pauses the current track

#### Track Info

- **Aliases:** `info`
- Gives info about the track playing

#### Queue

- **Aliases:** `queue`, `q`
- Gets the queue

#### Remove

- **Aliases:** `remove`, `rm`
- **Usage:** `r!remove [queue index]`. You can get the queue index with the queue command
- Remove item from queue

#### Stop

- **Aliases:** `stop`
- Stops playing music and clears the queue

#### Skip

- **Aliases:** `skip`, `s`
- **Usage:** `r!skip [index]`. Amount of tracks to skip
- Skip(s) track

### Voice Connection Commands

#### Join

- **Aliases:** `join`, `connect`, `c`, `j`
- Joins a voice channel

#### Leave

- **Aliases:** `leave`, `disconnect`, `dc`, `dis`
- Leaves the current voice channel

### Admin Commands

#### Delete

- **Aliases:** `delete`, `del`
- **Usage:** `r!delete [amount]` deletes the give amount of messages. `r!delete` only deletes one message
- Deletes messages

#### Prefix

- **Aliases:** `prefix`
- Change prefix

### Other

#### Bot Info

- **Aliases:** `botinfo`, `bot`, `this`
- Gives some small information about the bot

#### Echo

- **Aliases:** `echo`
- Echos the text following the command

#### Help

- **Aliases:** `help`
- Gives small information about commands

## Notice

Application commands and old school commands might not be the same.  
So don't except to every command have a variation of the other.

## License

Released under the MIT license.