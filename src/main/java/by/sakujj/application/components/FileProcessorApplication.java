package by.sakujj.application.components;

import by.sakujj.context.ApplicationContext;
import by.sakujj.services.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
public class FileProcessorApplication {
    private static final ApplicationContext context = ApplicationContext.getInstance();

    private final Yaml yamlFile;
    private final Timer timer = new Timer();
    private final AccountService accountService = context.getByClass(AccountService.class);
    private final String yamlFileName;

    private class ListConstructor<T> extends Constructor {
        private final Class<T> clazz;

        public ListConstructor(final Class<T> clazz) {
            super(new LoaderOptions());
            this.clazz = clazz;
        }

        @Override
        protected Object constructObject(final Node node) {
            if (node instanceof SequenceNode && isRootNode(node)) {
                ((SequenceNode) node).setListType(clazz);
            }
            return super.constructObject(node);
        }

        private boolean isRootNode(final Node node) {
            return node.getStartMark().getIndex() == 0;
        }
    }

    public void stop() {
        timer.cancel();
    }
    public FileProcessorApplication(String yamlFileName) {
        var ctr = new ListConstructor<>(AccountIdToPercentage.class);
        yamlFile = new Yaml(ctr);
        this.yamlFileName = yamlFileName;



        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream("./yamlFiles/" + yamlFileName);
                    Iterable<Object> list = yamlFile.loadAll(inputStream);

                    List<AccountIdToPercentage> accountIdsToPercentage = (List<AccountIdToPercentage>) list.iterator().next();
                    accountIdsToPercentage.forEach(x -> accountService.updateMoneyAmountByPercentage(x.percent, x.accountId));
                } catch (Exception e) {
                  log.error("INCORRECT yaml file {} FORMAT or the file does not exist", yamlFileName);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

        }, 0, 1000 * 30);

    }

}

