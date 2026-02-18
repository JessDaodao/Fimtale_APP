package com.app.fimtale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import java.io.IOException;
import java.io.InputStream;

public class HelpActivity extends AppCompatActivity {

    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        container = findViewById(R.id.container);

        addText("为什么需要配置 API 凭据？\n\n" +
                "该应用基于FimTale API运行，为了安全着想，用户访问API时必须提供APIKey和APIPass，你可以在FimTale上免费获取APIKey和APIPass\n\n" +
                "详细步骤：");

        addImage("img/1.png");

        addText("在登录FimTale账户后，进入设置菜单");

        addImage("img/2.png");

        addText("在“基本资料”选项卡内，找到“我的API令牌”部分，点击“添加新的令牌”");

        addImage("img/3.png");

        addText("稍等片刻，你会看见新生成的令牌，分别是APIKey和APIPass，复制这两个令牌（只复制图中红框圈住的部分）");

        addImage("img/4.png");

        addText("然后，回到APP，点击这个按钮，输入APIKey和APIPass，再点击“保存”即可");
    }

    private void addText(String text) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
        textView.setLineSpacing(0, 1.2f);
        container.addView(textView);
    }

    private void addImage(String assetPath) {
        ShapeableImageView imageView = new ShapeableImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) (16 * getResources().getDisplayMetrics().density);
        params.bottomMargin = (int) (16 * getResources().getDisplayMetrics().density);
        imageView.setLayoutParams(params);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        float radius = 12 * getResources().getDisplayMetrics().density;
        imageView.setShapeAppearanceModel(ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, radius)
                .build());

        try {
            InputStream is = getAssets().open(assetPath);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            imageView.setImageBitmap(bitmap);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        container.addView(imageView);
    }
}
