package io.webfolder.server.command;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import static java.lang.System.out;

@Command(
    name = "version",
    description = "Print version information and exit."
)
public class Version {

    public void version(CommandLine commandLine) {
        commandLine.printVersionHelp(out);
    }
}
