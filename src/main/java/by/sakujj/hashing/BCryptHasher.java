package by.sakujj.hashing;

import org.mindrot.jbcrypt.BCrypt;


public class BCryptHasher implements Hasher{
    private final static int LOG_ROUNDS = 10;

    private static final Hasher INSTANCE = new BCryptHasher();

    private BCryptHasher() {
    }

    public static Hasher getInstance() {
        return BCryptHasher.INSTANCE;
    }

    @Override
    public String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(LOG_ROUNDS));
    }

    @Override
    public boolean verifyHash(String candidatePassword, String storedHash) {
        return BCrypt.checkpw(candidatePassword, storedHash);
    }
}
