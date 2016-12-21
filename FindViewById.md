现在有一个Java文件，那么我们就可以构建一个PsiJavaFile对象，通过该对象，我们可以了解该java文件的一些信息，如package 名称，import语句列表，包含的class。假设我们获取了PsiJavaFile的一个PsiClass对象，我们就可以了解该Java类的各种信息，如名称、注释和包含的函数等等。在这里我们可以更改PsiClass的名称、注释等等，这些修改马上就会反映到文件的内容中。试想一下，如果这一切通过文本分析完成，那将是多么复杂的工作，有了Psi File，这一切就简单啦，操作对象比操作文件内容要可靠简单的多。关于PSI，请参考一些IntelliJ IDEA的插件开发文档，同时我们推荐Psi Viewer这个插件，你可以对IDEA处理内容做更好地理解。如果你想写出好的插件的话，你需要对PSI有较深的理解，虽然我写的很少，但是它的重要性却相当高。



``` java
if (fieldNameType == 2) {
            // aaBbCc
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < names.length; i++) {
                if (i == 0) {
                    sb.append(names[i]);
                } else {
                    sb.append(firstToUpperCase(names[i]));
                }
            }
            fieldName = sb.toString();
        } else if (fieldNameType == 3) {
            // mAaBbCc
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < names.length; i++) {
                if (i == 0) {
                    sb.append("m");
                }
                sb.append(firstToUpperCase(names[i]));
            }
            fieldName = sb.toString();
        }

/**
     * Check validity of field mClassName
     *
     * @return
     */
    public boolean checkValidity() {
        Matcher matcher = sValidityPattern.matcher(getFieldName());
        isValid = matcher.find();
        return isValid;
    }


    /**
     * 首字母大写
     * @param key
     * @return
     */
    public static String firstToUpperCase(String key) {
        return key.substring(0, 1).toUpperCase(Locale.CHINA) + key.substring(1);
    }
```

