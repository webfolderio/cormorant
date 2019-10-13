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
