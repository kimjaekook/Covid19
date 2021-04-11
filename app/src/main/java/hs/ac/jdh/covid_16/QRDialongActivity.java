package hs.ac.jdh.covid_16;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public class QRDialongActivity {

    private Context context;

    public QRDialongActivity(Context context) {
        this.context = context;
    }

    public void callFunction(final TextView tvTemp) {
        final Dialog dlg = new Dialog(context);

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.qr_dialong);
        dlg.setCancelable(false);
        dlg.show();

        final EditText etName = (EditText) dlg.findViewById(R.id.etName);
        final EditText etId = (EditText) dlg.findViewById(R.id.etId);
        final EditText etPnum = (EditText) dlg.findViewById(R.id.etPnum);
        final Button btnOK = (Button) dlg.findViewById(R.id.btnOK);


        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String temp = "이름 : " + etName.getText().toString() + "\n주민등록번호 : " + etId.getText().toString() +
                        "\n핸드폰 번호 : " + etPnum.getText().toString();
                try {
                    FileOutputStream outFs = context.openFileOutput("qrinfo.txt", Context.MODE_PRIVATE);
                    outFs.write(temp.getBytes());
                    outFs.close();
                    CreateQR.fin = true;
                    Toast.makeText(context, "QR코드가 생성되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context.getApplicationContext(), CreateQR.class);
                    ((CreateQR)context).finish();
                    context.startActivity(intent);
                } catch (FileNotFoundException e) {
                    Toast.makeText(context, "파일 생성을 실패했습니다.", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(context, "파일 쓰기를 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
                dlg.dismiss();
            }
        });
    }
}