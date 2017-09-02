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

import static java.lang.System.exit;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.readAllBytes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class ShutdownTask extends TimerTask implements ExitCodes {

    private final String pid;

    private final Path pidFile;

    private final CountDownLatch latch;

    public ShutdownTask(String pid, Path pidFile, CountDownLatch latch) {
        this.pid = pid;
        this.pidFile = pidFile;
        this.latch = latch;
    }

    @Override
    public void run() {
        if ( ! exists(pidFile) ) {
            System.out.println("Server terminated successfully.");
            latch.countDown();
            return;
        }
        try {
            String currentPid = new String(readAllBytes(pidFile));
            if ( ! pid.equals(currentPid) ) {
                System.out.println("Server terminated successfully.");
                latch.countDown();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            exit(UNABLE_TO_READ_PID_FILE);
            return;
        }
    }
}
