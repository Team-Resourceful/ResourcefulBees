# Resourceful Bees
<hr>

Resourceful Bees is a Minecraft mod that provides a feature rich framework API for players and Modpack Developers to create their own customized bees.

### Wiki

TODO: Add wiki link when gitbook wiki is ready

### License and Availability

The mod is licensed under LGPL 3.0 and is available on [Curseforge](https://www.curseforge.com/minecraft/mc-mods/resourceful-bees) and [Modrinth](https://modrinth.com/mod/resourceful-bees).

### Contributions

If you would like to contribute to the mod feel free to submit a PR.
<br>TODO: Add more info about importing the project in IntelliJ and any additional setup required.

## For Mod Developers
<hr>

Be sure to add our maven to your `build.gradle`:
```gradle
repositories {
    maven { url = "https://maven.resourcefulbees.com/repository/maven-public/" }
    <--- other repositories here --->
}
```
You can then add our mod as a dependency:

Forge:
```gradle
dependencies {
    <--- Other dependencies here --->

    implementation fg.deobf("com.teamresourceful.resourcefulbees:resourcefulbees-forge-1.19.2:1.0.0-alpha.17")
    
    //below are required dependencies for resourceful bees to work.
    runtimeOnly fg.deobf("com.teamresourceful.resourcefullib:resourcefullib-forge-1.19.2:1.1.23")
    runtimeOnly fg.deobf("com.teamresourceful.resourcefulconfig:resourcefulconfig-forge-1.19.2:1.0.20")
    runtimeOnly fg.deobf("software.bernie.geckolib:geckolib-forge-1.19:3.1.40")
    runtimeOnly fg.deobf("net.roguelogix.phosphophyllite:Phosphophyllite:0.6.0-beta.7")
}
```

TODO: Update this to include Architectury and Fabric