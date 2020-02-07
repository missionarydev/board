# board [![](https://jitpack.io/v/missionarydev/board.svg)](https://jitpack.io/#missionarydev/board)
A clean and functional scoreboard api for use with Bukkit/Spigot plugins. The objective of board is to allow for easy creation 
of scoreboard systems without the hassle of the included api's in Bukkit.

## Usage
board can be easily added to your project with the use of Apache Maven.

#### Requirements of board
* [Java 8+](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (usage of Java 8 specific features is heavy)
* [Project Lombok](https://projectlombok.org/) Any version after 1.16.20 shall suffice
* Bukkit/Spigot (up to the user where they find it)
* [Git SCM](https://git-scm.com/downloads)
* [Apache Maven 3](http://maven.apache.org/download.html)

**It is recommended that you use the [jitpack](https://jitpack.io/#missionarydev/board)**

After you have all of the requirements installed & configured you can use the following series of commands to download and install
board to your local maven repo (.m2)
```
git clone https://github.com/missionarydev/board.git
cd board
mvn clean install
```
board will now have been added to your local maven repo (.m2) and you can begin using it by adding the following into your Maven pom.xml
```xml
<dependency>
    <groupId>me.missionary</groupId>
    <artifactId>board</artifactId>
    <version>1.1.0-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```
After you have added the dependency to your Maven pom.xml the last thing you need to do is register the BoardManager and instance of BoardProvider
with your plugin!
```java
public class ExampleUsage extends JavaPlugin {

    private BoardManager manager;

    @Override
    public void onEnable() {
        manager = new BoardManager(this, BoardSettings.builder().boardProvider(new ExampleProviderImplementation()).scoreDirection(ScoreDirection.UP).build());
    }

    @Override
    public void onDisable() {
        manager.onDisable();
    }
}

```

## Functionality
board provides support for creating simple and elegant scoreboards.

Here is an example of how to create a scoreboard with board.
```java
public class ExampleProviderImplementation implements BoardProvider {

        @Override
        public String getTitle(Player player) {
            return ChatColor.LIGHT_PURPLE + "Board";
        }

        @Override
        public List<String> getLines(Player player) {
            List<String> lines = new ArrayList<>();
            lines.add("&7&m-----------------");
            lines.add(ChatColor.LIGHT_PURPLE + "Name" + ChatColor.GRAY + ": " + ChatColor.YELLOW + player.getName());
            lines.add(ChatColor.LIGHT_PURPLE + "Health" + ChatColor.GRAY + ": " + ChatColor.YELLOW + String.format("%.1f\u2764", Math.ceil(player.getHealth()) / 2.0));
            lines.add(ChatColor.LIGHT_PURPLE + "Hunger" + ChatColor.GRAY + ": " + ChatColor.YELLOW + player.getFoodLevel() / 2);
            lines.add("&7&m-----------------");
            return lines;
        }
    }
```
The previous code will produce this result: 

![](https://i.imgur.com/hI0I3Nx.png)
