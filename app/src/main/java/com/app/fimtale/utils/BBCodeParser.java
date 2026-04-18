package com.app.fimtale.utils;

public class BBCodeParser {

    public static String parse(String text) {
        if (text == null) return null;
        
        String html = text;
        
        html = html.replaceAll("(?is)\\[b\\](.*?)\\[/b\\]", "<b>$1</b>");
        html = html.replaceAll("(?is)\\[i\\](.*?)\\[/i\\]", "<i>$1</i>");
        html = html.replaceAll("(?is)\\[u\\](.*?)\\[/u\\]", "<u>$1</u>");
        html = html.replaceAll("(?is)\\[s\\](.*?)\\[/s\\]", "<s>$1</s>");
        
        html = html.replaceAll("(?is)\\[color=(.*?)\\](.*?)\\[/color\\]", "<span style=\"color:$1;\">$2</span>");
        html = html.replaceAll("(?is)\\[size=(.*?)\\](.*?)\\[/size\\]", "<span style=\"font-size:$1;\">$2</span>");
        
        html = html.replaceAll("(?is)\\[url=(.*?)\\](.*?)\\[/url\\]", "<a href=\"$1\">$2</a>");
        html = html.replaceAll("(?is)\\[url\\](.*?)\\[/url\\]", "<a href=\"$1\">$1</a>");
        
        html = html.replaceAll("(?is)\\[img\\](.*?)\\[/img\\]", "<img src=\"$1\"/>");
        html = html.replaceAll("(?is)\\[img=(.*?)\\](.*?)\\[/img\\]", "<img src=\"$1\" alt=\"$2\"/>");
        
        html = html.replaceAll("(?is)\\[quote\\](.*?)\\[/quote\\]", "<blockquote>$1</blockquote>");
        html = html.replaceAll("(?is)\\[quote=(.*?)\\](.*?)\\[/quote\\]", "<blockquote><b>$1</b><br/>$2</blockquote>");
        
        html = html.replaceAll("(?is)\\[center\\](.*?)\\[/center\\]", "<div align=\"center\">$1</div>");
        html = html.replaceAll("(?is)\\[right\\](.*?)\\[/right\\]", "<div align=\"right\">$1</div>");
        html = html.replaceAll("(?is)\\[left\\](.*?)\\[/left\\]", "<div align=\"left\">$1</div>");
        
        html = html.replaceAll("(?i)\\[/?[a-z0-9_-]+(?:=[^\\]]*)?\\](?![\\(\\[])", "");
        
        return html;
    }
}
