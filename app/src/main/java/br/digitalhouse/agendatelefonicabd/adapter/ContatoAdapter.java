package br.digitalhouse.agendatelefonicabd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.digitalhouse.agendatelefonicabd.R;
import br.digitalhouse.agendatelefonicabd.interfaces.ContatoListener;
import br.digitalhouse.agendatelefonicabd.model.Contato;

public class ContatoAdapter extends RecyclerView.Adapter<ContatoAdapter.ViewHolder> {

    private List<Contato> listaContatos;
    private ContatoListener listener;

    public ContatoAdapter(List<Contato> listaContatos, ContatoListener listener) {
        this.listaContatos = listaContatos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_contatos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Contato contato = listaContatos.get(position);
        holder.onBind(contato);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.clickContato(contato);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaContatos.size();
    }

    public void atualizaLista(List<Contato> novaListaContatos){
        this.listaContatos.clear();
        this.listaContatos = novaListaContatos;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nome;
        private TextView telefone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.textInputLayoutNome);
            telefone = itemView.findViewById(R.id.textInputLayoutTelefone);
        }

        public void onBind(Contato contato) {
            nome.setText(contato.getNome());
            telefone.setText(contato.getTelefone());
        }
    }
}
