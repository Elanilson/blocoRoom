package br.com.apkdoandroid.blocoroom.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.apkdoandroid.blocoroom.data.entities.Anotacao
import br.com.apkdoandroid.blocoroom.data.entities.relacionamentos.AnotacaoECategoria
import br.com.apkdoandroid.blocoroom.domain.repository.AnotacaoRepository
import br.com.apkdoandroid.blocoroom.data.repository.ResultadoOperacao
import br.com.apkdoandroid.blocoroom.presentation.model.Resposta
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnotacaoViewModel @Inject constructor(
    private val anotacaoRepository: AnotacaoRepository
) : ViewModel() {

    private val _resultadoOperacao = MutableLiveData<ResultadoOperacao>()
    val resultadoOperacao: LiveData<ResultadoOperacao>
        get() = _resultadoOperacao

    private val _listaAnotacoesECategoria = MutableLiveData<List<AnotacaoECategoria>>()
    val listaAnotacoesECategoria: LiveData<List<AnotacaoECategoria>>
        get() = _listaAnotacoesECategoria


    private val _ImportarBancoDados  =  MutableLiveData<Resposta>()
    val importarBancoDados : LiveData<Resposta> = _ImportarBancoDados

    private val _ExportarBancoDados  =  MutableLiveData<Resposta>()
    val exportarBancoDados : LiveData<Resposta> = _ExportarBancoDados

    fun salvar( anotacao: Anotacao){
        if( validarDadosAnotaoca( anotacao ) ){
            viewModelScope.launch( Dispatchers.IO ) {
                val resultadoOperacao = anotacaoRepository.salvar( anotacao )
                _resultadoOperacao.postValue( resultadoOperacao )
            }
        }
    }


    fun atualizar( anotacao: Anotacao ){
        if( validarDadosAnotaoca( anotacao ) ){
            viewModelScope.launch( Dispatchers.IO ) {
                val resultadoOperacao = anotacaoRepository.atualizar( anotacao )
                _resultadoOperacao.postValue( resultadoOperacao )
            }
        }
    }

    fun remover( anotacao: Anotacao ){
        viewModelScope.launch( Dispatchers.IO ) {
            val resultadoOperacao = anotacaoRepository.remover( anotacao )
            _resultadoOperacao.postValue( resultadoOperacao )
        }
    }

    fun listarAnotacaoECategoria(){
        viewModelScope.launch( Dispatchers.IO ) {
            val lista = anotacaoRepository.listarAnotacaoECategoria()
            _listaAnotacoesECategoria.postValue( lista )
        }
    }

    fun pesquisarAnotacaoECategoria( texto: String ){
        viewModelScope.launch( Dispatchers.IO ) {
            val lista = anotacaoRepository.pesquisarAnotacaoECategoria(texto)
            _listaAnotacoesECategoria.postValue( lista )
        }
    }

    private fun validarDadosAnotaoca(anotacao: Anotacao ) : Boolean {

        if( anotacao.titulo.isEmpty() ){
            _resultadoOperacao.value = ResultadoOperacao(
                false, "Preencha o título da anotação!"
            )
            return false
        }

        if( anotacao.idCategoria <= 0 ){//> 0
            _resultadoOperacao.value = ResultadoOperacao(
                false, "Preencha a categoria da anotação!"
            )
            return false
        }

        if( anotacao.descricao.isEmpty() ){
            _resultadoOperacao.value = ResultadoOperacao(
                false, "Preencha a descrição da anotação!"
            )
            return false
        }

        return true
    }


}