plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "meridian"
version = "0.1.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

intellij {
    version.set("2024.1")
    type.set("IC")
    plugins.set(listOf())
}

tasks {
    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("252.*")
        changeNotes.set(
            """
            Initial Meridian IntelliJ MVP:
            <ul>
              <li>Registers Meridian actions in the IDE.</li>
              <li>Adds Meridian settings.</li>
              <li>Adds Meridian findings tool window.</li>
              <li>Calls local meridian-mcp over stdio JSON-RPC.</li>
            </ul>
            """.trimIndent()
        )
    }

    buildSearchableOptions {
        enabled = false
    }

    test {
        useJUnitPlatform()
    }
}