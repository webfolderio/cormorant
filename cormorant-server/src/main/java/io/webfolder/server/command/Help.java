package io.webfolder.server.command;

import static java.lang.System.out;
import static picocli.CommandLine.usage;

import java.io.PrintStream;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "help",
    description = "Print help for the corresponding command."
)
public class Help {

    @Option(names = { "-c", "--command" }, description = "Print command help (Usage: help -c start)")
    private String command;

    public void help() {
        help(out, command);
    }

    public void help(PrintStream ps, String command) {
        if ("start".equals(command)) {
            usage(new Start(), ps);
        } else {
            usage(this, ps);
        }
    }
}
