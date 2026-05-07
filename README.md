<p align="center">
  <img src="public/logo.png" alt="Pandus Logo" width="128" height="128">
</p>

<h1 align="center">Pandus</h1>

<p align="center">
    Multipurpose open source Discord bot which handles all sort of things - from basic moderation, playing music on channel to automatic handling events.
</p>

---

<p align="center">
  <a href="https://github.com/Wardiusz/Pandus/releases">
    <img src="https://img.shields.io/github/v/release/wardiusz/pandus?style=flat-square" alt="Latest Release">
  </a>
  <a href="https://github.com/Wardiusz/Pandus/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/wardiusz/pandus?style=flat-square" alt="License">
  </a>
</p>

## Description

Pandus is a Discord bot written in Java using JDA. 
It supports multi-source music playback (YouTube, Spotify via LavaSrc), 
a SQLite-backed persistence layer with HikariCP connection pooling, 
and a command framework built on JDA-Chewtils. 
The source code is published here for transparency and open contribution — 
the bot itself runs as a hosted instance but feel free to private host and use.

## Demo
You can test and use bot by adding to your Discord server via [invite link](https://discord.com/oauth2/authorize?client_id=1106275577683529858&permissions=8&scope=bot+applications.commands).

## Tech Stack

| Tech       | Purpose                     |
|------------|-----------------------------|
| Java 25+   | Main runtime                |
| JDA        | Discord bot framework       |
| Gradle     | Build system                |
| SLF4J      | Logging                     |
| LavaPlayer | Audio playback              |
| SQLite     | Database storage            |

## External Requirements
* Discord Bot Token [Discord Developer Portal](https://discord.com/developers/applications))

## Help

If the bot fails to connect, double-check that your `.env` file exists and contains a valid `TOKEN` value.

If you encounter audio issues on Windows, ensure the JDAVE native library (`jdave-native-win-x86-64`) is on the classpath — it is bundled automatically via the shadow JAR.
For general questions, open a [GitHub Issue](https://github.com/wardiusz/Pandus/issues) page or describe your problem to our community on [Discord server](https://discord.gg/7nWWgZjhZk).

## Version History

* 1.0
  * Initial release

## Contributing

Pull requests are welcome! Feel free to open issues or submit pull requests.

## License

GNU License  
See `LICENSE` file for details.

## Acknowledgements

* [JDA](https://github.com/discord-jda/JDA) — Java Discord API
* [LavaPlayer](https://github.com/lavalink-devs/lavaplayer) — Audio streaming engine
* [youtube-source](https://github.com/lavalink-devs/youtube-source) — YouTube audio source
* [LavaSrc](https://github.com/topi314/LavaSrc) — YouTube, Spotify & more
* [JDA-Chewtils](https://github.com/Chew/JDA-Chewtils) — Command framework
* [JDAVE](https://github.com/MinnDevelopment/jdave) — Native voice support
* [HikariCP](https://github.com/brettwooldridge/HikariCP) — Database connection pool
* [dotenv-java](https://github.com/cdimascio/dotenv-java) — Environment config
