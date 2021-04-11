package hs.ac.jdh.covid_16;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class HomeActivity extends Activity {

    TextView tvTitle;
    TextView tvState_dt, tvState_time, tvDecide_cnt, tvClear_cnt, tvDeath_cnt, tvExam_cnt;
    Button btnQR;
    Button btnCovid, btnWHO, btnKDCA;
    DatePicker dp;

    private static Date currentTime = Calendar.getInstance().getTime();
    public static String date_text = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(currentTime);

    XmlPullParser xpp;
    String key = "4LN3hWJCjelfbB%2B4cgzghf%2Fc0OFEg%2F6Q1pDjY90S19P%2BwlSnKKnEvvJxa1xwOD%2BetKgnmHusygUywVzISZmyhA%3D%3D";

    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        StrictMode.enableDefaults();

        tvTitle = (TextView) findViewById(R.id.tvTitle);

        tvState_dt = (TextView) findViewById(R.id.tvState_dt);
        tvState_time = (TextView) findViewById(R.id.tvState_time);
        tvDecide_cnt = (TextView) findViewById(R.id.tvDecide_cnt);
        tvClear_cnt = (TextView) findViewById(R.id.tvClear_cnt);
        tvDeath_cnt = (TextView) findViewById(R.id.tvDeath_cnt);
        tvExam_cnt = (TextView) findViewById(R.id.tvExam_cnt);

        btnQR = (Button) findViewById(R.id.btnQR);

        btnCovid = (Button) findViewById(R.id.btnCovid);
        btnWHO = (Button) findViewById(R.id.btnWHO);
        btnKDCA = (Button) findViewById(R.id.btnKDCA);

        dp = (DatePicker) findViewById(R.id.dp);

        Calendar cal = Calendar.getInstance();
        int cYear = cal.get(Calendar.YEAR);
        int cMonth = cal.get(Calendar.MONTH);
        int cDay = cal.get(Calendar.DAY_OF_MONTH);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    data = getXmlData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String[] str = data.split("/");
                            tvState_dt.setText("기준날짜 : " + str[4]);
                            tvState_time.setText(str[5]);
                            tvDecide_cnt.setText("확진자\n" + str[2]);
                            tvClear_cnt.setText("격리해제\n" + str[0]);
                            tvDeath_cnt.setText("사망자\n" + str[1]);
                            tvExam_cnt.setText("검사진행\n" + str[3]);
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateQR.class);
                startActivity(intent);

                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        btnCovid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ncov.mohw.go.kr/"));
                startActivity(intent);
            }
        });
        btnWHO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.who.int/"));
                startActivity(intent);
            }
        });
        btnKDCA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.kdca.go.kr/"));
                startActivity(intent);
            }
        });

        dp.init(cYear, cMonth, cDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                String tempMonth, tempDay;
                if ((i1 + 1) < 10) {
                    tempMonth = "0" + Integer.toString(i1 + 1);
                } else {
                    tempMonth = Integer.toString(i1 + 1);
                }
                if (i2 < 10) {
                    tempDay = "0" + Integer.toString(i2);
                } else {
                    tempDay = Integer.toString(i2);
                }
                date_text = Integer.toString(i) + tempMonth + tempDay;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            data = getXmlData();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String[] str = data.split("/");
                                    tvState_dt.setText("기준날짜 : " + str[4]);
                                    tvState_time.setText(str[5]);
                                    tvDecide_cnt.setText("확진자\n" + str[2]);
                                    tvClear_cnt.setText("격리해제\n" + str[0]);
                                    tvDeath_cnt.setText("사망자\n" + str[1]);
                                    tvExam_cnt.setText("검사진행\n" + str[3]);
                                }
                            });
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    String getXmlData() throws MalformedURLException {
        StringBuffer buffer = new StringBuffer();

        String queryUrl = "http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson"
                + "?serviceKey=" + key + "&pageNo=1&numOfRows=10&startCreateDt=" + date_text + "&endCreateDt=" + date_text;

        try {
            URL url = new URL(queryUrl);
            InputStream is = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8"));

            String tag;

            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();

                        if(tag.equals("item"));
                        else if(tag.equals("stateDt")) {
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("/");
                        }
                        else if(tag.equals("stateTime")) {
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("/");
                        }
                        else if(tag.equals("decideCnt")) {
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("/");
                        }
                        else if(tag.equals("clearCnt")) {
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("/");
                        }
                        else if(tag.equals("deathCnt")) {
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("/");
                        }
                        else if(tag.equals("examCnt")) {
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("/");
                        }
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();
                        if (tag.equals("item")) { buffer.append("\n"); }
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
