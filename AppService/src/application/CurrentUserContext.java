package application;

public class CurrentUserContext {
    private static int currentUserId;

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }
}
