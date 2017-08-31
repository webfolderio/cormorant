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

import static io.webfolder.server.CommandLine.usage;
import static java.lang.System.out;

import java.io.PrintStream;

import io.webfolder.server.CommandLine.Command;
import io.webfolder.server.CommandLine.Option;

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
