package com.znv.fssrqs.util;

public class LocalUserUtil {
    public static ThreadLocal<String> localUser = new ThreadLocal();

    public static String getLocalUserId(){
        return localUser.get();
    }

    public static void setLocalUserId(String userId) {
        localUser.set(userId);
    }

    public static void removeLocalUserId() {
        localUser.remove();
    }
}
