plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

version = "1.5.4"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.39"))
    compileOnly("com.intellectualsites.plotsquared:plotsquared-core")
    compileOnly("com.intellectualsites.plotsquared:plotsquared-bukkit") { isTransitive = false }
    implementation("org.apache.commons:commons-lang3:3.0")
}

tasks {
    compileJava {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
        options.encoding = "UTF-8"
    }

    jar {
        dependsOn("shadowJar")
    }

    shadowJar {
        if (project.hasProperty("cd"))
            archiveFileName.set("FeaturedPlots.jar")
        else
            archiveFileName.set("FeaturedPlots-${archiveVersion.getOrElse("unknown")}.jar")

        destinationDirectory.set(file(System.getenv("outputDir") ?: "$rootDir/build/"))

        minimize()
    }
}

bukkit {
    name = "FeaturedPlots"
    main = "fr.aerwyn81.featuredplots.FeaturedPlots"
    authors = listOf("AerWyn81")
    apiVersion = "1.13"
    description = "Explore and discover the most beautiful plots highlighted on your server"
    softDepend = listOf("PlotSquared")
    version = project.version.toString()

    commands {
        register("featuredplots") {
            description = "Plugin command"
            aliases = listOf("fp")
        }
    }
}