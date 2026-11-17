plugins {
    `cpp-application`
}

dependencies {
    implementation("p")
}

application {
    targetMachines = listOf(
        machines.windows.x86, machines.windows.x86_64)
}