/**
 * The MIT License
 * Copyright © 2017, 2019 WebFolder OÜ
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.webfolder.server;

import static java.lang.System.err;
import static java.lang.System.exit;
import static java.lang.System.out;
import static java.nio.file.Paths.get;
import static java.util.Collections.emptyList;
import static picocli.CommandLine.Help.Ansi.AUTO;

import java.nio.file.Path;
import java.util.List;

import io.webfolder.server.command.Cormorant;
import io.webfolder.server.command.ExitCodes;
import io.webfolder.server.command.Help;
import io.webfolder.server.command.Start;
import io.webfolder.server.command.Stop;
import io.webfolder.server.command.Version;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

public class Server implements ExitCodes {

    public static void main(String[] args) {
        Cormorant cormorant = new Cormorant();
        CommandLine commandLine = new CommandLine(cormorant);
        commandLine.registerConverter(Path.class, value -> get(value));
        List<CommandLine> parsed = emptyList();
        try {
            ParseResult result = commandLine.parseArgs(args);
            parsed = result.asCommandLineList();
        } catch (ParameterException e) {
            err.println(e.getMessage());
            if (args.length >= 1) {
                err.println();
                new Help().help(err, args[0]);
            }
            exit(INVALID_ARG);
            return;
        }
        if (commandLine.isVersionHelpRequested()) {
            commandLine.printVersionHelp(out, AUTO);
        } else if (parsed.size() >= 2) {
            Object command = parsed.get(1).getCommand();
            if (Start.class.equals(command.getClass())) {
                ((Start) command).start();
            } else if (Help.class.equals(command.getClass())) {
                ((Help) command).help();
            } else if (Stop.class.equals(command.getClass())) {
                ((Stop) command).stop();
            } else if (Version.class.equals(command.getClass())) {
                ((Version) command).version(commandLine);
            }
        } else {
            commandLine.usage(err, AUTO);
            exit(INVALID_ARG);
        }
    }
}
