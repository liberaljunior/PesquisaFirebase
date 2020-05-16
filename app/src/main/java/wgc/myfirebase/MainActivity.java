package wgc.myfirebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import wgc.myfirebase.modelo.Pessoa;

public class MainActivity extends AppCompatActivity {
    EditText edtNome, edtEmail;
    ListView listV_dados;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<Pessoa> listPessoa = new ArrayList<Pessoa>();
    private ArrayAdapter<Pessoa> arrayAdapterPessoa;

    Pessoa pessoaSelecionada = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtEmail = (EditText)findViewById(R.id.editEmail);
        edtNome = (EditText)findViewById(R.id.editNome);
        listV_dados = (ListView)findViewById(R.id.listV_dados);

        inicializarFirebase();
        eventoDatabase();

        listV_dados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pessoaSelecionada = (Pessoa)parent.getItemAtPosition(position);
                edtNome.setText(pessoaSelecionada.getNome());
                edtEmail.setText(pessoaSelecionada.getEmail());
            }
        });

    }

    private void eventoDatabase() {
        databaseReference.child("Pessoa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listPessoa.clear();
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    Pessoa p = objSnapshot.getValue(Pessoa.class);
                    listPessoa.add(p);
                }
                arrayAdapterPessoa = new ArrayAdapter<Pessoa>(MainActivity.this,
                        android.R.layout.simple_list_item_1,listPessoa);
                listV_dados.setAdapter(arrayAdapterPessoa);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_novo){
            salvaPessoa();
        }else if(id == R.id.menu_atualiza){
            if(pessoaSelecionada != null){
                atualizaPessoa();
            }else{
                alert("Selecione um registro para atualizar");
            }
        }else if (id == R.id.menu_deleta){
            if(pessoaSelecionada != null){
                deletaPessoa();
            }else{
                alert("Selecione um registro para deletar");
            }
        }else if (id == R.id.menu_pesquisa){//verifica que o a lupa foi clicada
            Intent i = new Intent(MainActivity.this,Pesquisa.class); // cria nova intent relacionado o Activity de pesquisa
            startActivity(i);// starta a activity criada
        }

        return true;
    }

    private void limparCampos() {
        edtEmail.setText("");
        edtNome.setText("");
        pessoaSelecionada = null;
    }

    private void alert(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    private void deletaPessoa(){
        Pessoa p = new Pessoa();
        p.setUid(pessoaSelecionada.getUid());
        databaseReference.child("Pessoa").child(p.getUid()).removeValue();
        limparCampos();
        alert("Registro deletado com sucesso");
    }

    private void atualizaPessoa(){
        Pessoa p = new Pessoa();
        p.setUid(pessoaSelecionada.getUid());
        p.setNome(edtNome.getText().toString().trim());
        p.setEmail(edtEmail.getText().toString().trim());
        databaseReference.child("Pessoa").child(p.getUid()).setValue(p);
        limparCampos();
        alert("Registro atualizado com sucesso");
    }

    private void salvaPessoa(){
        Pessoa p = new Pessoa();
        p.setUid(UUID.randomUUID().toString());
        p.setNome(edtNome.getText().toString());
        p.setEmail(edtEmail.getText().toString());
        databaseReference.child("Pessoa").child(p.getUid()).setValue(p);
        limparCampos();
    }
}
