package io.webfolder.server.command;

import static org.mindrot.jbcrypt.BCrypt.gensalt;
import static org.mindrot.jbcrypt.BCrypt.hashpw;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "password",
    description = "Generate bcrypt password."
)
public class Password {

    @Option(names = { "-s", "--password" }, description = "Clear text password.", paramLabel = "<string>", required = true)
    private String password;

    public void generate() {
        if ( password != null && ! password.trim().isEmpty() ) {
            String hash = hashpw(password, gensalt(12));
            System.out.println(hash);
        }
    }
}
