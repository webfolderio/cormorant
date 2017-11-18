package io.webfolder.server.command;

import picocli.CommandLine.Command;

@Command(
    name = "version",
    description = "Print version information and exit."
)
public class Version {

    public void version() {
        System.out.println("1.0.0");
    }
}
