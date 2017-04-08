package app.gestionale;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class Loading extends AsyncTask<Void, Integer, Void> {

    ProgressDialog dialog;
    ProgressBar bar;
    Context context;
    RelativeLayout layoutPizze;


    Loading(ProgressBar bar, Context context) {
        this.bar = bar;
        this.context = context;
    }

    Loading(ProgressBar bar, Context context, RelativeLayout layoutPizze) {
        this.bar = bar;
        this.context = context;
        this.layoutPizze = layoutPizze;
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
