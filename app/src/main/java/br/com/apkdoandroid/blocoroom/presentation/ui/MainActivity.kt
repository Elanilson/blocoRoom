package br.com.apkdoandroid.blocoroom.presentation.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import br.com.apkdoandroid.blocoroom.R
import br.com.apkdoandroid.blocoroom.data.database.BancoDados
import br.com.apkdoandroid.blocoroom.data.entities.Anotacao
import br.com.apkdoandroid.blocoroom.data.entities.Categoria
import br.com.apkdoandroid.blocoroom.databinding.ActivityMainBinding
import br.com.apkdoandroid.blocoroom.helper.exportarParaJson
import br.com.apkdoandroid.blocoroom.helper.importarDeJson
import br.com.apkdoandroid.blocoroom.presentation.ui.adapter.AnotacaoAdapter
import br.com.apkdoandroid.blocoroom.presentation.viewmodel.AnotacaoViewModel
import br.com.apkdoandroid.blocoroom.presentation.viewmodel.CategoriaViewModel
import br.com.apkdoandroid.blocoroom.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate( layoutInflater )
    }
    private lateinit var anotacaoAdapter: AnotacaoAdapter
    private val anotacaoViewModel: AnotacaoViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )
        inicializarUI()
        inicializarEventosClique()
        inicializarObservables()

    }

    private fun inicializarObservables() {

        anotacaoViewModel.resultadoOperacao.observe(this){ resultado ->
            if( resultado.sucesso ){
                Toast.makeText(this, resultado.mensagem, Toast.LENGTH_SHORT).show()
                anotacaoViewModel.listarAnotacaoECategoria()
            }else{
                Toast.makeText(this, resultado.mensagem, Toast.LENGTH_SHORT).show()
            }
        }

        anotacaoViewModel
            .listaAnotacoesECategoria
            .observe(this){ listaAnotacaoECategoria ->
                anotacaoAdapter.configurarLista( listaAnotacaoECategoria )
            }

        mainViewModel.importarBancoDados.observe(this){

            if(it.status){
                anotacaoViewModel.listarAnotacaoECategoria()
                Toast.makeText(applicationContext, "Sucesso na importação!", Toast.LENGTH_SHORT).show()
            }else{
                it.mensagem?.let { Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show() }
            }

        }

        mainViewModel.exportarBancoDados.observe(this){


            if(it.status){
                Log.i("MyDrive", "Sucesso na exportação!")
                mainViewModel.verificarUsuarioLogdo(applicationContext)
                Toast.makeText(applicationContext, "Sucesso na exportação!", Toast.LENGTH_SHORT).show()
            }else{
                it.mensagem?.let { Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show() }
            }

        }

        mainViewModel.usuarioLogado.observe(this){

                Log.i("MyDrive", "usuarioLogado: ${it.status}")
            if(it.status){
                var file = java.io.File(applicationContext.getExternalFilesDir(null), "backup.json")
                file?.let { mainViewModel.upload(it,"application/json","anotacoes") }

            }else{
                it.mensagem?.let { Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show() }
            }

        }

        mainViewModel.upload.observe(this){

            if(it.status){
                Toast.makeText(applicationContext, "Banco sincronizado!", Toast.LENGTH_SHORT).show()

            }else{
                it.mensagem?.let { Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show() }
            }

        }

    }

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()
        anotacaoViewModel.listarAnotacaoECategoria()
    }

    private fun inicializarUI() {

        with(binding){

            val onClickRemover = { anotacao: Anotacao ->
                anotacaoViewModel.remover( anotacao )
            }
            val onClickAtualizar = { anotacao: Anotacao ->
                val intent = Intent(applicationContext, CadastroAnotacaoActivity::class.java)
                intent.putExtra("anotacao", anotacao)
                startActivity( intent )
            }

            anotacaoAdapter = AnotacaoAdapter(
                onClickRemover, onClickAtualizar
            )
            rvAnotacoes.adapter = anotacaoAdapter
            rvAnotacoes.layoutManager = StaggeredGridLayoutManager(
                2, LinearLayoutManager.VERTICAL
            )

        }
        inicializarBarraNavegacao()

    }

    private fun inicializarEventosClique() {

        binding.fabAdicionar.setOnClickListener {
            startActivity(
                Intent(this, CadastroAnotacaoActivity::class.java)
            )
            //exportarParaJson(applicationContext)
            //importarDeJson(applicationContext)
        }

    }

    private fun inicializarBarraNavegacao() {

        addMenuProvider( object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_principal, menu)
                val itemPesquisa = menu.findItem(R.id.item_pesquisa)
                val searchView = itemPesquisa.actionView as SearchView

                searchView.queryHint = "Digite algo para pesquisar"
                /*searchView.setOnCloseListener {
                    Log.i("pesquisa_search", "Saiu do SearchView")
                    true
                }*/
                searchView.setOnQueryTextListener( object : OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        Log.i("pesquisa_search", "onQueryTextSubmit: $query")
                        return true
                    }

                    override fun onQueryTextChange(texto: String?): Boolean {

                        if( texto != null )
                            anotacaoViewModel.pesquisarAnotacaoECategoria( texto )

                        return true
                    }

                })

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when( menuItem.itemId ){
                    R.id.item_pesquisa -> {
                        //Código
                        true
                    }
                    R.id.item_conta ->{
                        startActivity(Intent(this@MainActivity, ContaActivity::class.java))
                        true
                    }
                    R.id.item_importar ->{
                        mainViewModel.importarBancoDados(applicationContext)
                        true
                    }
                    R.id.item_exportar ->{
                        Log.i("MyDrive", "exportado: menu")
                        mainViewModel.exportarBancoDados(applicationContext)
                        true
                    }
                    else -> true
                }
            }

        })

    }
}