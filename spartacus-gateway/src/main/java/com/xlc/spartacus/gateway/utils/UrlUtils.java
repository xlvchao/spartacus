package com.xlc.spartacus.gateway.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class UrlUtils {
    public static Boolean isStaticResource(String uriPath) {
        if(StringUtils.isNotBlank(uriPath) && (
                Pattern.matches(".*?\\.psd", uriPath)
                        || Pattern.matches(".*?\\.swf", uriPath)
                        || Pattern.matches(".*?\\.bmp", uriPath)
                        || Pattern.matches(".*?\\.jsp", uriPath)
                        || Pattern.matches(".*?\\.lst", uriPath)
                        || Pattern.matches(".*?\\.ico", uriPath)
                        || Pattern.matches(".*?\\.aspx", uriPath)
                        || Pattern.matches(".*?\\.otf", uriPath)
                        || Pattern.matches(".*?\\.php", uriPath)
                        || Pattern.matches(".*?\\.asp", uriPath)
                        || Pattern.matches(".*?\\.jpg", uriPath)
                        || Pattern.matches(".*?\\.css", uriPath)
                        || Pattern.matches(".*?\\.svg", uriPath)
                        || Pattern.matches(".*?\\.gif", uriPath)
                        || Pattern.matches(".*?\\.ttf", uriPath)
                        || Pattern.matches(".*?\\.png", uriPath)
                        || Pattern.matches(".*?\\.js", uriPath)
                        || Pattern.matches(".*?\\.eot", uriPath)
                        || Pattern.matches(".*?\\.woff", uriPath)
                        || Pattern.matches(".*?\\.json", uriPath)
                        || Pattern.matches(".*?\\.html", uriPath)
                        || Pattern.matches(".*?\\.woff2", uriPath)
        )) {
            return true;
        }

        return false;
    }

//    public static void main(String[] args) {
//        System.out.println(isStaticResource("/comment/getRecentComments"));
//    }
}
