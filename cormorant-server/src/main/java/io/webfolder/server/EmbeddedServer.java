/**
 * cormorant - Object Storage Server
 * Copyright © 2017 WebFolder OÜ (support@webfolder.io)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import io.webfolder.server.command.Password;
import io.webfolder.server.command.Start;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;

public class EmbeddedServer implements ExitCodes {

    public static void main(String[] args) {
        Cormorant cormorant = new Cormorant();
        CommandLine commandLine = new CommandLine(cormorant);
        commandLine.registerConverter(Path.class, value -> get(value));
        List<CommandLine> parsed = emptyList();
        try {
            parsed = commandLine.parse(args);
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
            } else if (Password.class.equals(command.getClass())) {
                ((Password) command).generate();
            } else if (Help.class.equals(command.getClass())) {
                ((Help) command).help();
            }
        } else {
            commandLine.usage(err, AUTO);
            exit(INVALID_ARG);
        }
    }
}
