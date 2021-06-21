package com.peep.contractbak.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.peep.contractbak.BaseActivity;
import com.peep.contractbak.R;
import com.peep.contractbak.activity.ConnectActivity;
import com.peep.contractbak.activity.DownloadActivity;
import com.peep.contractbak.client.ClientSocketManager;
import com.peep.contractbak.p2pconn.WiFiDirectBroadcastReceiver;
import com.peep.contractbak.server.ServerSocketManager;
import com.peep.contractbak.utils.ConstantUtils;
import com.peep.contractbak.utils.ToolUtils;


public class ReceiveFileFragment extends Fragment {

    private ConnectFragment connectFragment;
    private ScannerFragment scannerFragment;
    private TransFragment transFragment;
        private View baseView;
        private ConnectActivity connectActivity;
        public ImageView imM;
        private TextView download;
        private TextView tv_old;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private ReceiveFileFragment receiveFileFragment;
    @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            baseView = inflater.inflate(R.layout.activity_receive_file,null);
            return baseView;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            connectActivity = (ConnectActivity)getActivity();
            initView();
            initClick();
        }

    private void initClick() {
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DownloadActivity.class);
                startActivity(intent);
            }
        });
        tv_old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(connectActivity,"点击了",Toast.LENGTH_LONG).show();
             
            }
        });
    }



    private void initView() {
        tv_old = baseView.findViewById(R.id.tv_old);
        imM = baseView.findViewById(R.id.imM);
            download = baseView.findViewById(R.id.download);
            ConstantUtils.stopSocket();
            Bitmap codeBitmap = ToolUtils.pruCode(connectActivity, "p2p://"+ToolUtils.getLocalIPAddress());
            imM.setImageBitmap(codeBitmap);
            imM.setVisibility(View.VISIBLE);
            connectActivity.startScanAsServer();  //启动服务端

    }

    @Override
    public void onResume() {
        super.onResume();

    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        connectActivity.removeDisconnect();
    }
    /**
     * 隐藏二维码
     * */
    public void setImageCodeGone(int a){
        Log.i("aaaaaaaaaaaaaaaaaaaaa",a+"");
        if(null == imM){
            return;
        }

        imM.setVisibility(View.GONE);
    }


}

