package br.com.apkdoandroid.blocoroom.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.apkdoandroid.blocoroom.data.entities.Anotacao
import androidx.recyclerview.widget.RecyclerView.Adapter
import br.com.apkdoandroid.blocoroom.R
import br.com.apkdoandroid.blocoroom.data.entities.relacionamentos.AnotacaoECategoria
import br.com.apkdoandroid.blocoroom.databinding.ItemAnotacaoBinding
import kotlin.random.Random

class AnotacaoAdapter(
    private val onClickRemover: (Anotacao) -> Unit,
    private val onClickAtualizar: (Anotacao) -> Unit
) : Adapter<AnotacaoAdapter.AnotacaoViewHolder>() {

    private var listaAnotacoesCategoria = listOf<AnotacaoECategoria>()

    fun configurarLista( lista: List<AnotacaoECategoria> ){
        listaAnotacoesCategoria = lista
        notifyDataSetChanged()
    }

    inner class AnotacaoViewHolder(
        private val binding: ItemAnotacaoBinding
    ) : RecyclerView.ViewHolder( binding.root ){
        fun bind( anotacaoECategoria: AnotacaoECategoria ){

            val anotacao = anotacaoECategoria.anotacao
            binding.textTituloAnotacao.text = anotacao.titulo
            binding.textDescricaoAnotacao.text = anotacao.descricao
            binding.textCategoriaAnotacao.text = anotacaoECategoria.categoria.nome

            binding.cardItem.setCardBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    gerarCorAleatoria()
                )
            )

            binding.btnRemoverAnotacao.setOnClickListener {
                onClickRemover( anotacao )
            }

            binding.cardItem.setOnClickListener {
                onClickAtualizar( anotacao )
            }

        }
    }

    private fun gerarCorAleatoria(): Int {
        val listaCores = listOf(
            R.color.laranja, R.color.roxo, R.color.azul,
            R.color.rosa, R.color.amarelo, R.color.bege
        )
        val numeroAleatorio =  Random.nextInt( listaCores.size )
        return listaCores[ numeroAleatorio ]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnotacaoViewHolder {
        val itemView = ItemAnotacaoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AnotacaoViewHolder( itemView )
    }

    override fun getItemCount(): Int {
        return listaAnotacoesCategoria.size
    }

    override fun onBindViewHolder(holder: AnotacaoViewHolder, position: Int) {
        val anotacaoECategoria = listaAnotacoesCategoria[position]
        holder.bind( anotacaoECategoria )
    }

}