package com.kuaidao.manageweb.util;

import java.util.regex.Pattern;


public class XssUtil {
    private final static Pattern PATTERN1 =
            Pattern.compile("<[\r\n| | ]*script[\r\n| | ]*>(.*?)</[\r\n| | ]*script[\r\n| | ]*>",
                    Pattern.CASE_INSENSITIVE);
    private final static Pattern PATTERN2 =
            Pattern.compile("src[\r\n| | ]*=[\r\n| | ]*[\\\"|\\\'](.*?)[\\\"|\\\']",
                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private final static Pattern PATTERN3 =
            Pattern.compile("</[\r\n| | ]*script[\r\n| | ]*>", Pattern.CASE_INSENSITIVE);
    private final static Pattern PATTERN4 = Pattern.compile("<[\r\n| | ]*script(.*?)>",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private final static Pattern PATTERN5 = Pattern.compile("eval\\((.*?)\\)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private final static Pattern PATTERN6 = Pattern.compile("e-xpression\\((.*?)\\)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private final static Pattern PATTERN7 =
            Pattern.compile("javascript[\r\n| | ]*:[\r\n| | ]*", Pattern.CASE_INSENSITIVE);
    private final static Pattern PATTERN8 =
            Pattern.compile("vbscript[\r\n| | ]*:[\r\n| | ]*", Pattern.CASE_INSENSITIVE);
    private final static Pattern PATTERN9 = Pattern.compile("onload(.*?)=",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    public static String xssEncode(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        } else {
            s = stripXSSAndSql(s);
        }
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '>':
                    sb.append("＞");// 转义大于号
                    break;
                case '<':
                    sb.append("＜");// 转义小于号
                    break;
                case '\'':
                    sb.append("＇");// 转义单引号
                    break;
                case '\"':
                    sb.append("＂");// 转义双引号
                    break;
                case '&':
                    sb.append("＆");// 转义&
                    break;
                case '#':
                    sb.append("＃");// 转义#
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    public static String stripXSSAndSql(String value) {
        if (value != null) {
            value = PATTERN1.matcher(value).replaceAll("");
            // Avoid anything in a src="http://www.yihaomen.com/article/java/..." type of
            // e-xpression
            value = PATTERN2.matcher(value).replaceAll("");
            // Remove any lonesome </script> tag
            value = PATTERN3.matcher(value).replaceAll("");
            // Remove any lonesome <script ...> tag
            value = PATTERN4.matcher(value).replaceAll("");
            // Avoid eval(...) expressions
            value = PATTERN5.matcher(value).replaceAll("");
            // Avoid e-xpression(...) expressions
            value = PATTERN6.matcher(value).replaceAll("");
            // Avoid javascript:... expressions
            value = PATTERN7.matcher(value).replaceAll("");
            // Avoid vbscript:... expressions
            value = PATTERN8.matcher(value).replaceAll("");
            // Avoid onload= expressions
            value = PATTERN9.matcher(value).replaceAll("");
        }
        return value;
    }
}
