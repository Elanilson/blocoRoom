package br.com.apkdoandroid.blocoroom.presentation.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.LauncherActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import br.com.apkdoandroid.blocoroom.R
import br.com.apkdoandroid.blocoroom.databinding.ActivityContaBinding
import br.com.apkdoandroid.blocoroom.helper.exportarParaJson
import br.com.apkdoandroid.blocoroom.helper.importarDeJson
import br.com.apkdoandroid.blocoroom.presentation.model.Resposta
import br.com.apkdoandroid.blocoroom.presentation.viewmodel.ContaViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ContaActivity : AppCompatActivity() {
    private val binding by lazy { ActivityContaBinding.inflate(layoutInflater) }
    private val viewmodel : ContaViewModel by viewModels()
    private lateinit var laucherLogin : ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        lauchers()
        onclicks()
        observers()
    }


    private fun observers() {
        viewmodel.intentGoogle.observe(this){
            laucherLogin.launch(it)
        }

        viewmodel.usuarioLogado.observe(this){resposta ->
            if(resposta.status){
                val conta = resposta.googleSignInAccount

                    binding.textViewEmail.text = "${conta?.email}"
                    binding.layoutLogin.visibility = View.GONE
                    binding.layout.visibility = View.VISIBLE
                    binding.linearLayout.visibility = View.VISIBLE
                    binding.buttonLogout.visibility = View.VISIBLE


            }else{

                resposta.mensagem?.let {mensagem ->
                    Log.d("MyDrive", mensagem)
                  //  Toast.makeText(applicationContext, mensagem, Toast.LENGTH_SHORT).show()
                }

            }
        }

        viewmodel.upload.observe(this){resposta ->
            if(resposta.status){
                Log.d("MyDrive", "*Upload com sucesso")

            }else{
                resposta.mensagem?.let {mensagem ->
                    Log.d("MyDrive", mensagem)
                    Toast.makeText(applicationContext, mensagem, Toast.LENGTH_SHORT).show()
                }

            }
        }
        viewmodel.download.observe(this){resposta ->
            if(resposta.status){
                binding.progressBar.visibility = View.GONE
                Log.d("MyDrive", "*Download com sucesso")
                viewmodel.importarBancoDados(applicationContext)
            }else{
                resposta.mensagem?.let {mensagem ->
                    Log.d("MyDrive", mensagem)
                    Toast.makeText(applicationContext, mensagem, Toast.LENGTH_SHORT).show()
                }

            }
        }

        viewmodel.importarBancoDados.observe(this){resposta ->
            if(resposta.status){
                Log.d("MyDrive", "*importado com sucesso")
                Toast.makeText(applicationContext, "Banco local sincronizado", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                resposta.mensagem?.let {mensagem ->
                    Log.d("MyDrive", mensagem)
                    Toast.makeText(applicationContext, mensagem, Toast.LENGTH_SHORT).show()
                }

            }
        }

        viewmodel.lagout.observe(this){resposta ->
            if(resposta.status){
                Log.d("MyDrive", "Deslogado com sucesso")
                finish()
            }else{
                resposta.mensagem?.let {mensagem ->
                    Log.d("MyDrive", mensagem)
                  //  Toast.makeText(applicationContext, mensagem, Toast.LENGTH_SHORT).show()
                }

            }
        }

       /* viewmodel.sincronizar.observe(this){
            Toast.makeText(applicationContext, "Sincronizando..", Toast.LENGTH_SHORT).show()
            finish()
        }*/
    }
    private fun onclicks() {
        binding.buttonLogin.setOnClickListener { viewmodel.login() }
        binding.buttonLogout.setOnClickListener { viewmodel.deslogar() }
        binding.buttonSincronizar.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE

            var file = File(applicationContext.getExternalFilesDir(null), "backup.json")
            viewmodel.download("anotacoes","backup.json",file)
           // viewmodel.importarBancoDados(applicationContext)

        }
    }


    private fun lauchers(){
         laucherLogin = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if(result.resultCode == Activity.RESULT_OK){
                val data : Intent? = result.data
                try{
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    Log.d("MyDrive", "Login com sucesso")
                    binding.textViewEmail.text = "${task?.getResult()?.email}"
                    binding.layoutLogin.visibility = View.GONE
                    binding.layout.visibility = View.VISIBLE
                    binding.linearLayout.visibility = View.VISIBLE
                    binding.buttonLogout.visibility = View.VISIBLE
                    Toast.makeText(applicationContext, "Login com sucesso", Toast.LENGTH_SHORT).show()
                    finish()
                }catch (e :Exception){
                    Log.d("MyDrive", "laucher ${e.message}")
                }
            }else if(result.resultCode == Activity.RESULT_CANCELED){
                Log.d("MyDrive", "Tentativa de login cancelada")
            }
        }
    }

    private fun checkForGooglePermissions() {
        if(!GoogleSignIn.hasPermissions(
                GoogleSignIn.getLastSignedInAccount(this),
                Scope(Scopes.DRIVE_FILE),
                Scope(Scopes.EMAIL)
            )){
            GoogleSignIn.requestPermissions(
                this,
                1,
                GoogleSignIn.getLastSignedInAccount(this),
                Scope(Scopes.DRIVE_FILE),
                Scope(Scopes.EMAIL)
            )
        }else{
            // lifecycle.coroutineScope.launch { driveSetUp()  }
        }
    }

    override fun onStart() {
        super.onStart()
        viewmodel.verificarUsuarioLogdo(applicationContext)
     /*   CoroutineScope(Dispatchers.IO).launch {
           // var file =  exportarParaJson(applicationContext)
            var file = File(applicationContext.getExternalFilesDir(null), "backup.json")

            if(file != null){
              // file.let {   viewmodel.upload(it!!,"application/json","anotacoes")}
                file.let {   viewmodel.download("anotacoes","backup.json",file)}
                Log.d("MyDrive", "File valido")
            }else{
                Log.d("MyDrive", "File ${file}")
            }
        }*/

    }
}