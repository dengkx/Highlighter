package com.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2019/3/23 0023.
 */
public class test1 {


    public static void main(String[] args) {
        String[] chapter = {"第一章", "第二章"};
        String[] section = {"第一节", "第二节"};
        Set<String> chapterSet = new HashSet<>(Arrays.asList(chapter));
        Set<String> sectionSet = new HashSet<>(Arrays.asList(section));
        String tt = "第一章  总则。\n" +
                "第一节  人名，内部的锚段。\n" +
                "第二节  好的，我们都知道的。";
        int wordLength = 0;//长度
        int wordIndex = -1;
        char wordChar = 0;
        String word;
        StringBuffer out = new StringBuffer();
        while (++wordIndex != tt.length()) {
            wordChar = tt.charAt(wordIndex);
            if (Character.isJavaIdentifierPart(wordChar)) {
                out.append(wordChar);
                ++wordLength;
                continue;
            }
            if (wordLength > 0) {
                word = out.substring(out.length() - wordLength);
                if (chapterSet.contains(word)) {
                    out.insert(out.length() - wordLength, "<p style=\"font-size:1.2em;padding-bottom:20px\">");
                } else if (sectionSet.contains(word)) {
                    out.insert(out.length() - wordLength, "</p><table border=0> <tr valign=top>  <td nowrap>");
                    out.append("&nbsp;</td>\n");
                }
            }
            out.append(wordChar);
            wordLength = 0;

        }
        if (wordIndex == tt.length()) {
            out.append("</p>");
        }
        System.out.println(out.toString());

    }
}
