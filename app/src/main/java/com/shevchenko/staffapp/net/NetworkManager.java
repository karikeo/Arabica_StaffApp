package com.shevchenko.staffapp.net;

import android.os.AsyncTask;

import com.shevchenko.staffapp.Common.Common;
import com.shevchenko.staffapp.Model.Category;
import com.shevchenko.staffapp.Model.CompleteTask;
import com.shevchenko.staffapp.Model.CompltedTinTask;
import com.shevchenko.staffapp.Model.GpsInfo;
import com.shevchenko.staffapp.Model.Producto;
import com.shevchenko.staffapp.Model.Producto_RutaAbastecimento;
import com.shevchenko.staffapp.Model.TaskInfo;
import com.shevchenko.staffapp.Model.TaskType;
import com.shevchenko.staffapp.Model.TinTask;
import com.shevchenko.staffapp.Model.User;
import com.shevchenko.staffapp.Model.LogEvent;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.json.JSONArray;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.net.MalformedURLException;
import java.io.IOException;
import org.json.JSONException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.InputStream;

import android.os.Environment;
import android.util.Log;
import java.io.FileInputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;
public class NetworkManager {
	protected final static String UTF8 = "utf-8";
	protected Map<String, Object> _reqParams = null;
	
	private NetworkManager() {
		_reqParams = new LinkedHashMap<String, Object>();
	}
	private static NetworkManager s_instance = null;

	public static NetworkManager getManager() {
		if (s_instance == null) {
			s_instance = new NetworkManager();
		}
		synchronized (s_instance) {
			return s_instance;
		}
	}

    private final static String SERVER_URL = "http://23.254.209.250:8087/staff/";
    //private final static String SERVER_URL = "http://192.168.1.217/staff/";
	protected final static String URL_LOGIN 		    = "http://vex.cl/login.aspx";
    protected final static String URL_UPLOAD 		    = "http://vex.cl/posttask.aspx";
    protected final static String URL_UPLOAD_TIN 		    = "http://vex.cl/posttintask.aspx";
    protected final static String URL_UPLOAD_FILE 		    = "http://vex.cl/uploadfile.aspx";
    protected final static String URL_LOADTASKS 	= "http://vex.cl/task.aspx";
    protected final static String URL_CATEGORY      ="http://vex.cl/category.aspx";
    protected final static String URL_PRODUCTO      ="http://vex.cl/producto.aspx";
    protected final static String URL_LOGEVENT      ="http://vex.cl/logevent.aspx";

    /*
    protected final static String URL_LOGIN 		    = "http://192.168.1.180:8070/login.aspx";
    protected final static String URL_UPLOAD 		    = "http://192.168.1.180:8070/posttask.aspx";
    protected final static String URL_UPLOAD_FILE 		    = "http://192.168.1.180:8070/uploadfile.aspx";
    protected final static String URL_LOADTASKS 	= "http://192.168.1.180:8070/task.aspx";
    protected final static String URL_CATEGORY      ="http://192.168.1.180:8070/category.aspx";
*/
    private String filePath = "";
    private DownloadThread dThread;
    public int login(String strUserID, String strPassword) {
        String myResult;
        try {
            //   URL 설정하고 접속하기
            URL url = new URL(URL_LOGIN);       // URL 설정
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속
            //--------------------------
            //   전송 모드 설정 - 기본적인 설정이다
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true);                         // 서버에서 읽기 모드 지정
            http.setDoOutput(true);                       // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST");         // 전송 방식은 POST
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //   서버로 값 전송

            StringBuffer buffer = new StringBuffer();
            buffer.append("userid").append("=").append(strUserID).append("&");                 // php 변수에 값 대입
            buffer.append("pwd").append("=").append(strPassword);  // php 변수 앞에 '$' 붙이지 않는다
            OutputStream out = http.getOutputStream();
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();

            // 서버에서 전송받기

            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            myResult = builder.toString();
            final JSONObject obj = new JSONObject(myResult.toString());
            String strRet = obj.getString("result");
            if(strRet.equals("success"))
                return Integer.parseInt(obj.getString("userid"));
            else
                return 0;
        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            e.printStackTrace();
            //
        } catch (JSONException e) {

        }
        return -1;
    }
    public void loadCategory(ArrayList<Category> arrCategory, ArrayList<Producto> arrPro, ArrayList<Producto_RutaAbastecimento> arrPro_Ruta, ArrayList<User> arrusers, ArrayList<TaskType> arrTypes)
    {
         String myResult;
        try {
            //   URL 설정하고 접속하기
            URL url = new URL(URL_CATEGORY);       // URL 설정
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속
            //--------------------------
            //   전송 모드 설정 - 기본적인 설정이다
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true);                         // 서버에서 읽기 모드 지정
            http.setDoOutput(true);                       // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST");         // 전송 방식은 POST
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //   서버로 값 전송

            StringBuffer buffer = new StringBuffer();
            buffer.append("userid").append("=").append(Common.getInstance().getUserID());                 // php 변수에 값 대입
            OutputStream out = http.getOutputStream();
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();
            // 서버에서 전송받기

            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            myResult = builder.toString();
            final JSONObject retVal = new JSONObject(myResult.toString());
            String strRet = retVal.getString("result");
            if(strRet.equals("success"))
            {
                JSONArray arrJsonCategory = retVal.getJSONArray("category");
                JSONObject obj;
                for (int i = 0; i < arrJsonCategory.length(); i++) {
                    obj = arrJsonCategory.getJSONObject(i);
                    Category info = new Category(obj.getString("category"));
                    arrCategory.add(info);
                }
                JSONArray arrJsonProducto = retVal.getJSONArray("producto");
                for (int i = 0; i < arrJsonProducto.length(); i++) {
                    obj = arrJsonProducto.getJSONObject(i);
                    Producto info = new Producto(obj.getString("CUS"), obj.getString("NUS"));
                    arrPro.add(info);
                }
                JSONArray arrJsonProducto_Ruta = retVal.getJSONArray("producto_ruta");
                for (int i = 0; i < arrJsonProducto_Ruta.length(); i++) {
                    obj = arrJsonProducto_Ruta.getJSONObject(i);
                    Producto_RutaAbastecimento info = new Producto_RutaAbastecimento(obj.getString("TaskType"), obj.getString("TaskBusinessKey"), obj.getString("RutaAbastecimiento"), obj.getString("CUS"));
                    arrPro_Ruta.add(info);
                }
                JSONArray arrJsonUser = retVal.getJSONArray("user");
                for (int i = 0; i < arrJsonUser.length(); i++) {
                    obj = arrJsonUser.getJSONObject(i);
                    User info = new User(obj.getString("userid"), obj.getString("password"));
                    arrusers.add(info);
                }
                JSONArray arrJsonType = retVal.getJSONArray("tasktype");
                for (int i = 0; i < arrJsonType.length(); i++) {
                    obj = arrJsonType.getJSONObject(i);
                    TaskType type = new TaskType(obj.getString("type"), obj.getString("name"));
                    arrTypes.add(type);
                }
            }
            return ;
            //((TextView)(findViewById(R.id.text_result))).setText(myResult);
            //Toast.makeText(Db_test.this, "전송 후 결과 받음", 0).show();
        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            //
        } catch (JSONException e) {

        }
        return;
    }
    public int loadProducto(ArrayList<Producto> arrProducto, String RutaAbastecimiento, String Taskbusinesskey, String tasktype)
    {
        String myResult;
        try {
            //   URL 설정하고 접속하기
            URL url = new URL(URL_PRODUCTO);       // URL 설정
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속
            //--------------------------
            //   전송 모드 설정 - 기본적인 설정이다
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true);                         // 서버에서 읽기 모드 지정
            http.setDoOutput(true);                       // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST");         // 전송 방식은 POST

            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            StringBuffer buffer = new StringBuffer();
            buffer.append("RutaAbastecimiento").append("=").append(RutaAbastecimiento).append("&");
            buffer.append("TaskType").append("=").append(tasktype).append("&");
            buffer.append("Taskbusinesskey").append("=").append(Taskbusinesskey);
            OutputStream out = http.getOutputStream();
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();
            // 서버에서 전송받기
            Log.e("Producto", "File Sent, Response: " + String.valueOf(http.getResponseCode()));

            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            myResult = builder.toString();
            final JSONArray arrJson = new JSONArray(myResult.toString());
            for (int i = 0; i < arrJson.length(); i++) {
                int a = 0;
                JSONObject objItem = arrJson.getJSONObject(i);
                Producto info = new Producto(objItem.getString("CUS"), objItem.getString("NUS"));
                arrProducto.add(info);
                a++;
            }
            return 1;
            //((TextView)(findViewById(R.id.text_result))).setText(myResult);
            //Toast.makeText(Db_test.this, "전송 후 결과 받음", 0).show();
        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {

        }
        return 0;
    }
    public int loadTasks(ArrayList<TaskInfo> arrTasks, ArrayList<CompleteTask> arrCompletedTasks, ArrayList<CompltedTinTask> arrTintasks)
    {
        String myResult;
        try {
            //   URL 설정하고 접속하기
            URL url = new URL(URL_LOADTASKS);       // URL 설정
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속
            //--------------------------
            //   전송 모드 설정 - 기본적인 설정이다
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true);                         // 서버에서 읽기 모드 지정
            http.setDoOutput(true);                       // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST");         // 전송 방식은 POST

            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //   서버로 값 전송

            StringBuffer buffer = new StringBuffer();
            buffer.append("userid").append("=").append(String.valueOf(Common.getInstance().getUserID()));                 // php 변수에 값 대입
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();

            // 서버에서 전송받기

            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            myResult = builder.toString();
            final JSONObject retVal = new JSONObject(myResult.toString());
            String strRet = retVal.getString("result");
            if(strRet.equals("success"))
            {
                JSONArray arrJsonTin = retVal.getJSONArray("tin");
                JSONObject obj;
                for (int i = 0; i < arrJsonTin.length(); i++) {
                    obj = arrJsonTin.getJSONObject(i);

                    CompltedTinTask info = new CompltedTinTask(Common.getInstance().getUserID(), Integer.parseInt(obj.getString("Taskid")), obj.getString("TaskType"), obj.getString("RutaAbastecimiento"), obj.getString("CUS"), obj.getString("NUS"), obj.getString("Quantity"));
                    arrTintasks.add(info);
                }
                JSONArray arrJson = retVal.getJSONArray("task");
                for (int i = 0; i < arrJson.length(); i++) {
                    obj = arrJson.getJSONObject(i);

                    TaskInfo info = new TaskInfo(obj.getString("userid"), Integer.parseInt(obj.getString("TaskID")), obj.getString("date"), obj.getString("TaskType"), obj.getString("RutaAbastecimiento"), obj.getString("TaskBusinessKey"),  obj.getString("Customer"), obj.getString("Adress"), obj.getString("LocationDesc"), obj.getString("Model"), obj.getString("Latitude"), obj.getString("Longitude"), obj.getString("EPV"), obj.getString("MachineType"), "", obj.getString("Aux_valor1"));
                    arrTasks.add(info);
                }

                JSONArray arrJson1 = retVal.getJSONArray("complete");
                for (int j = 0; j < arrJson1.length(); j++) {
                    obj = arrJson1.getJSONObject(j);
                    String filePathSignature = "";
                    String filePath1 = "";
                    String filePath2 = "";
                    String filePath3 = "";
                    String filePath4 = "";
                    String filePath5 = "";
                    if(!obj.getString("Signature").equals("")){
                        download(Common.getInstance().server_host + obj.getString("Signature"));
                        filePathSignature = filePath;
                    }
                    if(!obj.getString("image1").equals("")){
                        download(Common.getInstance().server_host + obj.getString("image1"));
                        filePath1 = filePath;
                    }
                    if(!obj.getString("image2").equals("")){
                        download(Common.getInstance().server_host + obj.getString("image2"));
                        filePath2 = filePath;
                    }
                    if(!obj.getString("image3").equals("")){
                        download(Common.getInstance().server_host + obj.getString("image3"));
                        filePath3 = filePath;
                    }
                    if(!obj.getString("image4").equals("")){
                        download(Common.getInstance().server_host + obj.getString("image4"));
                        filePath4 = filePath;
                    }
                    if(!obj.getString("image5").equals("")){
                        download(Common.getInstance().server_host + obj.getString("image5"));
                        filePath5 = filePath;
                    }
                    CompleteTask info = new CompleteTask(Common.getInstance().getUserID(), Integer.parseInt(obj.getString("TaskID")), obj.getString("date"), obj.getString("TaskType"), obj.getString("RutaAbastecimiento"), obj.getString("TaskBusinessKey"), obj.getString("Customer"), obj.getString("Adress"), obj.getString("LocationDesc"), obj.getString("Model"), obj.getString("Latitude"), obj.getString("Longitude"), obj.getString("EPV"), obj.getString("logLatitude"), obj.getString("logLongitude"), obj.getString("ActionDate"), filePath1, filePath2, filePath3, filePath4, filePath5, obj.getString("MachineType"), filePathSignature, obj.getString("NumeroGuia"), obj.getString("Glosa"), obj.getString("Aux_valor1"));
                    arrCompletedTasks.add(info);
                }
                return 0;
            }
            else
                return 1;
        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {

        }
        return -1;
    }
    void download(String path) {
        String[] path_array = path.split("/");
        String FileName = path_array[path_array.length - 1];

        String save_path = Environment.getExternalStorageDirectory() + "/staffapp";

        File dir = new File(save_path);
        if (!dir.exists())
            dir.mkdir();
        save_path += "/" + FileName;
        filePath = save_path;
        if (new File(save_path).exists() == false) {
            //loading.setVisibility(View.VISIBLE);
            dThread = new DownloadThread(path, save_path);
            dThread.start();
        }
    }
    class DownloadThread extends Thread {
        String ServerUrl;
        String LocalPath;

        DownloadThread(String serverPath, String localPath) {
            ServerUrl = serverPath;
            LocalPath = localPath;
        }

        @Override
        public void run() {
            URL videourl;
            int Read;
            try {
                videourl = new URL(ServerUrl);
                HttpURLConnection conn = (HttpURLConnection) videourl
                        .openConnection();
                int len = conn.getContentLength();
                byte[] tmpByte = new byte[len];
                InputStream is = conn.getInputStream();
                File file = new File(LocalPath);
                FileOutputStream fos = new FileOutputStream(file);
                for (;;) {
                    Read = is.read(tmpByte);
                    if (Read <= 0) {
                        break;
                    }
                    fos.write(tmpByte, 0, Read);
                }
                is.close();
                fos.close();
                conn.disconnect();

            } catch (MalformedURLException e) {
                Log.e("ERROR1", e.getMessage());
            } catch (IOException e) {
                Log.e("ERROR2", e.getMessage());
                e.printStackTrace();
            }
        }
    }
    public void HttpFileUpload(String urlString, String fileName, String filepath)
    {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag="fSnd";

        try
        {
            URL url = new URL(URL_UPLOAD_FILE);
            // Open a HTTP connection to the URL
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            // Allow Inputs
            conn.setDoInput(true);

            // Allow Outputs
            conn.setDoOutput(true);

            // Don't use a cached copy.
            conn.setUseCaches(false);

            // Use a post method.
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            Log.e(Tag, "Headers are written");

            // create a buffer of maximum size
            FileInputStream fileInputStream = new FileInputStream(filepath);

            int bytesAvailable = fileInputStream.available();
            int bufferSize = bytesAvailable;
            byte[] buffer = new byte[bufferSize];

            // read file and write it into form...
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = bytesAvailable;
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            fileInputStream.close();
            dos.flush();

            Log.e(Tag,"File Sent, Response: "+String.valueOf(conn.getResponseCode()));

            InputStream is = conn.getInputStream();

            // retrieve the response from server
            int ch;

            StringBuffer b =new StringBuffer();
            while( ( ch = is.read() ) != -1 ){ b.append( (char)ch ); }
            String s=b.toString();
            Log.i("Response", s);
            dos.close();
        }
        catch (MalformedURLException ex)
        {
            Log.e(Tag, "URL error: " + ex.getMessage(), ex);
        }

        catch (IOException ioe)
        {
            Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
        }


    }

    public boolean postLogEvent(LogEvent event) {

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag = "fSnd";

        try {
            URL url = new URL(URL_LOGEVENT);       // URL 설정
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속
            //--------------------------
            //   전송 모드 설정 - 기본적인 설정이다
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true);                         // 서버에서 읽기 모드 지정
            http.setDoOutput(true);                       // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST");         // 전송 방식은 POST1
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //   서버로 값 전송

            StringBuffer buffer = new StringBuffer();
            buffer.append("userid").append("=").append(event.userid).append("&");
            buffer.append("taskid").append("=").append(event.taskid).append("&");
            buffer.append("datetime").append("=").append(event.datetime).append("&");
            buffer.append("description").append("=").append(event.description).append("&");
            buffer.append("latitude").append("=").append(event.latitude).append("&");
            buffer.append("longitude").append("=").append(event.longitude);

            OutputStream out = http.getOutputStream();
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();

            // 서버에서 전송받기
            int status = http.getResponseCode();
            InputStream in;
            if(status >= HttpStatus.SC_BAD_REQUEST)
                in = http.getErrorStream();
            else
                in = http.getInputStream();
            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            String myResult = builder.toString();
            try {
                final JSONObject obj = new JSONObject(myResult.toString());
                String strRet = obj.getString("result");
                if (strRet.equals("success"))
                    return true;
                else
                    return false;
            } catch (JSONException e) {

            }
            //dos.close();
        } catch (MalformedURLException ex) {
            Log.e(Tag, "URL error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
        }

        return false;
    }

    public boolean postTinTask(TinTask task) {

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag = "fSnd";

        try {
            URL url = new URL(URL_UPLOAD_TIN);       // URL 설정
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속
            //--------------------------
            //   전송 모드 설정 - 기본적인 설정이다
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true);                         // 서버에서 읽기 모드 지정
            http.setDoOutput(true);                       // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST");         // 전송 방식은 POST1
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //   서버로 값 전송

            StringBuffer buffer = new StringBuffer();
            buffer.append("userid").append("=").append(task.userid).append("&");
            buffer.append("taskid").append("=").append(String.valueOf(task.taskid)).append("&");
            buffer.append("tasktype").append("=").append(task.tasktype).append("&");
            buffer.append("RutaAbastecimiento").append("=").append(task.RutaAbastecimiento).append("&");
            buffer.append("cus").append("=").append(task.cus).append("&");
            buffer.append("nus").append("=").append(task.nus).append("&");
            buffer.append("quantity").append("=").append(task.quantity);
            OutputStream out = http.getOutputStream();
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();

            // 서버에서 전송받기
            int status = http.getResponseCode();
            InputStream in;
            if(status >= HttpStatus.SC_BAD_REQUEST)
                in = http.getErrorStream();
            else
                in = http.getInputStream();
            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            String myResult = builder.toString();
            try {
                final JSONObject obj = new JSONObject(myResult.toString());
                String strRet = obj.getString("result");
                if (strRet.equals("success"))
                    return true;
                else
                    return false;
            } catch (JSONException e) {

            }
            //dos.close();
        } catch (MalformedURLException ex) {
            Log.e(Tag, "URL error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
        }

        return false;
    }/*
            URL url = new URL(URL_UPLOAD_TIN);
            // Open a HTTP connection to the URL
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
/*
            // Allow Inputs
            conn.setDoInput(true);

            // Allow Outputs
            conn.setDoOutput(true);

            // Don't use a cached copy.
            conn.setUseCaches(false);

            // Use a post method.
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"userid\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(Common.getInstance().getUserID());
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"taskid\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(task.taskid));
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"tasktype\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(task.tasktype);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"RutaAbastecimiento\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(task.RutaAbastecimiento);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"cus\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(task.cus);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"nus\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(task.nus);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"quantity\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(task.quantity);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"quantity\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(task.quantity);
            dos.writeBytes(lineEnd);

            dos.flush();
*/

            //--------------------------
            //   전송 모드 설정 - 기본적인 설정이다
            //--------------------------
    /*
            conn.setDefaultUseCaches(false);
            conn.setDoInput(true);                         // 서버에서 읽기 모드 지정
            conn.setDoOutput(true);                       // 서버로 쓰기 모드 지정
            conn.setRequestMethod("POST");         // 전송 방식은 POST

            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            StringBuffer buffer = new StringBuffer();
            buffer.append("userid").append("=").append(task.userid).append("&");
            buffer.append("taskid").append("=").append(String.valueOf(task.taskid)).append("&");
            buffer.append("tasktype").append("=").append(task.tasktype).append("&");
            buffer.append("RutaAbastecimiento").append("=").append(task.RutaAbastecimiento).append("&");
            buffer.append("cus").append("=").append(task.cus).append("&");
            buffer.append("nus").append("=").append(task.nus).append("&");
            buffer.append("quantity").append("=").append(task.quantity);
            OutputStream out = conn.getOutputStream();
            OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();

            InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF8");

            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            String myResult = builder.toString();
            try {
                final JSONObject obj = new JSONObject(myResult.toString());
                String strRet = obj.getString("result");
                if (strRet.equals("success"))
                    return true;
                else
                    return false;
            }catch (JSONException e){

            }
            //dos.close();
        }
        catch (MalformedURLException ex)
        {
        }

        catch (IOException ioe)
        {
        }

        return false;

    }*/

    public boolean postTask(int taskid, String date, String tasktype, String RutaAbastecimiento, String TaskBusinessKey, String Customer, String Adress, String LocationDesc, String Model, String latitude, String longitude, String epv, String logLatitude, String logLongitude, String ActionDate, String MachineType, String Signature, String NumeroGuia, String Glosa, String Aux_valor1, String[] arrPhoto, int count) {

        String fileNameSignature = "";
        String fileName1 = "";
        String fileName2 = "";
        String fileName3 = "";
        String fileName4 = "";
        String fileName5 = "";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag = "fSnd";
        if(!Signature.equals("")){
            String[] path_array = Signature.split("/");
            fileNameSignature = path_array[path_array.length - 1];
            HttpFileUpload(URL_UPLOAD_FILE, fileNameSignature, Signature);
        }
        if (!arrPhoto[0].equals("")) {
            String[] path_array = arrPhoto[0].split("/");
            fileName1 = path_array[path_array.length - 1];
            HttpFileUpload(URL_UPLOAD_FILE, fileName1, arrPhoto[0]);
        }
        if (!arrPhoto[1].equals("")) {
            String[] path_array = arrPhoto[1].split("/");
            fileName2 = path_array[path_array.length - 1];
            HttpFileUpload(URL_UPLOAD_FILE, fileName2, arrPhoto[1]);
        }
        if (!arrPhoto[2].equals("")) {
            String[] path_array = arrPhoto[2].split("/");
            fileName3 = path_array[path_array.length - 1];
            HttpFileUpload(URL_UPLOAD_FILE, fileName3, arrPhoto[2]);
        }
        if (!arrPhoto[3].equals("")) {
            String[] path_array = arrPhoto[3].split("/");
            fileName4 = path_array[path_array.length - 1];
            HttpFileUpload(URL_UPLOAD_FILE, fileName4, arrPhoto[3]);
        }
        if (!arrPhoto[4].equals("")) {
            String[] path_array = arrPhoto[4].split("/");
            fileName5 = path_array[path_array.length - 1];
            HttpFileUpload(URL_UPLOAD_FILE, fileName5, arrPhoto[4]);
        }
        try {
            Log.e(Tag, "Starting Http File Sending to URL");
            URL url = new URL(URL_UPLOAD);       // URL 설정
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속
            //--------------------------
            //   전송 모드 설정 - 기본적인 설정이다
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true);                         // 서버에서 읽기 모드 지정
            http.setDoOutput(true);                       // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST");         // 전송 방식은 POST1
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //   서버로 값 전송

            StringBuffer buffer = new StringBuffer();
            buffer.append("userid").append("=").append(Common.getInstance().getUserID()).append("&");
            buffer.append("taskid").append("=").append(String.valueOf(taskid)).append("&");
            buffer.append("date").append("=").append(date).append("&");
            buffer.append("tasktype").append("=").append(tasktype).append("&");
            buffer.append("RutaAbastecimiento").append("=").append(RutaAbastecimiento).append("&");
            buffer.append("TaskBusinessKey").append("=").append(TaskBusinessKey).append("&");
            buffer.append("Customer").append("=").append(Customer).append("&");
            buffer.append("Adress").append("=").append(Adress).append("&");
            buffer.append("LocationDesc").append("=").append(LocationDesc).append("&");
            buffer.append("Model").append("=").append(Model).append("&");
            buffer.append("latitude").append("=").append(latitude).append("&");
            buffer.append("longitude").append("=").append(longitude).append("&");
            buffer.append("epv").append("=").append(epv).append("&");
            buffer.append("logLatitude").append("=").append(logLatitude).append("&");
            buffer.append("logLongitude").append("=").append(logLongitude).append("&");
            buffer.append("ActionDate").append("=").append(ActionDate).append("&");
            buffer.append("MachineType").append("=").append(MachineType).append("&");
            buffer.append("Signature").append("=").append(NumeroGuia).append("&");
            buffer.append("NumeroGuia").append("=").append(Glosa).append("&");
            buffer.append("Glosa").append("=").append(fileNameSignature).append("&");
            buffer.append("count").append("=").append(String.valueOf(count)).append("&");
            buffer.append("Aux_valor1").append("=").append(Aux_valor1).append("&");
            buffer.append("file1").append("=").append(fileName1).append("&");
            buffer.append("file2").append("=").append(fileName2).append("&");
            buffer.append("file3").append("=").append(fileName3).append("&");
            buffer.append("file4").append("=").append(fileName4).append("&");
            buffer.append("file5").append("=").append(fileName5);
            OutputStream out = http.getOutputStream();
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();

            // 서버에서 전송받기
            int status = http.getResponseCode();
            InputStream in;
            if (status >= HttpStatus.SC_BAD_REQUEST)
                in = http.getErrorStream();
            else
                in = http.getInputStream();
            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");
            Log.e(Tag, "File Sent, Response: " + String.valueOf(http.getResponseCode()));

            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            String myResult = builder.toString();
            int ch;
            try {
                final JSONObject obj = new JSONObject(myResult.toString());
                String strRet = obj.getString("result");
                if (strRet.equals("success"))
                    return true;
                else
                    return false;
            } catch (JSONException e) {

            }
        } catch (MalformedURLException ex) {
            Log.e(Tag, "URL error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
        }

        return false;
    }

    public boolean postTask1(int taskid, String date, String tasktype, String RutaAbastecimiento, String TaskBusinessKey, String Customer, String Adress, String LocationDesc, String Model, String latitude, String longitude, String epv, String logLatitude, String logLongitude, String estMaq, String moned, String billeter, String tarjet, String nivAb, String higEx, String higIn, String atrSm, String atrSen, String atrIlu, String ActionDate, String MachineType, String[] arrPhoto, int count) {

        int Second = Integer.valueOf(new SimpleDateFormat("ss").format(new Date()));
        /*
        String fileName1 = new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + String.valueOf(Second) + ".jpg";
        String fileName2 = new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + String.valueOf(Second + 1) +  ".jpg";
        String fileName3 = new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + String.valueOf(Second + 2) +  ".jpg";
        String fileName4 = new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + String.valueOf(Second + 3) +  ".jpg";
        String fileName5 = new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + String.valueOf(Second + 4) +  ".jpg";*/
        String fileName1 = "";
        String fileName2 = "";
        String fileName3 = "";
        String fileName4 = "";
        String fileName5 = "";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag="fSnd";
        if(!arrPhoto[0].equals("")){
            String[] path_array = arrPhoto[0].split("/");
            fileName1 = path_array[path_array.length - 1];
            HttpFileUpload(URL_UPLOAD_FILE, fileName1, arrPhoto[0]);
        }
        if(!arrPhoto[1].equals("")){
            String[] path_array = arrPhoto[1].split("/");
            fileName2 = path_array[path_array.length - 1];
            HttpFileUpload(URL_UPLOAD_FILE, fileName2, arrPhoto[1]);
        }
        if(!arrPhoto[2].equals("")){
            String[] path_array = arrPhoto[2].split("/");
            fileName3 = path_array[path_array.length - 1];
            HttpFileUpload(URL_UPLOAD_FILE, fileName3, arrPhoto[2]);
        }
        if(!arrPhoto[3].equals("")){
            String[] path_array = arrPhoto[3].split("/");
            fileName4 = path_array[path_array.length - 1];
            HttpFileUpload(URL_UPLOAD_FILE, fileName4, arrPhoto[3]);
        }
        if(!arrPhoto[4].equals("")){
            String[] path_array = arrPhoto[4].split("/");
            fileName5 = path_array[path_array.length - 1];
            HttpFileUpload(URL_UPLOAD_FILE, fileName5, arrPhoto[4]);
        }
        /*
        if(arrPhoto[1] != null) HttpFileUpload(URL_UPLOAD_FILE, fileName2, arrPhoto[1]);
        if(arrPhoto[2] != null) HttpFileUpload(URL_UPLOAD_FILE, fileName3, arrPhoto[2]);
        if(arrPhoto[3] != null) HttpFileUpload(URL_UPLOAD_FILE, fileName4, arrPhoto[3]);
        if(arrPhoto[4] != null) HttpFileUpload(URL_UPLOAD_FILE, fileName5, arrPhoto[4]);
*/
        try
        {
            Log.e(Tag,"Starting Http File Sending to URL");
            URL url = new URL(URL_UPLOAD);
            // Open a HTTP connection to the URL
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            // Allow Inputs
            conn.setDoInput(true);

            // Allow Outputs
            conn.setDoOutput(true);

            // Don't use a cached copy.
            conn.setUseCaches(false);

            // Use a post method.
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"userid\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(Common.getInstance().getUserID()));
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"taskid\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(taskid));
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"date\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(date);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"tasktype\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(tasktype);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"RutaAbastecimiento\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(RutaAbastecimiento);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"TaskBusinessKey\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(TaskBusinessKey);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"Customer\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(Customer);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"Adress\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(Adress);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"LocationDesc\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(LocationDesc);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"Model\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(Model);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"latitude\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(latitude);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"longitude\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(longitude);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"epv\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(epv);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"logLatitude\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(logLatitude);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"logLongitude\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(logLongitude);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"estMaq\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(estMaq);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"moned\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(moned);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"billeter\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(billeter);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"tarjet\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(tarjet);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"nivAb\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(nivAb);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"higEx\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(higEx);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"higIn\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(higIn);
            dos.writeBytes(lineEnd)
            ;
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"atrSm\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(atrSm);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"atrSen\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(atrSen);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"atrIlu\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(atrIlu);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"ActionDate\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(ActionDate);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"MachineType\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(MachineType);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"count\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(count));
            dos.writeBytes(lineEnd);

            if(!arrPhoto[0].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file1\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(fileName1);
                dos.writeBytes(lineEnd);
            }

            if(!arrPhoto[1].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file2\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(fileName2);
                dos.writeBytes(lineEnd);
            }

            if(!arrPhoto[2].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file3\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(fileName3);
                dos.writeBytes(lineEnd);
            }

            if(!arrPhoto[3].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file4\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(fileName4);
                dos.writeBytes(lineEnd);
            }

            if(!arrPhoto[4].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file5\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(fileName5);
                dos.writeBytes(lineEnd);
            }
            if(!arrPhoto[0].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName1 + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e(Tag, "Headers are written");

                // create a buffer of maximum size
                FileInputStream fileInputStream = new FileInputStream(arrPhoto[0]);

                int bytesAvailable = fileInputStream.available();
                int bufferSize = bytesAvailable;
                byte[] buffer = new byte[bufferSize];

                // read file and write it into form...
                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = bytesAvailable;
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // close streams
                fileInputStream.close();
            }

            if(!arrPhoto[1].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName2 + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e(Tag, "Headers are written");

                // create a buffer of maximum size
                FileInputStream fileInputStream = new FileInputStream(arrPhoto[1]);

                int bytesAvailable = fileInputStream.available();
                int bufferSize = bytesAvailable;
                byte[] buffer = new byte[bufferSize];

                // read file and write it into form...
                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = bytesAvailable;
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // close streams
                fileInputStream.close();
            }

            if(!arrPhoto[2].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName3 + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e(Tag, "Headers are written");

                // create a buffer of maximum size
                FileInputStream fileInputStream = new FileInputStream(arrPhoto[2]);

                int bytesAvailable = fileInputStream.available();
                int bufferSize = bytesAvailable;
                byte[] buffer = new byte[bufferSize];

                // read file and write it into form...
                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = bytesAvailable;
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // close streams
                fileInputStream.close();
            }

            if(!arrPhoto[3].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName4 + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e(Tag, "Headers are written");

                // create a buffer of maximum size
                FileInputStream fileInputStream = new FileInputStream(arrPhoto[3]);

                int bytesAvailable = fileInputStream.available();
                int bufferSize = bytesAvailable;
                byte[] buffer = new byte[bufferSize];

                // read file and write it into form...
                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = bytesAvailable;
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // close streams
                fileInputStream.close();
            }

            if(!arrPhoto[4].equals("")){
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName5 + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e(Tag, "Headers are written");

                // create a buffer of maximum size
                FileInputStream fileInputStream = new FileInputStream(arrPhoto[4]);

                int bytesAvailable = fileInputStream.available();
                int bufferSize = bytesAvailable;
                byte[] buffer = new byte[bufferSize];

                // read file and write it into form...
                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = bytesAvailable;
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // close streams
                fileInputStream.close();
            }
            dos.flush();

            Log.e(Tag,"File Sent, Response: "+String.valueOf(conn.getResponseCode()));

            InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF8");

            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            String myResult = builder.toString();
            int ch;
            try {
                final JSONObject obj = new JSONObject(myResult.toString());
                String strRet = obj.getString("result");
                if (strRet.equals("success"))
                    return true;
                else
                    return false;
            }catch (JSONException e){

            }
            dos.close();
        }
        catch (MalformedURLException ex)
        {
            Log.e(Tag, "URL error: " + ex.getMessage(), ex);
        }

        catch (IOException ioe)
        {
            Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
        }

        return false;

    }

    private String urlEncode(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, UTF8);
    }
    protected JSONObject getResponseData(String strUrl, Map<String, Object> params, boolean bIsAbsoluteUri) {
        try {
            return new JSONObject(getServerResponse(strUrl, params, bIsAbsoluteUri));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    protected JSONObject getResponseData(String strUrl, Map<String, Object> params) {
        return getResponseData(strUrl, params, false);
    }
    protected JSONArray getResponseArray(String strUrl, Map<String, Object> params) {
        try {
            return new JSONArray(getServerResponse(strUrl, params));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    protected String getServerResponse(String strUrl, Map<String, Object> params, boolean bIsAbsoluteUri) {
        try {
            URL url;
            if(bIsAbsoluteUri) {
                url = new URL(strUrl);
            } else {
                url = new URL(SERVER_URL + strUrl);
            }

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String,Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            String inputLine;
            String repString = "";

            while ((inputLine = in.readLine()) != null) {
                repString = repString + inputLine;
            }
            in.close();
            return repString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    protected String getServerResponse(String strUrl, Map<String, Object> params) {
        return getServerResponse(strUrl, params, false);
    }

}
