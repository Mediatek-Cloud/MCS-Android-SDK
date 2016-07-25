package com.mediatek.mcstutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import com.google.gson.Gson;
import com.mediatek.mcs.Utils.UIUtils;
import com.mediatek.mcs.domain.McsDataChannel;
import com.mediatek.mcs.domain.McsResponse;
import com.mediatek.mcs.entity.DataChannelEntity;
import com.mediatek.mcs.entity.DataPointEntity;
import com.mediatek.mcs.entity.api.DeviceInfoEntity;
import com.mediatek.mcs.entity.api.DeviceSummaryEntity;
import com.mediatek.mcs.net.McsJsonRequest;
import com.mediatek.mcs.net.RequestApi;
import com.mediatek.mcs.net.RequestManager;
import com.mediatek.mcs.socket.McsSocketListener;
import com.mediatek.mcs.socket.SocketManager;
import java.util.List;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

  TextView tv_info;
  Button btn_nav;
  Button btn_req_devices;
  Button btn_req_device_detail;
  Button btn_show_data_channel;
  Button btn_submit_data_point;
  EditText et_submit_data_point;
  Switch switch_socket;

  String mDeviceId = "";
  DeviceInfoEntity mDeviceInfo;
  McsDataChannel mDataChannel;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_request);

    this.tv_info = (TextView) findViewById(R.id.tv_info);
    this.btn_nav = (Button) findViewById(R.id.btn_nav);
    this.btn_req_devices = (Button) findViewById(R.id.btn_req_devices);
    this.btn_req_device_detail = (Button) findViewById(R.id.btn_req_device_detail);
    this.btn_show_data_channel = (Button) findViewById(R.id.btn_show_data_channel);
    this.btn_submit_data_point = (Button) findViewById(R.id.btn_submit_data_point);
    this.et_submit_data_point = (EditText) findViewById(R.id.et_submit_data_point);
    this.switch_socket = (Switch) findViewById(R.id.switch_socket);

    btn_nav.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        final Intent intent = new Intent(MainActivity.this, SessionActivity.class);
        startActivity(intent);
      }
    });

    btn_req_devices.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        requestDevices();
      }
    });

    btn_req_device_detail.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        requestDeviceInfo(mDeviceId);
      }
    });

    btn_show_data_channel.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        showDataChannel(mDeviceInfo);
      }
    });

    btn_submit_data_point.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        submitDataPoint();
      }
    });

    switch_socket.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          turnOnSocket();
          btn_submit_data_point.setVisibility(View.VISIBLE);
          et_submit_data_point.setVisibility(View.VISIBLE);
        } else {
          turnOffSocket();
          btn_submit_data_point.setVisibility(View.GONE);
          et_submit_data_point.setVisibility(View.GONE);
        }
      }
    });
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /**
   * GET device list.
   */
  private void requestDevices() {
    // Default method is GET
    int method = McsJsonRequest.Method.GET;
    String url = RequestApi.DEVICES;
    McsResponse.SuccessListener<JSONObject> successListener =
        new McsResponse.SuccessListener<JSONObject>() {
          @Override public void onSuccess(JSONObject response) {
            List<DeviceSummaryEntity> summary = new Gson().fromJson(
                response.toString(), DeviceSummaryEntity.class).getResults();

            if (summary.size() > 0) {
              mDeviceId = summary.get(0).getDeviceId();
              btn_req_device_detail.setVisibility(View.VISIBLE);
            }

            printJson(response);
          }
    };

    /**
     * Optional.
     * Default error message would be shown in logcat.
     */
    McsResponse.ErrorListener errorListener = new McsResponse.ErrorListener() {
      @Override public void onError(Exception e) {
        tv_info.setText(e.toString());
      }
    };

    McsJsonRequest request = new McsJsonRequest(method, url, successListener, errorListener);
    RequestManager.sendInBackground(request);
  }

  /**
   * GET device info.
   */
  private void requestDeviceInfo(String deviceId) {
    McsJsonRequest request = new McsJsonRequest(
        RequestApi.DEVICE
            .replace("{deviceId}", deviceId),
        new McsResponse.SuccessListener<JSONObject>() {
          @Override public void onSuccess(JSONObject response) {
            mDeviceInfo = UIUtils.getFormattedGson()
                .fromJson(response.toString(), DeviceInfoEntity.class)
                .getResults().get(0);

            btn_show_data_channel.setVisibility(View.VISIBLE);
            printJson(response);
          }
        }
    );

    RequestManager.sendInBackground(request);
  }

  /**
   * GET data channel
   */
  private void showDataChannel(DeviceInfoEntity deviceInfo) {
    if (deviceInfo.getDataChannels().size() == 0) {
      tv_info.setText("data channel is empty, please create one");
      return ;
    }

    /**
     * Optional.
     * Default message of socket update shows in log.
     */
    McsSocketListener socketListener = new McsSocketListener(
        new McsSocketListener.OnUpdateListener() {
          @Override public void onUpdate(JSONObject data) {
            printJson(data);
          }
        }
    );
    DataChannelEntity channelEntity = deviceInfo.getDataChannels().get(0);

    mDataChannel = new McsDataChannel(deviceInfo, channelEntity, socketListener);

    switch_socket.setVisibility(View.VISIBLE);
    try {
      tv_info.setText(mDataChannel.getDeviceId() + ", " + mDataChannel.getDeviceKey() + "\n"
          + mDataChannel.getChannelId() + ", " + mDataChannel.getChannelName() + "\n\n" + mDataChannel
          .getDataChannelEntity()
          .toString() + "\n" + mDataChannel.getDataPointEntity().toString());
    } catch (Exception e) {
      printError(e);
    }
  }

  /**
   * Socket control of single data channel
   */
  private void turnOnSocket() {
    SocketManager.connectSocket();
    SocketManager.registerSocket(mDataChannel, mDataChannel.getMcsSocketListener());
  }

  private void turnOffSocket() {
    SocketManager.unregisterSocket(mDataChannel, mDataChannel.getMcsSocketListener());
    SocketManager.disconnectSocket();
  }

  /**
   *
   */
  private void submitDataPoint() {
    String data = et_submit_data_point.getText().toString();
    mDataChannel.submitDataPoint(new DataPointEntity.Values(data));
  }

  /**
   * Pretty-print a JSONObject.
   */
  private void printJson(JSONObject jsonObject) {
    try {
      tv_info.setText(jsonObject.toString(2));
    } catch (Exception e) {
      printError(e);
    }
  }

  private void printError(Exception e) {
    tv_info.setText("Something went wrong, please check Logcat for detail info.\n"
        + "Also, please "
        + "1. Create Prototype \n"
        + "2. Create Device \n"
        + "3. Upload a datapoint \n" 
        + "via https://mcs.mediatek.com");
    e.printStackTrace();
  }
}
