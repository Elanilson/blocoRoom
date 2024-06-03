package br.com.apkdoandroid.blocoroom.presentation.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.apkdoandroid.blocoroom.domain.repository.ContaGoogleRepository
import br.com.apkdoandroid.blocoroom.helper.Resource
import br.com.apkdoandroid.blocoroom.presentation.model.Resposta
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class ContaViewModel @Inject constructor(private val contaGoogleRepository: ContaGoogleRepository) : ViewModel(){

    private val _IntentGoogle  =  MutableLiveData<Intent>()
    val intentGoogle : LiveData<Intent> = _IntentGoogle

    private val _GoogleSignInAccount  =  MutableLiveData< GoogleSignInAccount?>()
    val googleSignInAccount : LiveData< GoogleSignInAccount?> = _GoogleSignInAccount

    private val _Sincronizar  =  MutableLiveData<Boolean>() // é só para simular
    val sincronizar : LiveData<Boolean> = _Sincronizar

    private val _Lagout  =  MutableLiveData<Resposta>()
    val lagout : LiveData<Resposta> = _Lagout

   // private val _UsuarioLogado  =  MutableLiveData<Resposta>()
    //val usuarioLogado : LiveData<Resposta> = _UsuarioLogado

    private val _Upload  =  MutableLiveData<Resposta>()
    val upload : LiveData<Resposta> = _Upload

    private val _Download  =  MutableLiveData<Resposta>()
    val download : LiveData<Resposta> = _Download

    private val _UsuarioLogado  =  MutableLiveData<Resposta>()
    val usuarioLogado : LiveData<Resposta> = _UsuarioLogado

    private val _ImportarBancoDados  =  MutableLiveData<Resposta>()
    val importarBancoDados : LiveData<Resposta> = _ImportarBancoDados



    fun login() {
        viewModelScope.launch(Dispatchers.Main){
            _IntentGoogle.value = contaGoogleRepository.login()
        }
    }

    fun deslogar() {

        viewModelScope.launch {

            val resource = contaGoogleRepository.logout()

            when(resource){
                is Resource.Success -> {
                    resource.data?.let {data ->
                        _Lagout.value = Resposta(true)
                    }

                }
                is Resource.Error ->{
                    resource.message?.let { message ->
                        _Lagout.value = Resposta(message)
                    }

                }
            }

        }

    }

    fun verificarUsuarioLogdo(context: Context) {

        viewModelScope.launch {
            val resource = contaGoogleRepository.verificarUsuarioLogdo(context)

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

            val resource = contaGoogleRepository.upload(localFile, mimeType, folderId)

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

     fun download(folderName: String, fileName: String, targetFile: java.io.File){

        viewModelScope.launch {
            val resource = contaGoogleRepository.download(folderName, fileName, targetFile)

            when(resource){
                is Resource.Success -> {
                    resource.data?.let {data ->
                        _Download.value = Resposta(true)
                    }

                }
                is Resource.Error ->{
                    resource.message?.let { message ->
                        _Download.value = Resposta(message)
                    }

                }
            }

        }
    }

    fun importarBancoDados(context: Context){

        viewModelScope.launch {
            val resource = contaGoogleRepository.importarBancoDados(context)

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

  /*   fun sincronizar(context: Context,file: java.io.File) {
         CoroutineScope(Dispatchers.IO).launch {
            downloadBackupFileIfExists("anotacoes","backup.json",file)
            importarDeJson(context)
            exportarParaJson(context)
            upload(file,"application/json","anotacoes")
            _Sincronizar.value = true //simulando
        }*/


    //}


}