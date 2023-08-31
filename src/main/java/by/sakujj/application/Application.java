package by.sakujj.application;

import by.sakujj.application.components.AuthenticationApplication;
import by.sakujj.application.components.BankApplication;
import by.sakujj.application.components.FileProcessorApplication;
import by.sakujj.context.ApplicationContext;
import by.sakujj.dto.ClientResponse;

public class Application {

    private static final String YAML_FILE = "percentToAdd.yml";
    private static final FileProcessorApplication fileProcessorApplication
            = new FileProcessorApplication(YAML_FILE);

    public static void run() {
        BankApplication bankApplication;
        ClientResponse authenticatedClient;
        while ((authenticatedClient = AuthenticationApplication.authenticateOrRegister())
                !=
                null) {
            bankApplication = new BankApplication(authenticatedClient);
            bankApplication.start();
        }

        ApplicationContext.cleanup(ApplicationContext.getInstance());
    }
}

