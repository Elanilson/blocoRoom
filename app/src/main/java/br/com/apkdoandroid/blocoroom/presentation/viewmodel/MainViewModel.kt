package br.com.apkdoandroid.blocoroom.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.apkdoandroid.blocoroom.domain.repository.MainRepository
import br.com.apkdoandroid.blocoroom.helper.Resource
import br.com.apkdoandroid.blocoroom.presentation.model.Resposta
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _ImportarBancoDados  =  MutableLiveData<Resposta>()
    val importarBancoDados : LiveData<Resposta> = _ImportarBancoDados

    private val _ExportarBancoDados  =  MutableLiveData<Resposta>()
    val exportarBancoDados : LiveData<Resposta> = _ExportarBancoDados

    private val _UsuarioLogado  =  MutableLiveData<Resposta>()
    val usuarioLogado : LiveData<Resposta> = _UsuarioLogado

    private val _Upload  =  MutableLiveData<Resposta>()
    val upload : LiveData<Resposta> = _Upload

    fun verificarUsuarioLogdo(context: Context) {

        viewModelScope.launch {
            val resource = mainRepository.verificarUsuarioLogdo(context)

            when(resource){
                is Resource.Success -> {
                    resource.data?.let {data ->
                        _UsuarioLogado.value = Resposta(data,true)
                    }

                }
                is Resource.Error ->{
                    resource.message?.let { message ->
                        _UsuarioLogado.value = Resposta(message)
                    }

                }
            }

        }

    }

    fun upload(localFile: java.io.File, mimeType: String?, folderId: String?) {

        viewModelScope.launch {

            val resource = mainRepository.upload(localFile, mimeType, folderId)

            when(resource){
                is Resource.Success -> {
                    resource.data?.let {data ->
                        _Upload.value = Resposta(true)
                    }

                }
                is Resource.Error ->{
                    resource.message?.let { message ->
                        _Upload.value = Resposta(message)
                    }

                }
            }

        }
    }



    fun importarBancoDados(context: Context){

        viewModelScope.launch {
            val resource = mainRepository.importarBancoDados(context)

            when(resource){
                is Resource.Success -> {
                    resource.data?.let {data ->
                        _ImportarBancoDados.value = Resposta(true)
                    }

                }
                is Resource.Error ->{
                    resource.message?.let { message ->
                        _ImportarBancoDados.value = Resposta(message)
                    }

                }
            }

        }
    }

    fun exportarBancoDados(context: Context){

        viewModelScope.launch {
            val resource = mainRepository.exportarBancoDados(context)

            when(resource){
                is Resource.Success -> {
                    resource.data?.let {data ->
                        _ExportarBancoDados.value = Resposta(true)
                    }

                }
                is Resource.Error ->{
                    resource.message?.let { message ->
                        _ExportarBancoDados.value = Resposta(message)
                    }

                }
            }

        }
    }

}