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
package io.webfolder.server.command;

import static java.lang.System.out;
import static picocli.CommandLine.usage;

import java.io.PrintStream;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = "help",
    description = "Print help for the corresponding command."
)
public class Help {

    @Parameters(description = "Print command help (Usage: help [start|stop|version])")
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
