package app.gestionale;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HttpManager {

    private static String convertInputStreamToString(InputStream is){
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static void execSimple(String query, Context context, String... parametri){
        new HttpManager.AsyncManager(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {};
        }, context, query, parametri).execute();
    }

    static class AsyncManager extends AsyncTask<String, String, List<HashMap<String, String>>> {
        private final String ID_CLIENTE = "PIZZERIA_ACCIPIZZA_VILLAFRANCA";

        public AsyncResponse delegate = null;//Call back interface
        private String query;
        private String[] parametri;
        private String strPost;
        private Context context;
        private List<JSONObject> jsonList = null;
        private List<HashMap<String, String>> listaRisultati;
        private ProgressDialog progressDialog;

        AsyncManager(AsyncResponse callback, Context context, String query, String[] parametri){
            this.query = query;
            this.parametri = parametri;
            this.delegate = callback;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            if (context != null) progressDialog = ProgressDialog.show(context, "", "Un attimo di pazienza...", true);
            listaRisultati = new ArrayList<HashMap<String, String>>();
            strPost = "id_cliente=" + ID_CLIENTE + "&exec_query=" + query;
            for (int i = 0; i < parametri.length; i++) {
                strPost += "&param" + i + "=" + parametri[i];
            }
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... params) {
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;

            try{
                URL url = new URL("http://ldvdevtest.x10host.com/get_dati");
                urlConnection = (HttpURLConnection) url.openConnection();
                byte[] postData       = strPost.getBytes( StandardCharsets.UTF_8 );
                int    postDataLength = postData.length;

                urlConnection.setUseCaches( false );
                urlConnection.setDoOutput( true );
                urlConnection.setInstanceFollowRedirects( false );
                urlConnection.setRequestMethod( "POST" );
                urlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty( "charset", "utf-8");
                urlConnection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
                urlConnection.setUseCaches( false );
                try (DataOutputStream wr = new DataOutputStream( urlConnection.getOutputStream())) {
                    wr.write( postData );
                }

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    String response = convertInputStreamToString(inputStream);

                    JSONObject jObject = new JSONObject(response);
                    if(jObject.has("status")) {
                        if (Integer.parseInt(jObject.getString("status")) == 0) {
                            if (jObject.has("output")) {
                                JSONArray jsonArray = jObject.getJSONArray("output");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    int colonne = obj.names().length();
                                    HashMap<String, String> row = new HashMap<String, String>(colonne);
                                    for (int k = 0; k < colonne; k++) {
                                        String nomeCol = obj.names().getString(k);
                                        Object valCol = obj.get(nomeCol);
                                        String isNull = (valCol == null ? "" : valCol.toString());
                                        row.put(nomeCol, isNull);
                                    }
                                    listaRisultati.add(row);
                                }
                            } else if(jObject.has("generated_id")) {
                                String generatedID = jObject.getString("generated_id");
                                HashMap<String, String> row = new HashMap<String, String>(1);
                                row.put("generated_id", generatedID);
                                listaRisultati.add(row);
                            }
                        }else{
                            System.out.println("ERROR = " + jObject.getString("error"));
                        }
                    }
                }
            } catch(ProtocolException e1){
                System.out.println(e1.getMessage());
            } catch(IOException e1) {
                System.out.println(e1.getMessage());
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            } finally {
                if(inputStream != null)
                    try {
                        inputStream.close();
                    } catch (IOException e) {}
                if(urlConnection != null) urlConnection.disconnect();
            }
            return listaRisultati;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> jsonObjects) {
            if(progressDialog != null) progressDialog.dismiss();
            delegate.processFinish(listaRisultati);
        }
    }

}
