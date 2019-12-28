import org.zaproxy.gradle.addon.AddOnStatus

version = "34"
description = "Modified The release quality Active Scanner rules"

zapAddOn {
    addOnName.set("Active scanner rules")
    addOnStatus.set(AddOnStatus.RELEASE)
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

spotless {
    java {
        clearSteps()
        // Don't enforce 
        targetExclude("**/*.java")
    }
}