package br.digitalhouse.agendatelefonicabd.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.digitalhouse.agendatelefonicabd.R;
import br.digitalhouse.agendatelefonicabd.adapter.ContatoAdapter;
import br.digitalhouse.agendatelefonicabd.data.ContatoDAO;
import br.digitalhouse.agendatelefonicabd.data.ContatoDataBase;
import br.digitalhouse.agendatelefonicabd.interfaces.ContatoListener;
import br.digitalhouse.agendatelefonicabd.model.Contato;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ContatoListener {

    private TextInputLayout nomeEdit;
    private TextInputLayout telefoneEdit;
    private FloatingActionButton bntDelete;
    private FloatingActionButton bntAdd;
    private RecyclerView recyclerViewContatos;
    private ContatoAdapter adapter;
    private ContatoDAO contatoDAO;
    private List<Contato> listaContatos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        ///Inicialização para utilizar o banco de dados
        ContatoDataBase dataBase = ContatoDataBase.getDatabase(this);
        contatoDAO = dataBase.contatoDAO();

        bntAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                insereBancoDados();
            }
        });

        bntDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detelaBancoDados();
            }
        });

        //Para atuar com adpater é preciso ter uma interface para ter o click
        adapter = new ContatoAdapter(listaContatos, this);
        recyclerViewContatos.setAdapter(adapter);
        recyclerViewContatos.setLayoutManager(new LinearLayoutManager(this));
        pegaDadosBanco(adapter);
    }

    //verifica se o valor não é nulo, abre um nova thered, verifica no banco, encontra registro, e retorna um objeto do tipo contato e depois deleta
    private void detelaBancoDados() {
        final String nomeRecuperado = nomeEdit.getEditText().getText().toString();

        if (!nomeRecuperado.isEmpty()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                   Contato contato = contatoDAO.getContatoNome(nomeRecuperado);
                   contatoDAO.deletaContato(contato);
                }
            }).start();
        }
    }


    //pegar os valores, ve se não é nulo, abre um nova thread e adiciona no banco de dados.
    private void insereBancoDados() {
        String novoNome = nomeEdit.getEditText().getText().toString();
        String novoTelefone = telefoneEdit.getEditText().getText().toString();

        if (!novoNome.isEmpty() && !novoTelefone.isEmpty()){

            final Contato contato = new Contato(novoNome, novoTelefone);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    contatoDAO.insereContato(contato);
                }
            }).start();

            Snackbar.make(nomeEdit, "Contato adicionado com sucesso!", Snackbar.LENGTH_LONG);
        }
    }

    public void initViews (){
        nomeEdit = findViewById(R.id.textInputLayoutNome);
        telefoneEdit = findViewById(R.id.textInputLayoutTelefone);
        bntAdd = findViewById(R.id.floatingActionButtonAdd);
        bntDelete = findViewById(R.id.floatingActionButtonDelete);
        recyclerViewContatos = findViewById(R.id.recyclerViewContatos);
    }

    private void pegaDadosBanco(final ContatoAdapter adapter){
        new Thread(new Runnable() {
            @Override
            public void run() {
                contatoDAO.getTodosContatos().observe(MainActivity.this, new Observer<List<Contato>>() {
                    @Override
                    public void onChanged(final List<Contato> novaListaContatos) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.atualizaLista(novaListaContatos);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    @Override
    public void clickContato(Contato contato) {
        nomeEdit.getEditText().setText(contato.getNome());
        telefoneEdit.getEditText().setText(contato.getTelefone());

    }
}
