package io.webfolder.server.command;

import static java.lang.System.setProperty;
import static org.pmw.tinylog.Configurator.defaultConfig;
import static org.pmw.tinylog.Level.ERROR;
import static org.pmw.tinylog.Level.INFO;

import org.pmw.tinylog.writers.ConsoleWriter;

import io.webfolder.cormorant.api.CormorantServer;
import io.webfolder.server.EmbeddedServer;
import picocli.CommandLine.Command;

@Command(
    name = "stop",
    description = "Stop object storage server."
)
public class Stop {

    public void start() {
        setProperty("org.jboss.logging.provider", "slf4j");

        defaultConfig()
                .writer(new ConsoleWriter())
                .level(ERROR)
                .level(EmbeddedServer.class, INFO)
                .level(CormorantServer.class, INFO)
                .formatPattern("{{level}|min-size=8} {date} {message}")
            .activate();
    }
}
