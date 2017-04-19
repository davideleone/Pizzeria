package app.gestionale;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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
    private ArrayList<String> listaCitta;
    private ArrayAdapter<String> adapterCitta;
    private Spinner spinnerCitta;


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
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.activity_clienti, container, false);
        context = view.getContext();
        super.onCreate(savedInstanceState);
        tableClienti = (TableLayout) view.findViewById(R.id.tabell_clienti);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        RelativeLayout layoutInserimentoToolbar = (RelativeLayout) toolbar.findViewById(R.id.layoutInserimentoToolbar);
        layoutInserimentoToolbar.setVisibility(View.GONE);

        spinnerCitta = new Spinner(context);

        new HttpManager.AsyncManager(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                inizializzaCitta(output);
            }
        }, context, "GET_CITTA", new String[]{}).execute();

        aggiornaClienti();

        FloatingActionButton btnNuovoCliente = (FloatingActionButton) view.findViewById(R.id.btn_Aggiungi);
        btnNuovoCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostraDialog(null);
            }
        });
        return view;
    }

    private void mostraDialog(Object param) {
        RelativeLayout inserimentoLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams paramInserimento = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramInserimento.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramInserimento.addRule(RelativeLayout.CENTER_VERTICAL);
        paramInserimento.setMargins(0, 30, 0, 0);
        inserimentoLayout.setLayoutParams(paramInserimento);

        RelativeLayout.LayoutParams paramBarra = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.dim_2dp));
        paramBarra.setMargins(30, 20, 30, 10);
        paramBarra.addRule(RelativeLayout.CENTER_HORIZONTAL);

        View view = new View(context);
        view.setId(View.generateViewId());
        view.setBackgroundColor(getResources().getColor(R.color.celeste));
        view.setLayoutParams(paramBarra);
        inserimentoLayout.addView(view);

        RelativeLayout.LayoutParams editTextParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_350dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
        editTextParams.addRule(RelativeLayout.BELOW, view.getId());
        TextInputLayout nomeInput = new TextInputLayout(context);
        editTextParams.setMargins(40, 15, 0, 40);
        nomeInput.setLayoutParams(editTextParams);
        nomeInput.setId(View.generateViewId());

        final TextInputEditText nome = new TextInputEditText(context);
        final TextInputEditText cognome = new TextInputEditText(context);
        final TextInputEditText telefono = new TextInputEditText(context);
        final TextInputEditText via = new TextInputEditText(context);

        nome.setTextSize(25);
        nome.setHint("Nome");
        nomeInput.addView(nome);

        RelativeLayout.LayoutParams editTextCognomeParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_350dp), RelativeLayout.LayoutParams.WRAP_CONTENT);

        TextInputLayout cognomeInput = new TextInputLayout(context);
        cognomeInput.setId(View.generateViewId());
        editTextCognomeParams.addRule(RelativeLayout.BELOW, nomeInput.getId());
        editTextCognomeParams.setMargins(40, 0, 0, 40);
        cognomeInput.setLayoutParams(editTextCognomeParams);
        cognome.setTextSize(25);
        cognome.setHint("Cognome");
        cognomeInput.addView(cognome);

        RelativeLayout.LayoutParams editTextTelefonoParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_350dp), RelativeLayout.LayoutParams.WRAP_CONTENT);

        TextInputLayout telefonoInput = new TextInputLayout(context);
        telefonoInput.setId(View.generateViewId());
        editTextTelefonoParams.addRule(RelativeLayout.BELOW, cognomeInput.getId());
        editTextTelefonoParams.setMargins(40, 0, 0, 40);
        telefonoInput.setLayoutParams(editTextTelefonoParams);
        telefono.setTextSize(25);
        telefono.setHint("Telefono");
        telefono.setInputType(InputType.TYPE_CLASS_PHONE);
        telefonoInput.addView(telefono);

        RelativeLayout.LayoutParams editTextViaParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_200dp), RelativeLayout.LayoutParams.WRAP_CONTENT);

        TextInputLayout viaInput = new TextInputLayout(context);
        viaInput.setId(View.generateViewId());
        editTextViaParams.addRule(RelativeLayout.BELOW, telefonoInput.getId());
        editTextViaParams.setMargins(40, 0, 0, 40);
        viaInput.setLayoutParams(editTextViaParams);
        via.setTextSize(25);
        via.setHint("Via/P.zza/Loc.");
        viaInput.addView(via);

        RelativeLayout.LayoutParams editTextCittaTxtParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_100dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
        editTextCittaTxtParams.addRule(RelativeLayout.BELOW, viaInput.getId());
        editTextCittaTxtParams.setMargins(40, 0, 0, 40);

        TextView txtCitta = new TextView(context);
        txtCitta.setTextSize(25);
        txtCitta.setText("Citta'");
        txtCitta.setId(View.generateViewId());
        txtCitta.setLayoutParams(editTextCittaTxtParams);

        RelativeLayout.LayoutParams editTextCittaSpinnerParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dim_200dp), RelativeLayout.LayoutParams.WRAP_CONTENT);
        editTextCittaSpinnerParams.addRule(RelativeLayout.BELOW, viaInput.getId());
        editTextCittaSpinnerParams.addRule(RelativeLayout.END_OF, txtCitta.getId());
        editTextCittaSpinnerParams.addRule(RelativeLayout.ALIGN_BASELINE, txtCitta.getId());

        editTextCittaSpinnerParams.setMargins(40, 0, 0, 40);


        spinnerCitta.setLayoutParams(editTextCittaSpinnerParams);

        inserimentoLayout.addView(nomeInput);
        inserimentoLayout.addView(cognomeInput);
        inserimentoLayout.addView(telefonoInput);
        inserimentoLayout.addView(viaInput);
        inserimentoLayout.addView(txtCitta);
        if (spinnerCitta.getParent() != null)
            ((ViewGroup) spinnerCitta.getParent()).removeView(spinnerCitta);
        inserimentoLayout.addView(spinnerCitta);

        String idTemp = "";
        if (param != null) {
            HashMap<String, String> hashDati = (HashMap<String, String>) param;
            final String idCliente = hashDati.get("id");
            final String strNome = hashDati.get("nome");
            final String strCognome = hashDati.get("cognome");
            final String strTelefono = hashDati.get("telefono");
            final String strVia = hashDati.get("via");
            final String strCitta = hashDati.get("citta");


            idTemp = idCliente;
            nome.setText(strNome);
            cognome.setText(strCognome);
            telefono.setText(strTelefono);
            via.setText(strVia);
            spinnerCitta.setSelection(listaCitta.indexOf(strCitta));
        }

        final String ID_CLIENTE = idTemp;

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(inserimentoLayout)
                .setTitle("Inserisci Nuovo Cliente")
                .setPositiveButton("Inserisci", null)
                .setNegativeButton("Annulla", null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean toClose = true;
                String strNome = nome.getText().toString();
                String strCognome = cognome.getText().toString();
                String strTelefono = telefono.getText().toString();
                String strVia = via.getText().toString();
                String strCitta = spinnerCitta.getSelectedItem().toString();

                if (strNome.isEmpty()) {
                    toClose = false;
                    nome.setError("Inserisci nome");
                }
                if (strCognome.isEmpty()) {
                    toClose = false;
                    cognome.setError("Inserisci cognome");
                }
                if (strTelefono.isEmpty() || strTelefono.length() != 10) {
                    toClose = false;
                    telefono.setError("Inserisci telefono");
                }
                if (strVia.isEmpty()) {
                    toClose = false;
                    via.setError("Inserisci via");
                }

                if (toClose) {
                    if (ID_CLIENTE.isEmpty()) {
                        new HttpManager.AsyncManager(new AsyncResponse() {
                            @Override
                            public void processFinish(Object output) {
                                Toast.makeText(context, "Cliente inserito correttamente", Toast.LENGTH_SHORT).show();
                            }
                        }, context, "INSERISCI_CLIENTE", new String[]{strCognome, strNome, strTelefono, strVia, strCitta}).execute();
                    } else {
                        new HttpManager.AsyncManager(new AsyncResponse() {
                            @Override
                            public void processFinish(Object output) {
                                Toast.makeText(context, "Cliente aggiornato correttamente", Toast.LENGTH_SHORT).show();
                            }
                        }, context, "AGGIORNA_CLIENTE", new String[]{strCognome, strNome, strTelefono, strVia, strCitta, ID_CLIENTE}).execute();
                    }

                    aggiornaClienti();
                    dialog.dismiss();
                }
            }
        });
    }

    private void aggiornaClienti() {
        new HttpManager.AsyncManager(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                fixClienti(output);
            }
        }, context, "GET_LISTA_UTENTI", new String[]{}).execute();
    }

    private void fixClienti(Object param) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itr = lista.iterator();
        hashColonne = new SparseArray<HashMap<String, String>>();
        while (itr.hasNext()) {
            HashMap<String, String> riga = itr.next();
            final int idcliente = Integer.parseInt(riga.get("id"));
            HashMap<String, String> row = new HashMap<String, String>(5);
            row.put("cognome", riga.get("cognome"));
            row.put("nome", riga.get("nome"));
            row.put("telefono", riga.get("telefono"));
            row.put("via", (riga.get("via").equals("null") ? "----- " : riga.get("via")));
            row.put("citta", (riga.get("citta").equals("null") ? "----- " : riga.get("citta")));
            hashColonne.put(idcliente, row);
        }

        riempiTabellaClienti();
    }

    private void riempiTabellaClienti() {
        tableClienti.removeViews(1, tableClienti.getChildCount() - 1);
        for (int i = 0; i < hashColonne.size(); i++) {
            final HashMap<String, String> listaCliente = hashColonne.valueAt(i);

            final int idcliente = hashColonne.keyAt(i);
            final String nome = listaCliente.get("nome");
            final String cognome = listaCliente.get("cognome");
            final String telefono = listaCliente.get("telefono");
            final String via = listaCliente.get("via");
            final String citta = listaCliente.get("citta");

            listaCliente.put("id", Integer.toString(idcliente));

            TableRow row = new TableRow(context);
            row.setBackgroundResource(R.drawable.table_bottom_style);

            TextView txtNome = makeTableRowWithText(nome, R.dimen.dim_200dp);
            TextView txtCognome = makeTableRowWithText(cognome, R.dimen.dim_200dp);
            TextView txtTelefono = makeTableRowWithText(telefono, R.dimen.dim_200dp);
            TextView txtVia = makeTableRowWithText(via, R.dimen.dim_200dp);
            TextView txtCitta = makeTableRowWithText(citta, R.dimen.dim_200dp);
            ImageButton btnModifica = makeTableRowWithImageButton(R.drawable.modifica);
            ImageButton btnElimina = makeTableRowWithImageButton(R.drawable.elimina);

            btnModifica.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostraDialog(listaCliente);
                }
            });

            btnElimina.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("Elimina cliente")
                            .setMessage("Sei sicuro di voler eliminare il cliente " + cognome + "?")
                            .setPositiveButton("Si, elimina", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new HttpManager.AsyncManager(new AsyncResponse() {
                                        @Override
                                        public void processFinish(Object output) {
                                            aggiornaClienti();
                                            Toast.makeText(context, "Cliente Eliminato!", Toast.LENGTH_SHORT).show();
                                        }
                                    }, context, "ELIMINA_CLIENTE", new String[]{Integer.toString(idcliente)}).execute();
                                }
                            })
                            .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setIcon(R.drawable.logo)
                            .show();
                }
            });

            row.addView(txtNome);
            row.addView(txtCognome);
            row.addView(txtTelefono);
            row.addView(txtVia);
            row.addView(txtCitta);
            row.addView(btnModifica);
            row.addView(btnElimina);
            tableClienti.addView(row);
        }
    }


    private void inizializzaCitta(Object param) {
        List<HashMap<String, String>> lista = (List<HashMap<String, String>>) param;
        Iterator<HashMap<String, String>> itrAgg = lista.iterator();
        listaCitta = new ArrayList<>();
        while (itrAgg.hasNext()) {
            HashMap<String, String> riga = itrAgg.next();
            final String nomeCitta = riga.get("nome");
            listaCitta.add(nomeCitta);
        }
        adapterCitta = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, listaCitta);
        spinnerCitta.setAdapter(adapterCitta);
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
        recyclableImageButton.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.dim_80dp));
        recyclableImageButton.setMaxHeight(getResources().getDimensionPixelSize(R.dimen.dim_26dp));
        recyclableImageButton.setPadding(40, 0, 0, 0);
        recyclableImageButton.setImageResource(img);
        recyclableImageButton.setBackgroundColor(Color.TRANSPARENT);
        return recyclableImageButton;
    }
}
