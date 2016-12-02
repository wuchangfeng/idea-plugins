import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBColor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by allen on 2016/11/28.
 */
public class TranslationAction extends AnAction {

    private Editor mEditor;
    private String BASE_URL = "https://api.shanbay.com/";

    @Override
    public void actionPerformed(AnActionEvent e) {
        //获取编辑器
        mEditor = e.getData(PlatformDataKeys.EDITOR);
        if (mEditor != null) {
            SelectionModel model = mEditor.getSelectionModel();

            //获取选中的文本
            String selectedText = model.getSelectedText();

            if (selectedText != null) {
                //翻译并显示
                getTranslation(selectedText);
                //showPopupBalloon(selectedText);
            }
        }
    }

    private void getTranslation(String selectedText) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ShanBeiApi shanbei= retrofit.create(ShanBeiApi.class);
        Call<Translation> call = shanbei.listInfos(selectedText);
        call.enqueue(new Callback<Translation>() {

            @Override
            public void onResponse(Call<Translation> call, Response<Translation> response) {
                String msg = response.body().toString();
                showPopupBalloon(msg);
            }

            @Override
            public void onFailure(Call<Translation> call, Throwable throwable) {
                showPopupBalloon("error msg:"+ throwable.getMessage());
            }
        });
    }

    private void showPopupBalloon(final String result) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                JBPopupFactory factory = JBPopupFactory.getInstance();
                factory.createHtmlTextBalloonBuilder(result, null, new JBColor(new Color(186, 238, 186), new Color(73, 117, 73)), null)
                        .setFadeoutTime(5000)
                        .createBalloon()
                        .show(factory.guessBestPopupLocation(mEditor), Balloon.Position.below);
            }
        });
    }

}
