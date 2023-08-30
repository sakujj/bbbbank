package by.sakujj.application.components;

import by.sakujj.context.ApplicationContext;
import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.services.AuthenticationService;
import by.sakujj.services.AuthenticationServiceImpl;
import by.sakujj.services.ClientService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationApplication {
    private static final ApplicationContext context = ApplicationContext.getInstance();

    private static final AuthenticationService authenticationService = context.getByClass(AuthenticationService.class);
    private static final ClientService clientService = context.getByClass(ClientService.class);

    public static ClientResponse authenticateOrRegister() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            Optional<ClientResponse> client = Optional.empty();
            while (client.isEmpty()) {
                System.out.println("Type 'a' to Authenticate, 'r' to Register, 'q' to Quit");
                String input = reader.readLine();
                switch (input) {
                    case "r" -> client = register(reader);
                    case "a" -> client = authenticate(reader);
                    case "q" -> {
                        return null;
                    }
                }
            }

            ClientResponse loggedClient = client.get();

            System.out.printf("Welcome %s, %s%n",
                    loggedClient.getUsername(),
                    loggedClient.getEmail());
            return loggedClient;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Optional<ClientResponse> register(BufferedReader reader) throws IOException {
        System.out.println("Register: ");

        System.out.print("\tusername: ");
        String username = reader.readLine();

        System.out.print("\temail: ");
        String email = reader.readLine();

        System.out.print("\tpassword: ");
        String password = reader.readLine();

        clientService.save(ClientRequest.builder()
                .username(username)
                .email(email)
                .notHashedPassword(password)
                .build());

        return clientService.findByEmail(email);
    }

    private static Optional<ClientResponse> authenticate(BufferedReader reader) throws IOException {
        System.out.println("Authenticate: ");

        System.out.print("\temail: ");
        String email = reader.readLine();

        System.out.print("\tpassword: ");
        String password = reader.readLine();

        AuthenticationService.Credentials credentials = new AuthenticationService.Credentials(email, password);
        Optional<ClientResponse> client = authenticationService.authenticate(credentials);
        if (client.isEmpty()) {
            System.out.println("(!) Oops, wrong credentials were entered");
        }
        return client;
    }
}
