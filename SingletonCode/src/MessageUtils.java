import com.intellij.openapi.ui.Messages;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by allen on 2016/12/22.
 */
public class MessageUtils {

    private static String creatorInfo;

    public static void showErrorMessage(String context,String title){
        Messages.showMessageDialog(context,title,Messages.getErrorIcon());
    }
    public static void showMessage(String context,String title){
        Messages.showMessageDialog(context,title,Messages.getInformationIcon());
    }

    public static void showDebugMessage(String context,String title){
        if(false) {
            Messages.showMessageDialog(context, title, Messages.getErrorIcon());
        }
    }

    public  static String getCreaterInfo(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        creatorInfo = "/**\n* Created by "+System.getProperty("user.name")+" on "+sdf.format(date)+"\n*/"+"\n" + "\n" ;
        return creatorInfo;
    }
}
