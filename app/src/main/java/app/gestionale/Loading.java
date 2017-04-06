package app.gestionale;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

public class Loading extends AsyncTask<Void, Integer, Void> {

    ProgressDialog dialog;
    ProgressBar bar;
    Context context;


    Loading(ProgressBar bar, Context context) {
        this.bar = bar;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        bar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (dialog != null) dialog.setProgress(values[0]);
        if (bar != null) bar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void result) {
        bar.setVisibility(View.GONE);
    }


}
