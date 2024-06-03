package br.com.apkdoandroid.blocoroom.domain.repository

import br.com.apkdoandroid.blocoroom.data.entities.Anotacao
import br.com.apkdoandroid.blocoroom.data.entities.relacionamentos.AnotacaoECategoria
import br.com.apkdoandroid.blocoroom.data.repository.ResultadoOperacao

interface AnotacaoRepository {
    suspend fun salvar( anotacao: Anotacao ) : ResultadoOperacao
    suspend fun atualizar( anotacao: Anotacao ) : ResultadoOperacao
    suspend fun remover( anotacao: Anotacao ) : ResultadoOperacao
    suspend fun listarAnotacaoECategoria() : List<AnotacaoECategoria>
    suspend fun pesquisarAnotacaoECategoria(texto: String) : List<AnotacaoECategoria>

}