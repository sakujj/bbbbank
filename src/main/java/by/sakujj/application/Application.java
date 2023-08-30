package by.sakujj.application;

import by.sakujj.application.components.AuthenticationApplication;
import by.sakujj.application.components.BankApplication;
import by.sakujj.context.ApplicationContext;
import by.sakujj.dto.ClientResponse;

public class Application {

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

