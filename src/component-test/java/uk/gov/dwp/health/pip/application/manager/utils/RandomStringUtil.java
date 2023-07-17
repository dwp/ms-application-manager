package uk.gov.dwp.health.pip.application.manager.utils;

import java.security.SecureRandom;

public class RandomStringUtil {
    static final String AB = "0123456789abcdef";
    static SecureRandom rnd = new SecureRandom();

    public static String generate(int len){
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
}
