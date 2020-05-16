package wgc.myfirebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import wgc.myfirebase.modelo.Pessoa;

public class Pesquisa extends AppCompatActivity {

    //Componentes da tela arquivo.xml
    private EditText editPalavra;
    private ListView listVPesquisa;

    //Objetos de conexão com o Banco Firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    //Objetos de manipulação da List View
    private List<Pessoa> listPessoa = new ArrayList<Pessoa>();
    private ArrayAdapter<Pessoa> arrayAdapterPessoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa);

        inicializarComponentes();// 1º passo - inicializar os componentes da tela e referencia-los
        inicializarFirebase();//2º passo - inicializar a conexão e pegar a referencia do Banco Firebase
        eventoEdit();// 3º passo - implementos os eventos que serão utilizando durante a aplicação
    }

    private void eventoEdit() {
        editPalavra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) { // apos digitar
                String palavra = editPalavra.getText().toString().trim(); // armaze o valor do editText
                pesquisarPalavra(palavra); // 4º passo - enviar a palavra para ser pesquisada no banco
            }
        });
    }

    private void pesquisarPalavra(String palavra) {
        Query query; // 5º passo - cria o objeto que ira receber os dados da pesquisa
        if (palavra.equals("")){// verifica o conteudo da variavel
            // caso a palavra esteja vazia, sejá exibido todos os objetos da ramificação Pessoa
            // orderByChil("nome") indica qual 'chave' sejá usa-da para ordenar os objetos
            // obs: letra maiuscula vem antes de minuscula na pesquisa
            query = databaseReference.child("Pessoa").orderByChild("nome");
        }else{
            /// caso palavra tenha algum valor
            // trazermos todos os objetos de formar que o primeiro a ser buscado no banco
            // é exatamente iqual a palavra enviada - starAt
            // e os seguintes sera a palavra acrescida de qualquer outro valore de string - endAt
            // a string  "\uf8ff" indica que quero qualquer valor apos a palavra inicial
            query = databaseReference.child("Pessoa")
                    .orderByChild("nome").startAt(palavra).endAt(palavra+"\uf8ff");
        }

        // limpa a list<Pessoa>
        listPessoa.clear();

        // implemento o metodo na query
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){// separo cada objeto contido na dataSnapshot
                    Pessoa p = objSnapshot.getValue(Pessoa.class);// salvo cada um deles na variavel pessoa;
                    listPessoa.add(p);// adiciono na list<Pessoa>
                }

                // inicializo o arrayAdapter passando o contexto da aplicação
                // a tipo de layout da lista, e a list<Pessoa> contendo os objetos
                arrayAdapterPessoa = new ArrayAdapter<Pessoa>(Pesquisa.this,
                        android.R.layout.simple_list_item_1,listPessoa);

                // incluo na ListView o arrayAdapter
                listVPesquisa.setAdapter(arrayAdapterPessoa);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(Pesquisa.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void inicializarComponentes() {
        editPalavra = (EditText) findViewById(R.id.editPalavra);
        listVPesquisa = (ListView) findViewById(R.id.listVPesquisa);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ultimo passo - assim que mostra a tela quero que seja exibido todos os dados contidos no Banco
        pesquisarPalavra("");
    }
}
