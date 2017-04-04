package app.gestionale;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class RiepilogoOrdini extends AppCompatActivity {

    private Spinner listadate;
    private ArrayAdapter<String> listaDate;
    private TableLayout tabellaOrdini;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riepilogo_ordini);

        tabellaOrdini = (TableLayout) findViewById(R.id.tabella_ordini);
        listadate = (Spinner) findViewById(R.id.date_settimane);

        String[] dateSettimana;
        Calendar c = Calendar.getInstance(Locale.ITALY);
        Calendar c_copia = Calendar.getInstance(Locale.ITALY);

        dateSettimana = new String[8 - (c.get(Calendar.DAY_OF_WEEK) - 1)];
        int count = 0;
        String giorno = "";
        String mese = "";
        String anno = "";
        for (int i = c.get(Calendar.DAY_OF_WEEK) - 1; i < 7; i++) {
            giorno = (c_copia.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + (c_copia.get(Calendar.DAY_OF_MONTH)) : "" + (c_copia.get(Calendar.DAY_OF_MONTH));
            mese = (c_copia.get(Calendar.MONTH) + 1 < 10) ? "0" + (c_copia.get(Calendar.MONTH) + 1) : "" + (c_copia.get(Calendar.MONTH) + 1);
            anno = "" + (c_copia.get(Calendar.YEAR));
            dateSettimana[count] = giorno + "-" + mese + "-" + anno;
            c_copia.add(Calendar.DATE, 1);
            count++;
        }
        giorno = (c_copia.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + (c_copia.get(Calendar.DAY_OF_MONTH)) : "" + (c_copia.get(Calendar.DAY_OF_MONTH));
        mese = (c_copia.get(Calendar.MONTH) + 1 < 10) ? "0" + (c_copia.get(Calendar.MONTH) + 1) : "" + (c_copia.get(Calendar.MONTH) + 1);
        anno = "" + (c_copia.get(Calendar.YEAR));

        dateSettimana[dateSettimana.length - 1] = giorno + "-" + mese + "-" + anno;

        listaDate = new ArrayAdapter<String>(this, R.layout.content_spinner, dateSettimana); //selected item will look like a spinner set from XML
        listaDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listadate.setAdapter(listaDate);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        caricaOrdini();
    }

    public void caricaOrdini() {
        List<HashMap<String, Object>> risultatoQuery2;
        risultatoQuery2 = DBmanager.selectQuery(EnumQuery.MONITORA_ORDINE.getValore(), listadate.getSelectedItem().toString());
        Iterator<HashMap<String, Object>> itr2 = risultatoQuery2.iterator();
    if(itr2.hasNext()) {
        while (itr2.hasNext()) {
            HashMap<String, Object> riga = itr2.next();
            final String idOrdine = riga.get("id").toString();
            final String stato = riga.get("tipo").toString();
            final String consegna = riga.get("consegna").toString();
            final String totale = new DecimalFormat("#0.00").format((double) Float.parseFloat(riga.get("totale").toString())) + " \u20ac";
            final String social = riga.get("social").toString();

            TableRow row = new TableRow(this);
            row.setBackgroundResource(R.drawable.table_bottom_style);

            TextView dataOrdine = makeTableRowWithText(riga.get("dataconsegna").toString(), R.dimen.dim_150dp);
            TextView oraOrdine = makeTableRowWithText(riga.get("oraconsegna").toString().substring(0, 5), R.dimen.dim_80dp);
            TextView nomeCliente = makeTableRowWithText((riga.get("nome").toString()), R.dimen.dim_150dp);
            TextView cognomeCliente = makeTableRowWithText(riga.get("cognome").toString(), R.dimen.dim_150dp);
            TextView totaleOrdine = makeTableRowWithText(totale, R.dimen.dim_100dp);
            TextView viaOrdine = makeTableRowWithText(riga.get("via").toString(), R.dimen.dim_150dp);
            TextView cittaOrdine = makeTableRowWithText(riga.get("citta").toString(), R.dimen.dim_150dp);

            row.addView(dataOrdine);
            row.addView(oraOrdine);
            row.addView(nomeCliente);
            row.addView(cognomeCliente);
            row.addView(totaleOrdine);
            row.addView(viaOrdine);
            row.addView(cittaOrdine);

            tabellaOrdini.addView(row);
        }
    }
    else Toast.makeText(this, "Nessun nuovo ordine", Toast.LENGTH_SHORT).show();
    }

    private TextView recyclableTextView;
    private ImageButton recyclableImageButton;

    private TextView makeTableRowWithText(String text, int resource) {
        recyclableTextView = new TextView(this);
        recyclableTextView.setGravity(Gravity.CENTER);
        recyclableTextView.setText(text);
        recyclableTextView.setTextSize(25);
        recyclableTextView.setMinimumWidth(getResources().getDimensionPixelSize(resource));
        return recyclableTextView;
    }

    private ImageButton makeTableRowWithImageButton(int img) {
        TableLayout.LayoutParams ImageButtonLayout = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        ImageButtonLayout.setMargins(10, 0, 0, 0);
        recyclableImageButton = new ImageButton(this);
        recyclableImageButton.setLayoutParams(ImageButtonLayout);
        recyclableImageButton.setBackgroundResource(R.drawable.button);
        return recyclableImageButton;
    }

}
