package app.gestionale;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Clienti extends Fragment {

    private TableLayout tabellaOrdini;
    private FragmentActivity listener;
    private Bundle bundle;
    private Context context;
    private TextView recyclableTextView;
    private ImageButton recyclableImageButton;
    private SparseArray<HashMap<String, String>> hashColonne;
    private TableLayout tableClienti;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.listener = (FragmentActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        bundle = this.getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.activity_clienti, container, false);
        context = view.getContext();
        super.onCreate(savedInstanceState);
        tableClienti = (TableLayout) view.findViewById(R.id.tabell_clienti);

        new HttpManager.AsyncManager(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                fixClienti(output);
            }
        }, context, "GET_LISTA_UTENTI", new String[]{}).execute();

        return view;
    }

    private void fixClienti(Object param) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        hashColonne = new SparseArray<HashMap<String, String>>();
        while (itr.hasNext()) {
            HashMap<String, String> riga = itr.next();
            final int idcliente = Integer.parseInt(riga.get("id"));
            HashMap<String, String> row = new HashMap<String, String>(6);
            row.put("cognome", riga.get("cognome"));
            row.put("nome", riga.get("nome"));
            row.put("prefisso", riga.get("prefisso"));
            row.put("telefono", riga.get("telefono"));
            row.put("via", riga.get("via"));
            row.put("citta", riga.get("citta"));
            hashColonne.put(idcliente, row);
        }

        riempiTabellaClienti(hashColonne);
    }

    private void riempiTabellaClienti(SparseArray<HashMap<String, String>> hashclienti) {
        Toast.makeText(context, "Sono entrato!", Toast.LENGTH_SHORT).show();
        for (int i = 0; i < hashclienti.size(); i++) {
            HashMap<String, String> listaCliente = hashclienti.valueAt(i);

            final String nome = listaCliente.get("nome");
            final String cognome = listaCliente.get("cognome");
            final String telefono = listaCliente.get("prefisso") + listaCliente.get("telefono");
            final String via = listaCliente.get("via");
            final String citta = listaCliente.get("citta");

            TableRow row = new TableRow(context);
            row.setBackgroundResource(R.drawable.table_bottom_style);

            TextView txtNome = makeTableRowWithText(nome, R.dimen.dim_200dp);
            TextView txtCognome = makeTableRowWithText(cognome, R.dimen.dim_200dp);
            TextView txtTelefono = makeTableRowWithText(telefono, R.dimen.dim_200dp);
            TextView txtVia = makeTableRowWithText(via, R.dimen.dim_200dp);
            TextView txtCitta = makeTableRowWithText(citta, R.dimen.dim_200dp);

            row.addView(txtNome);
            row.addView(txtCognome);
            row.addView(txtTelefono);
            row.addView(txtVia);
            row.addView(txtCitta);
            tableClienti.addView(row);
        }
    }


    private TextView makeTableRowWithText(String text, int resource) {
        recyclableTextView = new TextView(context);
        recyclableTextView.setGravity(Gravity.CENTER);
        recyclableTextView.setText(text);
        recyclableTextView.setTextSize(25);
        recyclableTextView.setMinimumWidth(getResources().getDimensionPixelSize(resource));
        return recyclableTextView;
    }

    private ImageButton makeTableRowWithImageButton(int img) {
        recyclableImageButton = new ImageButton(context);
        recyclableImageButton.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.dim_45dp));
        recyclableImageButton.setImageResource(img);
        recyclableImageButton.setBackgroundColor(Color.TRANSPARENT);
        return recyclableImageButton;
    }
}
