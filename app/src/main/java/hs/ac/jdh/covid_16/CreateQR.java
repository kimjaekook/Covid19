package hs.ac.jdh.covid_16;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

public class CreateQR extends AppCompatActivity {
    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSION_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    ImageView iv;
    Button btnQR;
    public static boolean fin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_qr);

        if (!checkLocationServicesStatus()) {
            showDialogFoerLocationServiceSetting();
        }
        else {
            checkRunTimePermission();
        }

        final TextView tvTemp = (TextView) findViewById(R.id.tvTemp);

        btnQR = (Button) findViewById(R.id.btnQR);

        if (new File(getDataDir().getAbsolutePath()).exists()) {
            btnQR.setText("QR?????? ??????");

            gpsTracker = new GpsTracker(CreateQR.this);

            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongtitude();

            String address = getCurrentAddress(latitude, longitude);

            FileInputStream inFs;
            try {
                byte[] txt = new byte[500];
                inFs = openFileInput("qrinfo.txt");
                inFs.read(txt);
                inFs.close();

                iv = (ImageView) findViewById(R.id.qrCode);
                String text = (new String(txt)).trim();

                Hashtable hints = new Hashtable();
                hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                BitMatrix bitMatrix = null;
                bitMatrix = multiFormatWriter.encode(text.concat("\n" + address), BarcodeFormat.QR_CODE, 200, 200, hints);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                iv.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                btnQR.setText("QR?????? ??????");
                Toast.makeText(getApplicationContext(), "????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            } catch (WriterException e) {
                Toast.makeText(getApplicationContext(), "???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "QR????????? ????????????. ????????? ?????? QR????????? ???????????????.", Toast.LENGTH_SHORT).show();
        }

        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QRDialongActivity qrDialongActivity = new QRDialongActivity(CreateQR.this);
                qrDialongActivity.callFunction(tvTemp);
            }
        });
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showDialogFoerLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateQR.this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n?????? ????????? ?????????????????????????");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    void checkRunTimePermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(CreateQR.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(CreateQR.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) { }
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CreateQR.this, REQUIRED_PERMISSION[0])) {
                Toast.makeText(CreateQR.this, "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(CreateQR.this, REQUIRED_PERMISSION, PERMISSION_REQUEST_CODE);
            }
            else {
                ActivityCompat.requestPermissions(CreateQR.this, REQUIRED_PERMISSION, PERMISSION_REQUEST_CODE);
            }
        }
    }

    public String getCurrentAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 7);
        } catch (IOException ioEception) {
            Toast.makeText(this, "???????????? ????????? ????????????", Toast.LENGTH_SHORT).show();
            return  "???????????? ????????? ????????????";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "????????? GPS ??????", Toast.LENGTH_SHORT).show();
            return "????????? GPS ??????";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "?????? ?????????", Toast.LENGTH_SHORT).show();
            return "?????? ?????????";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }
}
