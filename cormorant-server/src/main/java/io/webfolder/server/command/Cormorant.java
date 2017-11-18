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

import picocli.CommandLine.Command;

@Command(
    version     = "1.0.0",
    header      = { "\r\ncormorant is an object storage server released under AGPL.\r\n" },
    footer      = "\r\nCopyright 2017 WebFolder OÜ",
    name        = "cormorant",
    sortOptions = false,
    customSynopsis = { "cormorant [start|stop|version|help] <arguments>\r\n" },
    subcommands = {
                Start.class,
                Stop.class,
                Version.class,
                Help.class,
    },
    showDefaultValues = true
)
public class Cormorant {
}
