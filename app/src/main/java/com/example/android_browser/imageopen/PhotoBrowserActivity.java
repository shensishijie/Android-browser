package com.example.android_browser.imageopen;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android_browser.R;
import com.example.android_browser.utils.FileUtil;

public class PhotoBrowserActivity extends Activity implements View.OnClickListener{
    private ViewPager mPager;
    private String curImageUrl = "";
    private String[] imageUrls;

    private ImageView backIv;
    private ImageView loadingIv;
    private TextView saveTv;
    private TextView orderTv;

    private int curPosition=0;
    private int[] initialedPostion;
    private ObjectAnimator objectAnimator;
    private View curPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_photo_browser);

        curImageUrl = getIntent().getStringExtra("curImageUrl");
        imageUrls=getIntent().getStringArrayExtra("imageUrls");
        initialedPostion=new int[imageUrls.length];
        for(int i=0;i<imageUrls.length;i++){
            initialedPostion[i]=-1;         //初始化-1；
            if(curImageUrl.equals(imageUrls[i])){       //找出点击图片的在列表中具体序号
                curPosition=i;
            }
        }
        init();
    }
    private void init(){
        backIv=findViewById(R.id.backIv);         //返回
        saveTv=findViewById(R.id.saveTv);         //保存
        orderTv=findViewById(R.id.orderTv);       //图片序号
        loadingIv=findViewById(R.id.loadingIv);   //加载中图片

        backIv.setOnClickListener(this);
        saveTv.setOnClickListener(this);

        objectAnimator=ObjectAnimator.ofFloat(loadingIv,"rotation",0,360);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setDuration(2000);

        mPager = (ViewPager) findViewById(R.id.pager);        //显示页面
        mPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        mPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return imageUrls.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                if(imageUrls[position]!=null && !imageUrls[position].equals("")){
                    final PhotoView view = new PhotoView(PhotoBrowserActivity.this);
                    view.enable();
                    view.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    Glide.with(PhotoBrowserActivity.this)
                            .load(imageUrls[position])
                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .fitCenter()
                            .listener(new RequestListener<Drawable>() {
                                //加载失败显示错误图片
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    loadFailed();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    initialedPostion[position]=position;
                                    hideLoading();
                                    return false;
                                }
                            })
                            .into(view);
                    curPosition=position;
                    container.addView(view);
                    return view;
                }
            return null;
            }
            //页面滑动过后摧毁
            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                initialedPostion[position]=-1;
                container.removeView((View) object);
            }
            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                curPage = (View) object;
            }
        });
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {//页面滑动时

            }

            @Override
            public void onPageSelected(int position) {//滑动结束
                if(initialedPostion[position]!=curPosition){
                    loading();
                }else{
                    hideLoading();
                }
                curPosition=position;
                orderTv.setText((curPosition+1)+"/"+imageUrls.length);
                mPager.setTag(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {//state=1滑动，0未滑动，2滑动结束

            }
        });

        mPager.setCurrentItem(curPosition);
        mPager.setTag(curPosition);
        if(curPosition!=initialedPostion[curPosition]){
            loading();
        }
        orderTv.setText((curPosition+1)+"/"+imageUrls.length);

    }

    //加载失败时
    private void loadFailed(){
        saveTv.setVisibility(View.GONE);
        orderTv.setVisibility(View.GONE);

        //loadingIv.setImageResource(R.drawable.ic_baseline_error_24);//设置错误图片
        loadingIv.setVisibility(View.VISIBLE);
        objectAnimator.start();
        objectAnimator.cancel();
        Toast.makeText(this,"图片加载错误",Toast.LENGTH_SHORT);

    }
    //加载中
    private void loading(){
        saveTv.setVisibility(View.VISIBLE);
        orderTv.setVisibility(View.VISIBLE);
        //loadingIv.setImageResource(R.drawable.ic_baseline_autorenew_24);//设置加载图片
        loadingIv.setVisibility(View.VISIBLE);
        objectAnimator.start();
    }
    //隐蔽加载动画
    private void hideLoading(){
        loadingIv.setVisibility(View.GONE);
        objectAnimator.cancel();
    }
    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backIv://返回
                finish();
                break;
            case R.id.saveTv://保存
                saveImage();
                break;
            default:
                break;
        }
    }
    //保存图片操作
    private void saveImage(){
        PhotoView photoViewTemp = (PhotoView) curPage;
        if (photoViewTemp != null) {
            BitmapDrawable glideBitmapDrawable = (BitmapDrawable) photoViewTemp.getDrawable();
            if (glideBitmapDrawable == null) {
                return;
            }
            Bitmap bitmap = glideBitmapDrawable.getBitmap();
            if (bitmap == null) {
                return;
            }
            FileUtil.savePhoto(this, bitmap, new FileUtil.SaveResultCallback() {
                @Override
                public void onSavedSuccess() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PhotoBrowserActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onSavedFailed() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PhotoBrowserActivity.this, "保存失败，请确认已开通存储权限", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
}
