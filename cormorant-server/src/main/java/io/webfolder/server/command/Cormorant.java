package io.webfolder.server.command;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    version     = "1.0.0",
    footer      = "\n2017 Copyright(c) WebFolder OÜ - https://webfolder.io",
    name        = "cormorant",
    sortOptions = false,
    description = "cormorant is an object storage server released under AGPL.",
    subcommands = {
                    Start.class,
                    Password.class,
                    Help.class
                },
    showDefaultValues = true
)
public class Cormorant {

    @Option(names = { "-v", "--version" }, versionHelp = true, description = "Print version information and exit.")
    private boolean version;

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Display this help message.")
    private boolean help;

    public boolean isVersion() {
        return version;
    }

    public boolean isHelp() {
        return help;
    }
}
