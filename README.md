# Rift
## Overview
This is a Discord bot mostly focused on music, with support for Youtube, SoundCloud and Spotify (and others).
Feature suggestions and bug reports are appreciated

## Installation
- You need to have Java JRE 11 installed on your machine
- You can download the latest release [here](https://github.com/ItsOnlyGame/Rift/releases/latest). 
- And just run it in a terminal ```java -jar Rift-x.x.x.jar```
- When ran the first time it should quit automatically and create a Settings file in the same directory, open it and put in your discord token and your spotify token, now it should run normally
- Default prefix for the bot is ```r!```

This bot should work on Windows, Linux and Mac system


## Commands
I'm just assuming the prefix is default: ``r!``

### Voice Connection Commands
#### Join
-  **Aliases:** ``join``, ``connect``, ``c``, ``j``
- Joins a voice channel

#### Leave
- **Aliases:** ``leave``, ``disconnect``, ``dc``, ``dis``
- Leaves the current voice channel

### Music Commands
#### Clear Queue
- **Aliases:** ``clearqueue``, ``clear``
- Leaves the current voice channel

#### Lyrics
- **Aliases:** ``lyrics``
- **Usage:** ``r!lyrics [song title]``. If no song title is give then it will get the lyrics for the current playing track
- Finds lyrics to a song

#### Pause
- **Aliases:** ``pause``
- Pauses the current track

#### Play
- **Aliases:** ``play``, ``p``
- **Usage:** ``r!play [track url / search word]``
- Searches tracks from Youtube, SoundCloud and Spotify
- Play track

#### Track Info
- **Aliases:** ``info``
- Gives info about the track playing

#### Queue
- **Aliases:** ``queue``, ``q``
- Gets the queue

#### Remove
- **Aliases:** ``remove``, ``rm``
- **Usage:** ``r!remove [queue index]``. You can get the queue index with the queue command
- Remove item from queue

#### Resume
- **Aliases:** ``resume``
- Resumes the current track paused

#### Skip
- **Aliases:** ``skip``, ``s``
- **Usage:** ``r!skip [index]``. Amount of tracks to skip
- Skip(s) track

**NOT FINNISHED!**


### Admin Commands
**NOT FINNISHED!**


## License
Released under the [GNU GPL v3](https://www.gnu.org/licenses/gpl-3.0.en.html) license.
