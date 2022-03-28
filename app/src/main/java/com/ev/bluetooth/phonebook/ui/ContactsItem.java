package com.ev.bluetooth.phonebook.ui;

import com.ev.bluetooth.phonebook.utils.Hanyu2Pinyin;

import java.io.Serializable;

public class ContactsItem implements Serializable,Comparable<ContactsItem> {
    private String name; // 姓名
    private String telephone;
    private String pinyin; // 姓名对应的拼音
    private String firstLetter; // 拼音的首字母

    public ContactsItem(String name,String telephone){
        this.name = name;
        this.telephone = telephone;
        pinyin = Hanyu2Pinyin.getPinYin(name); // 根据姓名获取拼音
        firstLetter = pinyin.substring(0, 1).toUpperCase(); // 获取拼音首字母并转成大写
        if (!firstLetter.matches("[A-Z]")) { // 如果不在A-Z中则默认为“#”
            firstLetter = "#";
        }
    }

    public String getName() {
        return name;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getPinyin() {
        return pinyin;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    @Override
    public int compareTo(ContactsItem another) {
        if (firstLetter.equals("#") && !another.getFirstLetter().equals("#")) {
            return 1;
        } else if (!firstLetter.equals("#") && another.getFirstLetter().equals("#")){
            return -1;
        } else {
            return pinyin.compareToIgnoreCase(another.getPinyin());
        }
    }
}
