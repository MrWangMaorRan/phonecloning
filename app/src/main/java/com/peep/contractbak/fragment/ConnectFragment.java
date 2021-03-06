package com.peep.contractbak.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.DislikeInfo;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.PersonalizationPrompt;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.lwy.righttopmenu.MenuItem;
import com.lwy.righttopmenu.RightTopMenu;
import com.peep.contractbak.bannerss.DislikeDialog;
import com.peep.contractbak.bannerss.TTAdManagerHolder;
import com.peep.contractbak.bannerss.TToast;
import com.peep.contractbak.BaseActivity;
import com.peep.contractbak.R;
import com.peep.contractbak.activity.AgreementActivity;
import com.peep.contractbak.activity.ConnectActivity;
import com.peep.contractbak.activity.PolicyActivity;
import com.peep.contractbak.utils.ConstantUtils;
import com.peep.contractbak.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class ConnectFragment extends Fragment implements View.OnClickListener {
    private Dialog mShareDialog;
    private View baseView;
    private ConnectActivity connectActivity;
    private ImageView mMenuIV;
    private RightTopMenu mRightTopMenu;
    private TextView agreement;
    private TextView cancel;
    private TextView consent;
    private TextView policy;
    private TTNativeExpressAd mTTAd;
    public FrameLayout express_container;
    private long startTime = 0;
    private boolean mHasShowDownloadActive = false;
    private Context mContext;
    private TTAdNative mTTAdNative;
    private String TAG="ConnectFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_connect,null);
        return baseView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TTAdManagerHolder.get().requestPermissionIfNecessary(getActivity());
        connectActivity = (ConnectActivity)getActivity();
        onTobat();
        String ok = SharedPreferencesUtil.getSharedPreferences(getActivity()).getString("OK", "");
        if (ok==null||!ok.equals("123")){
            onDialog();
        }
        initView();
        initBanners();
    }

    public void onDialog(){
        mShareDialog = new Dialog(getContext(), R.style.dialog_bottom_full);
        mShareDialog.setCanceledOnTouchOutside(false); //???????????????????????????
        mShareDialog.setCancelable(false);             //????????? ???true(?????????????????????)
        Window window = mShareDialog.getWindow();      // ??????dialog?????????
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.share_animation);
        window.getDecorView().setPadding(150, 0, 150, 0);

        View view = View.inflate(getContext(), R.layout.dialog_lay_share_dialog, null); //??????????????????
        agreement = view.findViewById(R.id.agreement);
        cancel = view.findViewById(R.id.cancel);
        consent = view.findViewById(R.id.consent);
        policy = view.findViewById(R.id.policy);
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//??????????????????

        DialogListener();

        mShareDialog.show();
    }
    private void DialogListener() {
        agreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AgreementActivity.class);
                startActivity(intent);
            }
        });
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PolicyActivity.class);
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        consent.setOnClickListener(new View.OnClickListener() {

            private String ok="123";

            @Override
            public void onClick(View v) {
                mShareDialog.dismiss();
                SharedPreferencesUtil.getSharedPreferences(getContext()).putString("OK",ok);
            }
        });
    }
    public void  onTobat(){
        mMenuIV = baseView.findViewById(R.id.menu_iv);
        mMenuIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<com.lwy.righttopmenu.MenuItem> menuItems = new ArrayList<>();
                menuItems.add(new com.lwy.righttopmenu.MenuItem(R.mipmap.fenxiang, getResources().getString(R.string.Share_with_friends), 100));
                menuItems.add(new com.lwy.righttopmenu.MenuItem(R.mipmap.pinglun,getResources().getString(R.string.Rate_us), 0));
                menuItems.add(new com.lwy.righttopmenu.MenuItem(R.mipmap.yinsi, getResources().getString(R.string.Privacy_Policy)));
                menuItems.add(new MenuItem(R.mipmap.xieyi, getResources().getString(R.string.Terms_of_Service)));
                if (mRightTopMenu == null) {
                    Log.i("?????????","?????????");
                    mRightTopMenu = new RightTopMenu.Builder(getActivity())
//                            .windowHeight(480)     //?????????????????????3????????????wrap_content,?????????????????????320
//                        .windowWidth()      //????????????wrap_content
                            .dimBackground(true)           //????????????????????????true
                            .needAnimationStyle(true)   //????????????????????????true
                            .animationStyle(R.style.RTM_ANIM_STYLE)  //?????????R.style.RTM_ANIM_STYLE

                            .menuItems(menuItems)
                            .onMenuItemClickListener(new RightTopMenu.OnMenuItemClickListener() {
                                @Override
                                public void onMenuItemClick(int position) {
                                    final String[] cities = {getString(R.string.lan_chinese), getString(R.string.lan_en),getString(R.string.lan_zh_rTYW),getString(R.string.Follow_the_system)};
                                    final String[] locals = {"zh_CN", "en","zh_TW","111"};
                                    if (position==0){

                                    }else if (position==1){

                                    }else if (position==2){
                                        Intent intent = new Intent(getContext(), PolicyActivity.class);
                                        startActivity(intent);
                                    }else if (position==3){
                                        Intent intent = new Intent(getContext(), AgreementActivity.class);
                                        startActivity(intent);

                                        // Duoyuyan();
                                    }

                                }
                            }).build();
                }
                mRightTopMenu.showAsDropDown(mMenuIV, 0, 0);
            }
        });
    }


    private void initView() {
//        imgeView = baseView.findViewById(R.id.codeImg);
//        imgeView.setVisibility(View.GONE);
        Button loginBtn = baseView.findViewById(R.id.news);
        loginBtn.setOnClickListener(this);
        Button registerBtn = baseView.findViewById(R.id.old);
        registerBtn.setOnClickListener(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.news:
                //??????socket??????
                if (!BaseActivity.ALLOWED_FLAG) {
                    Toast.makeText(connectActivity, "????????????", Toast.LENGTH_LONG).show();
                    return;
                }

                ReceiveFileFragment receiveFileFragment = new ReceiveFileFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.part4, receiveFileFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.commit();// ??????
                break;
            case R.id.old:
                //?????????
                if (!BaseActivity.ALLOWED_FLAG) {
                    Toast.makeText(connectActivity, "????????????", Toast.LENGTH_LONG).show();
                    return;
                }
                ConstantUtils.stopSocket();
                connectActivity.changeFragment(1);
//                ConstantUtils.stopSocket();
//                Bitmap codeBitmap = ToolUtils.pruCode(connectActivity, "p2p://"+ToolUtils.getLocalIPAddress());
//                imgeView.setImageBitmap(codeBitmap);
//                imgeView.setVisibility(View.VISIBLE);
//                connectActivity.startScanAsServer();  //???????????????
                break;
        }
    }
    private void initBanners() {
        express_container = baseView.findViewById(R.id.express_container);
        mContext = getActivity().getApplicationContext();
        //??????TTAdNative?????????createAdNative(Context context) context????????????Activity??????
        TTAdManagerHolder.init(getActivity());
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(getActivity());
        //step3:(?????????????????????????????????????????????):????????????????????????read_phone_state,??????????????????imei????????????????????????????????????????????????

        //step4:????????????????????????AdSlot,??????????????????????????????
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("946200858") //?????????id
                .setAdCount(1) //?????????????????????1???3???
                .setExpressViewAcceptedSize(FrameLayout.LayoutParams.MATCH_PARENT, 150) //??????????????????view???size,??????dp
                .build();
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            //??????????????????
            @Override
            public void onError(int code, String message) {
                Log.i("??????",code+""+message);
            }

            //??????????????????
            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }
                Log.i("????????????","???");
                Log.i("????????????",ads.size()+"");
                mTTAd = ads.get(0);
                mTTAd.setSlideIntervalTime(30 * 1000);
                if (mTTAd!=null){
                    mTTAd.render();
                    bindAdListener(mTTAd);

                }

                startTime = System.currentTimeMillis();

            }
        });

    }
    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                Log.i(TAG,"???????????????");
            }

            @Override
            public void onAdShow(View view, int type) {
                Log.i(TAG,"????????????");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e(TAG, "????????????");
                //??????view????????? ?????? dp
                if (view!=null){
                    express_container .removeAllViews();
                    express_container.addView(view);
                }

            }
        });
        //dislike??????
        bindDislike(ad, false);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                Log.e(TAG, "??????????????????");
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    Log.e(TAG, "????????????????????????");
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                Log.e(TAG, "???????????????????????????");
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                Log.e(TAG, "?????????????????????????????????");
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                Log.e(TAG, "?????????????????????????????????");
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                Log.e(TAG, "????????????");
            }
        });
    }

    /**
     * ??????????????????????????????????????????????????????
     * @param ad
     * @param customStyle ????????????????????????true:???????????????
     */
    private void bindDislike (TTNativeExpressAd ad,boolean customStyle){
        if (customStyle) {
            //????????????????????????????????????"????????????????????????"????????????????????????startPersonalizePromptActivity??????????????????
            final DislikeInfo dislikeInfo = ad.getDislikeInfo();
            if (dislikeInfo == null || dislikeInfo.getFilterWords() == null || dislikeInfo.getFilterWords().isEmpty()) {
                return;
            }
            final DislikeDialog dislikeDialog = new DislikeDialog(getActivity(), dislikeInfo);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    //????????????
                    //???????????????????????????????????????????????????
                    express_container.removeAllViews();
                }
            });
            dislikeDialog.setOnPersonalizationPromptClick(new DislikeDialog.OnPersonalizationPromptClick() {
                @Override
                public void onClick(PersonalizationPrompt personalizationPrompt) {
                    Log.e(TAG, "?????????????????????????????????");
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //???????????????????????????dislike????????????
        ad.setDislikeCallback(getActivity(), new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }



            @Override
            public void onSelected(int position, String value, boolean enforce) {
                express_container.removeAllViews();
                //???????????????????????????????????????????????????
                if (enforce) {
                    Log.e(TAG, "??????Banner ?????????sdk?????????view?????????");
                }
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "????????????");
            }




        });
//            //???????????????????????????dislike????????????

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConstantUtils.stopSocket();
    }
    //    /**
//     * ???????????????
//     * */
//    public void setImageCodeGone(){
//        if(null == imgeView){
//            return;
//        }
//        imgeView.setVisibility(View.GONE);
//    }
}
