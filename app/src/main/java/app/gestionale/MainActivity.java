package app.gestionale;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    ArrayList<String> listaPizze;
    ArrayList<String> listaIngredienti;
    ArrayList<String> listaPrezzi;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private ImageView logo;
    private FrameLayout frameLayout;
    private RelativeLayout layoutAperti;
    private RelativeLayout layoutChiusi;
    private Calendar c = Calendar.getInstance();
    private int seconds = c.get(Calendar.SECOND);
    private int hour = c.get(Calendar.HOUR_OF_DAY);
    private int day = c.get(Calendar.DAY_OF_WEEK);
    private TextView nomeAct;
    private String idOrdine = "";
    private String modifica = "";
    private SparseArray<HashMap<String, String>> hashColonne;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        frameLayout = (FrameLayout) findViewById(R.id.flContent);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        View hView = nvDrawer.getHeaderView(0);

        setupDrawerContent(nvDrawer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);





        //logo = (ImageView) findViewById(R.id.logo_accipizza);
    }

    private void riempiClienti(Object param) {
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
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }


    public void selectDrawerItem(MenuItem menuItem) {

        Fragment fragment = null;
        Class fragmentClass;
        Bundle bundle = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragmentClass = RiepilogoOrdini.class;
                break;
            case R.id.nav_second_fragment:
                fragmentClass = Fattorini.class;
                break;
            case R.id.nav_third_fragment:
                fragmentClass = Clienti.class;
                break;
            default:
                fragmentClass = MainActivity.class;
        }

        try {
            if (fragmentClass != null)
                fragment = (Fragment) fragmentClass.newInstance();
            else fragment = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        if (fragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(false);
            setTitle(menuItem.getTitle());
            mDrawer.closeDrawers();
        }

    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
        layoutTheView();
    }

    private void layoutTheView() {
        ActionBar actionBar = this.getSupportActionBar();
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
        int actionBarHeight = actionBar.getHeight();
        params.setMargins(0, actionBarHeight, 0, 0);
        frameLayout.setLayoutParams(params);
        frameLayout.requestLayout();
    }


    @Override
    public void onBackPressed() {
    }

}