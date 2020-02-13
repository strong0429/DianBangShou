package com.xingdhl.www.storehelper.utility;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leeyc on 2018/3/14.
 *
 */

public class PinyinUtil {
    public static boolean isEnglishCharacter(int c){
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    /**
     * 根据Unicode编码判断中文汉字和符号
     *
     */
    public static boolean isChineseCharacter(char c)
    {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;

    }

    /**
     * 完整的判断中文汉字和符号
     *
     */
    public static boolean isContainsChinese(String strName)
    {
        char[] chars = strName.toCharArray();
        for (char ch : chars) {
            if (isChineseCharacter(ch)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 汉字转换位汉语拼音首字母，英文字符不变，特殊字符丢失 支持多音字，生成方式如:
     * （长沙市长:cssc,zssz,zssc,cssz）
     *
     */
    public static String converterToFirstSpell(String hanzi)
    {
        boolean isLastIsEngChr = false;
        char[] hanziChar = hanzi.toCharArray();
        List<StringBuilder> pinyinStrs = new ArrayList<>();

        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        pinyinStrs.add(new StringBuilder());
        for (char aChar : hanziChar) {
            if (isChineseCharacter(aChar)) {
                isLastIsEngChr = false;
                try {
                    //取得当前汉字的所有全拼
                    String[] strArray = PinyinHelper.toHanyuPinyinStringArray(aChar, defaultFormat);
                    if(strArray == null) {
                        continue;
                    }
                    for (int i = pinyinStrs.size() - 1; i >= 0; i--) {
                        for (int j = 0; j < strArray.length; j++) {
                            if(j > 0 ){
                                pinyinStrs.add(new StringBuilder());
                                int id = pinyinStrs.size() - 1;
                                pinyinStrs.get(id).append(pinyinStrs.get(i));
                                pinyinStrs.get(id).deleteCharAt(pinyinStrs.get(id).length() - 1);
                                pinyinStrs.get(pinyinStrs.size()-1).append(strArray[j].charAt(0));
                                continue;
                            }
                            pinyinStrs.get(i).append(strArray[j].charAt(0));
                        }
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else if(isEnglishCharacter(aChar)){
                if(isLastIsEngChr) {    //英文单词同样只取首字母；
                    continue;
                }
                isLastIsEngChr = true;
                for(StringBuilder stringBuilder : pinyinStrs)
                    stringBuilder.append(Character.toLowerCase(aChar));
            }else {
                isLastIsEngChr = false;
            }
            //pinyinString.append("");
        }

        for(int i = 1; i < pinyinStrs.size(); i++){
            pinyinStrs.get(0).append(',');
            pinyinStrs.get(0).append(pinyinStrs.get(i));
        }

        return pinyinStrs.get(0).toString();
    }

    /**
     * 汉字转换位汉语全拼，英文字符不变，特殊字符丢失
     * 支持多音字，生成方式如（重当参:zhongdangcen,zhongdangcan,chongdangcen,
     * chongdangshen,zhongdangshen,chongdangcan）
     *
     */
    public static String converterToSpell(String chines)
    {
        StringBuilder pinyinName = new StringBuilder();
        char[] chinesChar = chines.toCharArray();

        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        for (char aChar : chinesChar) {
            if (isChineseCharacter(aChar)) {
                try {
                    //取得当前汉字的所有全拼
                    String[] strs = PinyinHelper.toHanyuPinyinStringArray(aChar, defaultFormat);
                    if (strs != null) {
                        for (int j = 0; j < strs.length; j++) {
                            pinyinName.append(strs[j]);
                            if (j != strs.length - 1) {
                                pinyinName.append(",");
                            }
                        }
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName.append(aChar);
            }
            pinyinName.append("");
        }

        return pinyinName.toString();
    }
}
