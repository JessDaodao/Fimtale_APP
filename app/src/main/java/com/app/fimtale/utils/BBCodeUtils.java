package com.app.fimtale.utils;

public class BBCodeUtils {

    public static String parseBBCode(String text) {
        if (text == null) return "";

        text = text.replaceAll("\\[b\\]", "<b>")
                .replaceAll("\\[/b\\]", "</b>")
                .replaceAll("\\[i\\]", "<i>")
                .replaceAll("\\[/i\\]", "</i>")
                .replaceAll("\\[u\\]", "<u>")
                .replaceAll("\\[/u\\]", "</u>")
                .replaceAll("\\[s\\]", "<strike>")
                .replaceAll("\\[/s\\]", "</strike>")
                .replaceAll("\\[center\\]", "<div align=\"center\">")
                .replaceAll("\\[/center\\]", "</div>")
                .replaceAll("\\[quote\\]", "<blockquote>")
                .replaceAll("\\[/quote\\]", "</blockquote>")
                .replaceAll("\\[list\\]", "<ul>")
                .replaceAll("\\[/list\\]", "</ul>")
                .replaceAll("\\[\\*\\]", "<li>");

        text = text.replaceAll("\\[url=(.*?)\\](.*?)\\[/url\\]", "<a href=\"$1\">$2</a>");
        text = text.replaceAll("\\[url\\](.*?)\\[/url\\]", "<a href=\"$1\">$1</a>");

        text = text.replaceAll("\\[img\\](.*?)\\[/img\\]", "<img src=\"$1\">");

        text = text.replaceAll("\\[color=(.*?)\\](.*?)\\[/color\\]", "<font color=\"$1\">$2</font>");

        text = text.replaceAll("\\[size=(.*?)\\](.*?)\\[/size\\]", "<font size=\"$1\">$2</font>");

        text = text.replaceAll("\n", "<br>");

        return text;
    }
}
