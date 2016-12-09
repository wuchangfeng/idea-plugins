import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Element {

    // constants
    private static final Pattern sIdPattern = Pattern.compile("@\\+?(android:)?id/([^$]+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern sValidityPattern = Pattern.compile("^([a-zA-Z_\\$][\\w\\$]*)$", Pattern.CASE_INSENSITIVE);
    public String id;
    // AndroidNs 是什么属性
    public boolean isAndroidNS = false;
    // element mClassName with package，com.xyz.aa.java
    // 完整的类名字
    public String nameFull;
    // element mClassName 类名
    public String name;
    // 1 aa_bb_cc; 2 aaBbCc 3 mAaBbCc
    public int fieldNameType = 1;
    public boolean isValid = false;
    public boolean used = true;
    // Button, view_having_clickable_attr etc.
    public boolean isClickable = false;
    // ListView, GridView etc.
    public boolean isItemClickable = false;
    // EditText
    public boolean isEditText = false;
    public XmlTag xml;
    //GET SET mClassName
    public String strGetMethodName;
    public String strSetMethodName;

    /**
     *
     * @param name just like TextView
     * @param id  just like "et_data" in android:id="@+id/et_data"
     * @param xml just like <TextView>android:id=.....android:layout_width:=....</>
     */
    public Element(String name, String id, XmlTag xml) {

        final Matcher matcher = sIdPattern.matcher(id);
        if (matcher.find() && matcher.groupCount() > 1) {
            this.id = matcher.group(2);
            String androidNS = matcher.group(1);
            this.isAndroidNS = !(androidNS == null || androidNS.length() == 0);
        }

        // 抛出异常
        if (this.id == null) {
            throw new IllegalArgumentException("Invalid format of view id");
        }

        // mClassName split("\\.") 效果类似于 split(".") z转义
        String[] packages = name.split("\\.");

        if (packages.length > 1) {
            this.nameFull = name;
            this.name = packages[packages.length - 1];
        } else {
            this.nameFull = null;
            this.name = name;
        }

        this.xml = xml;
        // clickable
        XmlAttribute clickable = xml.getAttribute("android:clickable", null);
        boolean hasClickable = clickable != null &&
                clickable.getValue() != null &&
                clickable.getValue().equals("true");

        String xmlName = xml.getName();

        // 如果 tag 中含有 RadioButton 标签
        if (xmlName.contains("RadioButton")) {
            // TODO check
        } else {
            if ((xmlName.contains("ListView") || xmlName.contains("GridView")) && hasClickable) {
                isItemClickable = true;
            } else if (xmlName.contains("Button") || hasClickable) {
                isClickable = true;
            }
        }
        // 判断是否含有 EditText 表填
        isEditText = xmlName.contains("EditText");
    }


    /**
     * Create full ID for using in layout XML files
     * et_data = (EditText) findViewById(R.id.et_data);
     *
     * @return
     */
    public String getFullID() {
        StringBuilder fullID = new StringBuilder();
        String rPrefix;

        if (isAndroidNS) {
            rPrefix = "android.R.id.";
        } else {
            rPrefix = "R.id.";
        }

        fullID.append(rPrefix);
        fullID.append(id);
        return fullID.toString();
    }


    /**
     * Generate field mClassName just like aa_bb_cc in
     * private TextView aa_bb_cc;
     *
     * @return
     */
    public String getFieldName() {
        String fieldName = id;
        String[] names = id.split("_");
        return fieldName;
    }
}
