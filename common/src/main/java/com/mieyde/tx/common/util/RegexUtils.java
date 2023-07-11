package com.mieyde.tx.common.util;

import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.ReUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 我吃稀饭面
 * @date 2023/7/11 13:38
 */
public class RegexUtils {

    /**
     * 正则替换指定值 通过正则查找到字符串，然后把匹配到的字符串加入到replacementTemplate中，$1表示分组1的字符串
     * 例如：原字符串是：中文1234，我想把1234换成(1234)，则可以：
     *   ReUtil.replaceAll("中文1234", "(\\d+)", "($1)"))
     *
     *   结果：中文(1234)
     *
     * Params:
     * content – 文本 regex – 正则 replacementTemplate – 替换的文本模板，可以使用$1类似的变量提取正则匹配出的内容
     * Returns:
     * 处理后的文本
     */
    public static String replaceAll(CharSequence content, String regex, String replacementTemplate) {
        return ReUtil.replaceAll(content,regex,replacementTemplate);
    }

    public static String replaceAll(CharSequence content, Pattern pattern, String replacementTemplate) {
        return ReUtil.replaceAll(content,pattern,replacementTemplate);
    }

    public static String replaceAll(CharSequence str, String regex, Func1<Matcher, String> replaceFun) {
        return ReUtil.replaceAll(str,regex,replaceFun);
    }

    public static String replaceAll(CharSequence str, Pattern pattern, Func1<Matcher, String> replaceFun) {
        return ReUtil.replaceAll(str, pattern, replaceFun);
    }

    public static String replaceFirst(CharSequence content,Pattern pattern,  String replacement) {
        return ReUtil.replaceFirst(pattern, content, replacement);
    }

    public static String replaceFirst( CharSequence content,String regex, String replacement) {
        return ReUtil.replaceFirst(Pattern.compile(regex), content, replacement);
    }

    public static boolean isMatch(String regex, CharSequence content) {
        return ReUtil.isMatch(regex, content);
    }

    public static boolean isMatch(Pattern pattern, CharSequence content) {
        return ReUtil.isMatch(pattern, content);
    }

    public static String get(String regex, CharSequence content, int groupIndex) {
        return ReUtil.get(regex,content,groupIndex);
    }

    public static String get(String regex, CharSequence content, String groupName) {
        return ReUtil.get(regex,content,groupName);
    }

    public static String get(Pattern pattern, CharSequence content, int groupIndex) {
        return ReUtil.get(pattern,content,groupIndex);
    }

    public static String get(Pattern pattern, CharSequence content, String groupName) {
        return ReUtil.get(pattern,content,groupName);
    }

    public static String getGroup0(Pattern pattern, CharSequence content) {
        return ReUtil.getGroup0(pattern,content);
    }

    public static String getGroup1(Pattern pattern, CharSequence content) {
        return ReUtil.getGroup1(pattern,content);
    }

    public static String getGroup0(String regex, CharSequence content) {
        return ReUtil.getGroup0(regex,content);
    }

    public static String getGroup1(String regex, CharSequence content) {
        return ReUtil.getGroup1(regex,content);
    }

    public static List<String> findAll(String regex, CharSequence content, int group) {
        return ReUtil.findAll(regex, content, group);
    }

    public static List<String> findAll(Pattern pattern, CharSequence content, int group) {
        return ReUtil.findAll(pattern, content, group);
    }

    public static List<String> findAllGroup0(String regex, CharSequence content) {
        return ReUtil.findAllGroup0(regex, content);
    }

    public static List<String> findAllGroup1(String regex, CharSequence content) {
        return ReUtil.findAllGroup1(regex, content);
    }

    public static List<String> findAllGroup0(Pattern pattern, CharSequence content) {
        return ReUtil.findAllGroup0(pattern, content);
    }

    public static List<String> findAllGroup1(Pattern pattern, CharSequence content) {
        return ReUtil.findAllGroup1(pattern, content);
    }

}
