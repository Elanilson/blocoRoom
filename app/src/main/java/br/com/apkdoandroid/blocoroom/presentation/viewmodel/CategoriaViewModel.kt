package br.com.apkdoandroid.blocoroom.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.apkdoandroid.blocoroom.data.entities.Categoria
import br.com.apkdoandroid.blocoroom.domain.repository.CategoriaRepository
import br.com.apkdoandroid.blocoroom.data.repository.ResultadoOperacao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriaViewModel @Inject constructor(
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    private val _resultadoOperacao = MutableLiveData<ResultadoOperacao>()
    val resultadoOperacao: LiveData<ResultadoOperacao>
        get() = _resultadoOperacao

    private val _listaCategorias = MutableLiveData<List<Categoria>>()
    val listaCategorias: LiveData<List<Categoria>>
        get() = _listaCategorias

    fun salvar( categoria: Categoria ){
        viewModelScope.launch( Dispatchers.IO ) {
            val resultadoOperacao = categoriaRepository.salvar( categoria )
            _resultadoOperacao.postValue( resultadoOperacao )
        }
    }

    fun listar(){
        viewModelScope.launch( Dispatchers.IO ) {
            val lista = categoriaRepository.listar()
            _listaCategorias.postValue( lista )
        }
    }

}