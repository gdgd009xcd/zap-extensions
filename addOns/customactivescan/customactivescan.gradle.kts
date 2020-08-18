import org.zaproxy.gradle.addon.AddOnStatus

version = "0.0.1"
description = "a Active Scanner with custmizable rules"

zapAddOn {
    addOnName.set("Custom Active scanner rules")
    addOnStatus.set(AddOnStatus.ALPHA)
    zapVersion.set("2.7.0")

    manifest {
        author.set("gdgd009xcd")
    }
}

dependencies {
    implementation("com.googlecode.java-diff-utils:diffutils:1.2.1")
    implementation("org.bitbucket.mstrobel:procyon-compilertools:0.5.25")

    testImplementation(project(":testutils"))
    testImplementation("org.apache.commons:commons-lang3:3.9")
}

tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    artifacts {
        archives(sourcesJar)
        //archives(jar)
    }
}

spotless {
    java {
        clearSteps()
        // Don't enforce 
        targetExclude("**/*.java")
    }
}