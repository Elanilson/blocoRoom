package br.com.apkdoandroid.blocoroom.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider

import br.com.apkdoandroid.blocoroom.data.database.BancoDados
import br.com.apkdoandroid.blocoroom.data.entities.Anotacao
import br.com.apkdoandroid.blocoroom.data.entities.Categoria
import com.google.common.truth.Truth.assertThat


import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runners.Parameterized

class AnotacaoDAOTest {

    private lateinit var bancoDados: BancoDados
    private lateinit var categoriaDAO: CategoriaDAO
    private lateinit var anotacaoDAO: AnotacaoDAO

    @Before
    fun setUp() {
        bancoDados = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            BancoDados::class.java
        ).allowMainThreadQueries().build()
        categoriaDAO = bancoDados.categoriaDAO
        anotacaoDAO = bancoDados.anotacaoDAO
    }

    @Test
    fun salvarCategoria_verificaCategoriaCadastrada_retornaTrue() {
        val categoria = Categoria(0, "mercado")
        val idCategoria = categoriaDAO.insert( categoria )
        assertThat( idCategoria ).isGreaterThan( 0L )
    }

    @Test
    fun salvarAnotacao_verificaAnotacaoCadastrada_retornaTrue() {
        salvarCategoria_verificaCategoriaCadastrada_retornaTrue()
        val anotacao = Anotacao(
            0, 1, "Titulo","descricao"
        )
        val idAnotacao = anotacaoDAO.salvar( anotacao )
        assertThat( idAnotacao ).isGreaterThan( 0L )
    }

    @Test
    fun listarAnotacao_verificaListagemAnotacoes_retornaLista() {
        salvarAnotacao_verificaAnotacaoCadastrada_retornaTrue()
        val listaAnotacoes = anotacaoDAO.listar()
        assertThat( listaAnotacoes ).isNotEmpty()
    }

    @After
    fun tearDown() {
        bancoDados.close()
    }
}